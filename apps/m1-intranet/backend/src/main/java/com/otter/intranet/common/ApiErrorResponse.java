package com.otter.intranet.common;

import java.util.Map;

/**
 * API 오류 응답 DTO
 * HTTP 상태 코드, 오류 메시지, 필드별 검증 결과 전달
 */
public record ApiErrorResponse(
        int status,
        String message,
        Map<String, String> fieldErrors
) {
}