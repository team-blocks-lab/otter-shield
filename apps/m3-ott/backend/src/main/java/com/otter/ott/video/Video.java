package com.otter.ott.video;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "video")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "thumbnail_key", nullable = false, length = 500)
    private String thumbnailKey;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "actor", length = 255)
    private String actor;

    // 로그인 확인 후 재생 주소 생성에 사용하는 미디어 저장 키
    @Column(name = "video_key", nullable = false, length = 500)
    private String videoKey;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    // Enum 이름을 문자열로 저장하여 순서 변경에 따른 데이터 오해 방지
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VideoStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}