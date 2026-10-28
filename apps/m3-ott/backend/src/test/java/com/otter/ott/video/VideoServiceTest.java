package com.otter.ott.video;

import com.otter.ott.video.dto.VideoDetailResponse;
import com.otter.ott.video.dto.VideoPageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VideoServiceTest {

    private VideoRepository videoRepository;
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        // 실제 DB 연결 없이 조회 결과를 지정하는 모의 Repository
        videoRepository = mock(VideoRepository.class);
        videoService = new VideoService(videoRepository);
    }

    @Test
    @DisplayName("공개 영상 조회 조건 및 페이지·정렬 전달")
    void getVideosUsesPublicStatusAndPagination() {
        Video video = mock(Video.class);

        when(video.getVideoId()).thenReturn(1L);
        when(video.getTitle()).thenReturn("테스트 영상");
        when(video.getThumbnailKey()).thenReturn("thumbnails/test.jpg");
        when(video.getGenre()).thenReturn("여행");
        when(video.getDurationSeconds()).thenReturn(45);

        when(videoRepository.findByStatus(
                eq(VideoStatus.PUBLIC),
                any(Pageable.class)
        )).thenAnswer(invocation -> {
            Pageable pageable = invocation.getArgument(1);

            return new PageImpl<>(
                    List.of(video),
                    pageable,
                    1
            );
        });

        VideoPageResponse response = videoService.getVideos(0, 12);

        // 목록 DTO 변환 및 페이지 정보 확인
        assertEquals(1, response.videos().size());
        assertEquals(1L, response.videos().getFirst().videoId());
        assertEquals("테스트 영상", response.videos().getFirst().title());
        assertEquals(0, response.page());
        assertEquals(1, response.totalPages());
        assertEquals(1L, response.totalElements());

        // Repository에 전달된 실제 페이지·정렬 조건 확인
        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(videoRepository).findByStatus(
                eq(VideoStatus.PUBLIC),
                captor.capture()
        );

        Pageable pageable = captor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(12, pageable.getPageSize());
        assertEquals(
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("videoId")
                ),
                pageable.getSort()
        );
    }

    @Test
    @DisplayName("영상이 없을 때 빈 목록 반환")
    void getVideosReturnsEmptyList() {
        when(videoRepository.findByStatus(
                eq(VideoStatus.PUBLIC),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        VideoPageResponse response = videoService.getVideos(0, 12);

        assertTrue(response.videos().isEmpty());
        assertEquals(0L, response.totalElements());
    }

    @Test
    @DisplayName("잘못된 페이지·조회 개수의 DB 접근 차단")
    void getVideosRejectsInvalidPagination() {
        int[][] invalidInputs = {
                {-1, 12},
                {0, 0},
                {0, 51}
        };

        for (int[] input : invalidInputs) {
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> videoService.getVideos(input[0], input[1])
            );

            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exception.getStatusCode()
            );
        }

        verifyNoInteractions(videoRepository);
    }

    @Test
    @DisplayName("공개 영상 상세 조회 및 DTO 변환")
    void getVideoReturnsPublicVideoDetail() {
        Video video = mock(Video.class);

        when(video.getVideoId()).thenReturn(1L);
        when(video.getTitle()).thenReturn("테스트 영상");
        when(video.getThumbnailKey()).thenReturn("thumbnails/test.jpg");
        when(video.getGenre()).thenReturn("여행");
        when(video.getSummary()).thenReturn("도시 여행 소개");
        when(video.getActor()).thenReturn("내레이션");
        when(video.getDurationSeconds()).thenReturn(45);

        when(videoRepository.findByVideoIdAndStatus(
                1L,
                VideoStatus.PUBLIC
        )).thenReturn(Optional.of(video));

        VideoDetailResponse response = videoService.getVideo(1L);

        assertEquals(1L, response.videoId());
        assertEquals("테스트 영상", response.title());
        assertEquals("도시 여행 소개", response.summary());
        assertEquals(45, response.durationSeconds());

        // 공개 조건을 포함한 상세 조회 및 재생 키 조회 미호출 확인
        verify(videoRepository).findByVideoIdAndStatus(
                1L,
                VideoStatus.PUBLIC
        );
        verify(video, never()).getVideoKey();
    }

    @Test
    @DisplayName("공개 조건에 맞는 영상이 없으면 404 반환")
    void getVideoRejectsUnavailableVideo() {
        when(videoRepository.findByVideoIdAndStatus(
                999L,
                VideoStatus.PUBLIC
        )).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> videoService.getVideo(999L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
    }
}