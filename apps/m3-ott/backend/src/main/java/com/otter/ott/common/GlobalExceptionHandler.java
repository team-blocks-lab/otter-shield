package com.otter.ott.common;

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

    // DTO 검증 실패 시 필드별 오류 메시지 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // 동일 필드의 여러 오류 중 첫 번째 메시지 보관
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "입력값을 확인해 주세요."
                ));

        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "입력값을 확인해 주세요.",
                        fieldErrors
                )
        );
    }

    // 서비스에서 지정한 인증·중복 등의 HTTP 상태 유지
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(
            ResponseStatusException exception
    ) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ApiErrorResponse(
                        exception.getStatusCode().value(),
                        exception.getReason() != null
                                ? exception.getReason()
                                : "요청을 처리할 수 없습니다.",
                        Map.of()
                ));
    }

    // 잘못된 JSON 또는 요청 데이터 타입 오류 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "JSON 형식과 입력값을 확인해 주세요.",
                        Map.of()
                )
        );
    }

    // 동시 가입 등의 DB 제약조건 충돌 처리
    // SQL·테이블 구조 등 내부 오류 정보의 응답 노출 방지
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "저장할 정보가 기존 데이터 또는 저장 조건과 충돌합니다.",
                        Map.of()
                ));
    }
}