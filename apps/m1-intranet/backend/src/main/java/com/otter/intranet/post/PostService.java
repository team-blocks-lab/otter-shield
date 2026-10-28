package com.otter.intranet.post;

import com.otter.intranet.employee.Employee;
import com.otter.intranet.employee.EmployeeRepository;
import com.otter.intranet.image.ImageService;
import com.otter.intranet.post.dto.PostCreateRequest;
import com.otter.intranet.post.dto.PostFeedResponse;
import com.otter.intranet.post.dto.PostResponse;
import com.otter.intranet.post.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final EmployeeRepository employeeRepository;
    private final PostRepository postRepository;
    private final ImageService imageService;

    public PostFeedResponse getFeed(
            Long currentEmployeeId,
            int page,
            int size
    ) {
        requireReadyEmployee(currentEmployeeId);

        // 음수 페이지 및 과도한 일괄 조회 차단
        if (page < 0 || size < 1 || size > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "페이지는 0 이상, 조회 개수는 1~50으로 입력해 주세요."
            );
        }

        // 생성일 기준 최신순 정렬 및 동일 시각 게시글의 순서 고정
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("postId")
                )
        );

        Page<Post> result = postRepository.findAll(pageable);

        // 트랜잭션 범위 안에서 작성자 조회 및 응답 DTO 변환
        return new PostFeedResponse(
                result.getContent().stream()
                        .map(post -> toResponse(post, currentEmployeeId))
                        .toList(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    @Transactional
    public Long createPost(
            Long currentEmployeeId,
            PostCreateRequest request
    ) {
        // 기존 JSON 등록 요청과 사진 미첨부 등록 방식 유지
        return createPost(currentEmployeeId, request, null);
    }

    @Transactional
    public Long createPost(
            Long currentEmployeeId,
            PostCreateRequest request,
            MultipartFile image
    ) {
        // 클라이언트 입력 대신 로그인 직원으로 작성자 지정
        Employee author = requireReadyEmployee(currentEmployeeId);

        // 다른 게시글의 사진 연결 및 임의 파일 경로 지정 차단
        if (request.imageKey() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "사진 저장 이름은 직접 지정할 수 없습니다."
            );
        }

        // 파일 저장 전 게시글 입력값 검증
        // 유효하지 않은 게시글로 인한 불필요한 파일 생성 방지
        Post post = new Post(
                author,
                request.category(),
                request.title(),
                request.content(),
                request.anonymous(),
                null
        );

        if (image != null && !image.isEmpty()) {
            requireActiveTransaction();

            String imageKey = imageService.save(image);

            // DB 저장 실패 또는 커밋 실패 시 신규 사진 정리
            registerImageCleanupOnRollback(imageKey);

            post = new Post(
                    author,
                    request.category(),
                    request.title(),
                    request.content(),
                    request.anonymous(),
                    imageKey
            );
        }

        return postRepository.save(post).getPostId();
    }

    public Resource getPostImage(
            Long currentEmployeeId,
            Long postId
    ) {
        requireReadyEmployee(currentEmployeeId);

        Post post = findPost(postId);

        if (post.getImageKey() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "첨부된 사진이 없습니다."
            );
        }

        // 게시글에 연결된 저장 이름을 통한 사진 조회
        // 클라이언트의 파일 경로 직접 지정 방지
        return imageService.load(post.getImageKey());
    }

    @Transactional
    public void updatePost(
            Long currentEmployeeId,
            Long postId,
            PostUpdateRequest request
    ) {
        requireReadyEmployee(currentEmployeeId);

        Post post = findPost(postId);
        requireAuthor(post, currentEmployeeId);

        // 작성자 확인 후 변경 가능한 필드만 수정
        // 기존 사진 유지 및 엔티티 변경 감지를 통한 DB 반영
        post.update(
                request.category(),
                request.title(),
                request.content(),
                request.anonymous()
        );
    }

    @Transactional
    public void deletePost(
            Long currentEmployeeId,
            Long postId
    ) {
        requireReadyEmployee(currentEmployeeId);

        Post post = findPost(postId);
        requireAuthor(post, currentEmployeeId);

        String imageKey = post.getImageKey();

        if (imageKey != null) {
            requireActiveTransaction();
            registerImageCleanupAfterCommit(imageKey);
        }

        // DB 삭제 확정 이후 사진 삭제를 위한 게시글 삭제 요청
        postRepository.delete(post);
    }

    private void registerImageCleanupOnRollback(String imageKey) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            deleteImageSafely(imageKey);
                        }
                    }
                }
        );
    }

    private void registerImageCleanupAfterCommit(String imageKey) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        deleteImageSafely(imageKey);
                    }
                }
        );
    }

    private void deleteImageSafely(String imageKey) {
        try {
            imageService.delete(imageKey);
        } catch (RuntimeException exception) {
            // DB 처리 결과와 파일 정리 실패의 분리
            // 잔여 파일 확인을 위한 저장 이름 및 오류 기록
            log.error(
                    "사진 파일 정리 실패: imageKey={}",
                    imageKey,
                    exception
            );
        }
    }

    private void requireActiveTransaction() {
        // 파일 정리 시점을 DB 처리 결과에 연결하기 위한 트랜잭션 확인
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "사진 처리는 활성 트랜잭션 안에서 실행해야 합니다."
            );
        }
    }

    private Employee requireReadyEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "로그인이 필요합니다."
                ));

        // 서비스 직접 호출 시에도 최초 설정 조건 유지
        if (employee.isFirstLogin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "비밀번호와 닉네임을 먼저 설정해 주세요."
            );
        }

        return employee;
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다."
                ));
    }

    private void requireAuthor(Post post, Long employeeId) {
        // 익명 표시와 무관한 실제 작성자의 수정·삭제 권한 확인
        if (!post.isWrittenBy(employeeId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인이 작성한 게시글만 수정·삭제할 수 있습니다."
            );
        }
    }

    private PostResponse toResponse(
            Post post,
            Long currentEmployeeId
    ) {
        // 익명 게시글의 응답에서 작성자 닉네임 제외
        String displayName = post.isAnonymous()
                ? "익명"
                : post.getAuthor().getNickname();

        // 서버의 실제 저장 경로 대신 인증을 거치는 조회 API 제공
        String imageUrl = post.getImageKey() == null
                ? null
                : "/api/post/" + post.getPostId() + "/image";

        return new PostResponse(
                post.getPostId(),
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                displayName,
                post.isAnonymous(),
                imageUrl,
                post.isWrittenBy(currentEmployeeId),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}