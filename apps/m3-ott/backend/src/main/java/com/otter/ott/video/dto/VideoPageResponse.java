package com.otter.ott.video.dto;

import java.util.List;

// 영상 목록 및 페이지 이동에 필요한 정보
public record VideoPageResponse(
        List<VideoSummaryResponse> videos,
        int page,
        int totalPages,
        long totalElements
) {
}