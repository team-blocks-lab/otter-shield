package com.otter.ott.common;

import java.util.Map;

// 프론트엔드에서 공통으로 처리할 API 오류 응답
public record ApiErrorResponse(
        int status,
        String message,
        Map<String, String> fieldErrors
) {
}