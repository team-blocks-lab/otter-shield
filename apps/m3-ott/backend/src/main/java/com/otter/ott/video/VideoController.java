package com.otter.ott.video;

import com.otter.ott.auth.AuthController;
import com.otter.ott.video.dto.VideoDetailResponse;
import com.otter.ott.video.dto.VideoPageResponse;
import com.otter.ott.video.dto.PlaybackResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final PlaybackService playbackService;

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

    // 로그인한 회원의 영상 재생 정보
    @GetMapping("/{videoId}/playback")
    public PlaybackResponse getPlayback(
            @PathVariable Long videoId,
            @SessionAttribute(
                    name = AuthController.LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId
    ) {
        return playbackService.getPlayback(videoId, memberId);
    }
}


