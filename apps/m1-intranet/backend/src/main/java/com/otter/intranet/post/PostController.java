package com.otter.intranet.post;

import com.otter.intranet.post.dto.PostCreateRequest;
import com.otter.intranet.post.dto.PostFeedResponse;
import com.otter.intranet.post.dto.PostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 로그인 시 저장하는 세션 속성명과 동일한 이름 사용
    private static final String SESSION_EMPLOYEE_ID = "LOGIN_EMPLOYEE_ID";

    @GetMapping
    public PostFeedResponse getFeed(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return postService.getFeed(employeeId, page, size);
    }

    // 사진 없는 기존 JSON 등록 요청 처리
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> createPost(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        Long postId = postService.createPost(employeeId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("postId", postId));
    }

    // 게시글 JSON과 사진 파일을 함께 받는 multipart 등록 요청 처리
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Long>> createPostWithImage(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @Valid @RequestPart("post") PostCreateRequest request,
            @RequestPart(
                    name = "image",
                    required = false
            ) MultipartFile image
    ) {
        Long postId = postService.createPost(employeeId, request, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("postId", postId));
    }

    @GetMapping(
            value = "/{postId}/image",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<Resource> getPostImage(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @PathVariable Long postId
    ) {
        Resource image = postService.getPostImage(employeeId, postId);

        // PNG로 정규화한 사진 응답 및 브라우저의 다른 형식 추측 차단
        // 로그아웃 이후 캐시를 통한 사내 사진 재노출 방지를 위한 저장 금지
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header("X-Content-Type-Options", "nosniff")
                .body(image);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        // 기존 사진 유지 및 게시글 정보 수정
        postService.updatePost(employeeId, postId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @PathVariable Long postId
    ) {
        // 작성자 권한 확인 및 게시글 삭제 처리를 서비스에 위임
        postService.deletePost(employeeId, postId);

        return ResponseEntity.noContent().build();
    }
}