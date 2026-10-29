package com.otter.ott.video.dto;

import java.util.List;

public record PlaybackResponse(
        String videoUrl,
        List<SubtitleTrackResponse> subtitles
) {
}