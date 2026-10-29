package com.otter.ott.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MediaUrlService {

    private final String baseUrl;

    public MediaUrlService(
            @Value("${APP_MEDIA_BASE_URL:}") String baseUrl
    ) {
        this.baseUrl = baseUrl.trim().replaceAll("/+$", "");
    }

    public String toUrl(String mediaKey) {

        // 미디어 서버 주소가 설정되어 있는지 확인
        if (baseUrl.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "미디어 서버 주소가 설정되지 않았습니다."
            );
        }

        // 잘못된 파일 키가 URL에 사용되지 않도록 검사
        if (mediaKey == null || mediaKey.isBlank()
                || mediaKey.startsWith("/")
                || mediaKey.contains("..")
                || mediaKey.contains("\\")
                || mediaKey.contains("?")
                || mediaKey.contains("#")) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "미디어 파일 경로가 올바르지 않습니다."
            );
        }

        // 기본 주소와 파일 키를 조합
        return baseUrl + "/" + mediaKey;
    }
}