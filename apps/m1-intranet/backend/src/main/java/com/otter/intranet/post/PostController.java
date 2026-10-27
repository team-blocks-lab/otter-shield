
package com.otter.intranet.post;

import com.otter.intranet.post.dto.PostCreateRequest;
import com.otter.intranet.post.dto.PostFeedResponse;
import com.otter.intranet.post.dto.PostUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 기존 로그인 코드의 세션 속성명과 일치해야 함
    private static final String SESSION_EMPLOYEE_ID = "LOGIN_EMPLOYEE_ID";

    // 게시글 목록 조회
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

    // 게시글 등록
    @PostMapping
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

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        postService.updatePost(employeeId, postId, request);

        return ResponseEntity.noContent().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @SessionAttribute(
                    name = SESSION_EMPLOYEE_ID,
                    required = false
            ) Long employeeId,
            @PathVariable Long postId
    ) {
        postService.deletePost(employeeId, postId);

        return ResponseEntity.noContent().build();
    }
}
