package com.otter.ott.video.dto;

// 목록 카드에 필요한 정보만 전달
public record VideoSummaryResponse(
        Long videoId,
        String title,
        String thumbnailKey,
        String genre,
        int durationSeconds
) {
}