package com.otter.ott.subtitle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubtitleRepository
        extends JpaRepository<Subtitle, Long> {

    List<Subtitle> findByVideoIdOrderByLanguageAsc(
            Long videoId
    );
}