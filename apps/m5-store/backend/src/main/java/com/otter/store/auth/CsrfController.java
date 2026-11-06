package com.otter.store.auth;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/api/auth/csrf")
    public ResponseEntity<CsrfResponse> getCsrfToken(CsrfToken csrfToken) {
        // 세션별 토큰 응답의 캐시 저장 방지
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new CsrfResponse(
                        csrfToken.getHeaderName(),
                        csrfToken.getToken()
                ));
    }

    public record CsrfResponse(String headerName, String token) {
    }
}