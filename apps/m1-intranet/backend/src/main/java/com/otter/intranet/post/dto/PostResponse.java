package com.otter.intranet.post.dto;

import com.otter.intranet.post.PostCategory;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        PostCategory category,
        String title,
        String content,

        // 익명 여부를 반영한 화면 표시용 작성자 이름
        String authorDisplayName,

        boolean anonymous,

        // 브라우저에서 이미지를 조회할 주소
        // 이미지 미첨부 시 null 반환
        String imageUrl,

        // 로그인 직원의 작성 여부에 따른 수정·삭제 버튼 표시
        boolean mine,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}