
package com.otter.ott.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/ai")
public class AiIngestController {

    private final JdbcTemplate jdbc;
    private final String apiToken;

    public AiIngestController(
            JdbcTemplate jdbc,
            @Value("${AI_API_TOKEN}") String apiToken
    ) {
        this.jdbc = jdbc;
        this.apiToken = apiToken;

        if (apiToken == null || apiToken.isBlank()) {
            throw new IllegalStateException(
                    "AI_API_TOKEN is required"
            );
        }
    }

    private void authenticate(String token) {
        byte[] expected = apiToken.getBytes(
                StandardCharsets.UTF_8
        );

        byte[] received = (token == null ? "" : token)
                .getBytes(StandardCharsets.UTF_8);

        if (!MessageDigest.isEqual(expected, received)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }
    }

    private void checkKey(String key, String prefix) {
        if (key == null
                || !key.startsWith(prefix + "/")
                || key.length() > 500
                || key.contains("..")
                || key.contains("\\")
                || key.contains("?")
                || key.contains("#")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid S3 key"
            );
        }
    }

    private void checkFingerprint(String fingerprint) {
        if (fingerprint == null
                || !fingerprint.matches("[0-9a-f]{64}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid fingerprint"
            );
        }
    }

    public record SubtitleInput(
            String language,
            String subtitleKey
    ) {}

    public record VideoInput(
            String videoKey,
            String fingerprint,
            String title,
            String thumbnailKey,
            String summary,
            int durationSeconds,
            List<SubtitleInput> subtitles
    ) {}

    @GetMapping("/status")
    public Map<String, Boolean> status(
            @RequestHeader(
                    value = "X-AI-Token",
                    required = false
            ) String token,
            @RequestParam String videoKey,
            @RequestParam String fingerprint
    ) {
        authenticate(token);
        checkKey(videoKey, "test/videos");
        checkFingerprint(fingerprint);

        List<String> rows = jdbc.query(
                """
                SELECT source_fingerprint
                FROM ai_media_job
                WHERE video_key = ?
                """,
                (rs, rowNum) -> rs.getString(1),
                videoKey
        );

        boolean completed = !rows.isEmpty()
                && fingerprint.equals(rows.get(0));

        return Map.of("completed", completed);
    }

    @PostMapping("/complete")
    @Transactional
    public Map<String, Object> complete(
            @RequestHeader(
                    value = "X-AI-Token",
                    required = false
            ) String token,
            @RequestBody VideoInput input
    ) {
        authenticate(token);

        checkKey(input.videoKey(), "test/videos");
        checkKey(input.thumbnailKey(), "thumbnails");
        checkFingerprint(input.fingerprint());

        if (input.title() == null
                || input.title().isBlank()
                || input.title().length() > 200
                || input.summary() == null
                || input.durationSeconds() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid video metadata"
            );
        }

        List<SubtitleInput> subtitles =
                input.subtitles() == null
                        ? List.of()
                        : input.subtitles();

        if (subtitles.size() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Too many subtitle tracks"
            );
        }

        java.util.Set<String> languages =
                new java.util.HashSet<>();

        for (SubtitleInput subtitle : subtitles) {
            if (subtitle == null
                    || !("ko".equals(subtitle.language())
                    || "en".equals(subtitle.language()))
                    || !languages.add(subtitle.language())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid subtitle language"
                );
            }

            checkKey(
                    subtitle.subtitleKey(),
                    "subtitles"
            );
        }

        jdbc.update(
                """
                INSERT INTO video (
                    title, thumbnail_key, genre,
                    summary, actor, video_key,
                    duration_seconds, status
                )
                VALUES (?, ?, NULL, ?, NULL, ?, ?, 'PUBLIC')
                ON DUPLICATE KEY UPDATE
                    title = VALUES(title),
                    thumbnail_key = VALUES(thumbnail_key),
                    summary = VALUES(summary),
                    duration_seconds = VALUES(duration_seconds),
                    status = 'PUBLIC'
                """,
                input.title(),
                input.thumbnailKey(),
                input.summary(),
                input.videoKey(),
                input.durationSeconds()
        );

        Long videoId = jdbc.queryForObject(
                "SELECT video_id FROM video WHERE video_key = ?",
                Long.class,
                input.videoKey()
        );

        if (videoId == null) {
            throw new IllegalStateException(
                    "Video ID not found"
            );
        }

        jdbc.update(
                "DELETE FROM subtitle WHERE video_id = ?",
                videoId
        );

        for (SubtitleInput subtitle : subtitles) {
            jdbc.update(
                    """
                    INSERT INTO subtitle (
                        video_id, language, subtitle_key
                    )
                    VALUES (?, ?, ?)
                    """,
                    videoId,
                    subtitle.language(),
                    subtitle.subtitleKey()
            );
        }

        jdbc.update(
                """
                INSERT INTO ai_media_job (
                    video_key, source_fingerprint,
                    completed_at
                )
                VALUES (?, ?, CURRENT_TIMESTAMP(6))
                ON DUPLICATE KEY UPDATE
                    source_fingerprint =
                        VALUES(source_fingerprint),
                    completed_at = CURRENT_TIMESTAMP(6)
                """,
                input.videoKey(),
                input.fingerprint()
        );

        return Map.of(
                "status", "completed",
                "videoId", videoId
        );
    }
}

