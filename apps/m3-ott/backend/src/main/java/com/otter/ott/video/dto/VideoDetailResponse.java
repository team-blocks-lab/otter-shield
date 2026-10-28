package com.otter.ott.video.dto;

// 비로그인 사용자에게도 제공하는 영상 소개 정보
// 재생 파일 키 및 재생 주소는 별도 인증 API로 분리
public record VideoDetailResponse(
        Long videoId,
        String title,
        String thumbnailKey,
        String genre,
        String summary,
        String actor,
        int durationSeconds
) {
}