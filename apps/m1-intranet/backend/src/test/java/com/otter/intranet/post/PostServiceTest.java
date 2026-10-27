package com.otter.intranet.post;

import com.otter.intranet.employee.Employee;
import com.otter.intranet.employee.EmployeeRepository;
import com.otter.intranet.post.dto.PostFeedResponse;
import com.otter.intranet.post.dto.PostUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private EmployeeRepository employeeRepository;
    private PostRepository postRepository;
    private PostService postService;

    @BeforeEach
    void setUp() {
        // 실제 DB 연결과 분리된 서비스 테스트 환경 구성
        employeeRepository = mock(EmployeeRepository.class);
        postRepository = mock(PostRepository.class);

        postService = new PostService(
                employeeRepository,
                postRepository
        );
    }

    @Test
    @DisplayName("다른 직원의 게시글 수정 거절 및 기존 내용 유지")
    void rejectsUpdateByNonAuthor() {
        Employee currentEmployee = mock(Employee.class);
        Employee author = mock(Employee.class);

        when(employeeRepository.findById(2L))
                .thenReturn(Optional.of(currentEmployee));
        when(currentEmployee.isFirstLogin()).thenReturn(false);
        when(author.getEmployeeId()).thenReturn(1L);

        Post post = new Post(
                author,
                PostCategory.PRAISE,
                "기존 제목",
                "기존 내용",
                true,
                null
        );

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        PostUpdateRequest request = new PostUpdateRequest(
                PostCategory.PRAISE,
                "변경 제목",
                "변경 내용",
                false
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.updatePost(2L, 10L, request)
        );

        // 권한 오류 및 변경 전 데이터 유지 검증
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("기존 제목", post.getTitle());
        assertEquals("기존 내용", post.getContent());
        assertTrue(post.isAnonymous());
    }

    @Test
    @DisplayName("다른 직원의 게시글 삭제 거절")
    void rejectsDeleteByNonAuthor() {
        Employee currentEmployee = mock(Employee.class);
        Employee author = mock(Employee.class);

        when(employeeRepository.findById(2L))
                .thenReturn(Optional.of(currentEmployee));
        when(currentEmployee.isFirstLogin()).thenReturn(false);
        when(author.getEmployeeId()).thenReturn(1L);

        Post post = new Post(
                author,
                PostCategory.PRAISE,
                "칭찬 제목",
                "칭찬 내용",
                true,
                null
        );

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> postService.deletePost(2L, 10L)
        );

        // 권한 오류 및 Repository 삭제 미호출 검증
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("익명 게시글의 작성자 이름 비공개 및 본인 글 표시")
    void hidesAuthorNameForAnonymousPost() {
        Employee author = mock(Employee.class);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(author));
        when(author.isFirstLogin()).thenReturn(false);
        when(author.getEmployeeId()).thenReturn(1L);

        Post post = new Post(
                author,
                PostCategory.PRAISE,
                "동료 칭찬",
                "도와주셔서 감사합니다.",
                true,
                null
        );

        when(postRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post)));

        PostFeedResponse result = postService.getFeed(1L, 0, 10);

        // 익명 표시와 작성자 본인 여부의 독립 처리 검증
        assertEquals(1, result.posts().size());
        assertEquals("익명", result.posts().getFirst().authorDisplayName());
        assertTrue(result.posts().getFirst().mine());

        // 익명 응답 생성 시 실제 닉네임 조회 생략 검증
        verify(author, never()).getNickname();
    }
}