package com.otter.ott.video;

import com.otter.ott.auth.AuthService;
import com.otter.ott.media.MediaUrlService;
import com.otter.ott.subtitle.SubtitleRepository;
import com.otter.ott.video.dto.PlaybackResponse;
import com.otter.ott.video.dto.SubtitleTrackResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaybackService {

    private final AuthService authService;
    private final VideoRepository videoRepository;
    private final SubtitleRepository subtitleRepository;
    private final MediaUrlService mediaUrlService;

    public PlaybackResponse getPlayback(
            Long videoId,
            Long memberId
    ) {

        // 1. 로그인 세션의 회원 확인
        authService.getCurrentMember(memberId);

        // 2. 공개된 영상만 조회
        Video video = videoRepository
                .findByVideoIdAndStatus(
                        videoId,
                        VideoStatus.PUBLIC
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "영상을 찾을 수 없습니다."
                        )
                );

        // 3. 영상 파일의 실제 URL 생성
        String videoUrl = mediaUrlService.toUrl(
                video.getVideoKey()
        );

        // 4. 영상에 연결된 자막 조회
        List<SubtitleTrackResponse> subtitles =
                subtitleRepository
                        .findByVideoIdOrderByLanguageAsc(videoId)
                        .stream()
                        .map(subtitle ->
                                new SubtitleTrackResponse(
                                        subtitle.getLanguage(),
                                        mediaUrlService.toUrl(
                                                subtitle.getSubtitleKey()
                                        )
                                )
                        )
                        .toList();

        // 5. React에 영상·자막 정보 전달
        return new PlaybackResponse(
                videoUrl,
                subtitles
        );
    }
}