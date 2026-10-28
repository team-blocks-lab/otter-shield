package com.otter.ott.video;

import com.otter.ott.video.dto.VideoDetailResponse;
import com.otter.ott.video.dto.VideoPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    // 비로그인 사용자에게도 공개하는 영상 목록
    @GetMapping
    public VideoPageResponse getVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return videoService.getVideos(page, size);
    }

    // 영상 소개 정보 조회 및 재생 요청의 분리
    @GetMapping("/{videoId}")
    public VideoDetailResponse getVideo(
            @PathVariable Long videoId
    ) {
        return videoService.getVideo(videoId);
    }
}