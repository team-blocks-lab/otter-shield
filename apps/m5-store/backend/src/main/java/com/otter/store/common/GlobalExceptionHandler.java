package com.otter.store.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        // 필드별 첫 번째 입력 오류 메시지 보관
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "입력값을 확인해 주세요."
                ));

        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        400,
                        "입력값을 확인해 주세요.",
                        errors
                )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(
            ResponseStatusException exception
    ) {
        // 서비스에서 지정한 HTTP 상태와 안내 문구 유지
        return ResponseEntity.status(exception.getStatusCode())
                .body(new ApiErrorResponse(
                        exception.getStatusCode().value(),
                        exception.getReason() != null
                                ? exception.getReason()
                                : "요청을 처리할 수 없습니다.",
                        Map.of()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        400,
                        "JSON 형식과 입력값을 확인해 주세요.",
                        Map.of()
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataConflict(
            DataIntegrityViolationException exception
    ) {
        // 동시 요청 등의 DB 제약조건 충돌 처리 및 내부 SQL 노출 방지
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        409,
                        "기존 데이터 또는 저장 조건과 충돌합니다.",
                        Map.of()
                ));
    }
}