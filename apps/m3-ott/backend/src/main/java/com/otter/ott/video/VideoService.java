package com.otter.ott.video;

import com.otter.ott.video.dto.VideoDetailResponse;
import com.otter.ott.video.dto.VideoPageResponse;
import com.otter.ott.video.dto.VideoSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoPageResponse getVideos(int page, int size) {
        // 잘못된 페이지 번호 및 과도한 일괄 조회 차단
        if (page < 0 || size < 1 || size > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "페이지는 0 이상, 조회 개수는 1~50으로 입력해 주세요."
            );
        }

        // 최신 등록순 및 동일 등록 시각의 정렬 순서 고정
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("videoId")
                )
        );

        Page<Video> result = videoRepository.findByStatus(
                VideoStatus.PUBLIC,
                pageable
        );

        return new VideoPageResponse(
                result.getContent().stream()
                        .map(this::toSummary)
                        .toList(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    public VideoDetailResponse getVideo(Long videoId) {
        // 비공개 영상과 존재하지 않는 영상의 응답 통일
        Video video = videoRepository.findByVideoIdAndStatus(
                videoId,
                VideoStatus.PUBLIC
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "영상을 찾을 수 없습니다."
        ));

        return new VideoDetailResponse(
                video.getVideoId(),
                video.getTitle(),
                video.getThumbnailKey(),
                video.getGenre(),
                video.getSummary(),
                video.getActor(),
                video.getDurationSeconds()
        );
    }

    private VideoSummaryResponse toSummary(Video video) {
        return new VideoSummaryResponse(
                video.getVideoId(),
                video.getTitle(),
                video.getThumbnailKey(),
                video.getGenre(),
                video.getDurationSeconds()
        );
    }
}