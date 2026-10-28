package com.otter.intranet.post.dto;

import java.util.List;

public record PostFeedResponse(

        // 현재 페이지에 표시할 게시글 목록
        List<PostResponse> posts,

        // 0부터 시작하는 현재 페이지 번호
        int page,

        int totalPages,
        long totalElements
) {
}