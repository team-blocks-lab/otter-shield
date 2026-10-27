package com.otter.intranet.post;

import com.otter.intranet.employee.Employee;
import com.otter.intranet.employee.EmployeeRepository;
import com.otter.intranet.post.dto.PostCreateRequest;
import com.otter.intranet.post.dto.PostFeedResponse;
import com.otter.intranet.post.dto.PostResponse;
import com.otter.intranet.post.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final EmployeeRepository employeeRepository;
    private final PostRepository postRepository;

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

        // 생성일 기준 최신순 정렬
        // 생성 시각이 같은 게시글은 식별자 역순으로 정렬
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
        // 클라이언트 입력 대신 로그인 직원으로 작성자 지정
        Employee author = requireReadyEmployee(currentEmployeeId);

        // 업로드 기능 연동 전 임의 이미지 키의 저장 차단
        if (request.imageKey() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "사진 첨부 기능은 아직 준비 중입니다."
            );
        }

        Post post = new Post(
                author,
                request.category(),
                request.title(),
                request.content(),
                request.anonymous(),
                null
        );

        return postRepository.save(post).getPostId();
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
        // 관리 중인 엔티티의 변경 감지를 통한 DB 반영
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

        // 작성자 확인을 마친 게시글 삭제
        postRepository.delete(post);
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

        return new PostResponse(
                post.getPostId(),
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                displayName,
                post.isAnonymous(),
                null,
                post.isWrittenBy(currentEmployeeId),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}