package com.otter.store.common;

import java.util.Map;

// API 오류의 상태·안내 문구·필드별 검증 결과
public record ApiErrorResponse(
        int status,
        String message,
        Map<String, String> fieldErrors
) {
}