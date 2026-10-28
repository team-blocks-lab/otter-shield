package com.otter.intranet.post;

import com.otter.intranet.employee.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PostTest {

    // 실제 직원 데이터 및 DB 연결과 분리된 작성자 객체
    private final Employee author = mock(Employee.class);

    @Test
    @DisplayName("칭찬 게시글의 익명 등록 및 실제 작성자 유지")
    void allowsAnonymousPraisePost() {
        Post post = new Post(
                author,
                PostCategory.PRAISE,
                "동료를 칭찬합니다",
                "업무를 도와주셔서 감사합니다.",
                true,
                null
        );

        // 익명 표시 설정 및 실제 작성자 관계 유지 검증
        assertTrue(post.isAnonymous());
        assertSame(author, post.getAuthor());
    }

    @Test
    @DisplayName("점심 게시글의 익명 등록 거절")
    void rejectsAnonymousLunchPost() {
        // 게시글 분류에 따른 익명 제한 검증
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post(
                        author,
                        PostCategory.LUNCH,
                        "오늘의 점심",
                        "근처 식당을 추천합니다.",
                        true,
                        null
                )
        );
    }

    @Test
    @DisplayName("공백 제목의 게시글 등록 거절")
    void rejectsBlankTitle() {
        // 제목의 필수 입력 규칙 검증
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post(
                        author,
                        PostCategory.PRAISE,
                        "   ",
                        "칭찬 내용",
                        false,
                        null
                )
        );
    }

    @Test
    @DisplayName("공백 내용의 게시글 등록 거절")
    void rejectsBlankContent() {
        // 내용의 필수 입력 규칙 검증
        assertThrows(
                IllegalArgumentException.class,
                () -> new Post(
                        author,
                        PostCategory.PRAISE,
                        "칭찬 제목",
                        "   ",
                        false,
                        null
                )
        );
    }
}