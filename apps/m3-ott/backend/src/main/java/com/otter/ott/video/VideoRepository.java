package com.otter.ott.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    // 공개 상태 영상만 페이지 단위 조회
    Page<Video> findByStatus(
            VideoStatus status,
            Pageable pageable
    );

    // 비공개 영상의 상세 정보 노출 방지를 위한 공개 조건 포함 조회
    Optional<Video> findByVideoIdAndStatus(
            Long videoId,
            VideoStatus status
    );
}