package com.otter.ott.subtitle;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "subtitle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subtitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtitle_id")
    private Long subtitleId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "language", nullable = false, length = 2)
    private String language;

    @Column(name = "subtitle_key", nullable = false, length = 500)
    private String subtitleKey;
}