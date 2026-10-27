
package com.otter.intranet.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API 공통 예외 처리
 * 예외 유형별 HTTP 상태 코드 및 JSON 오류 응답 통일
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid를 통한 DTO 필드 검증 실패 시 발생하는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // 필드별 첫 번째 검증 오류 메시지 저장
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

    // 잘못된 인자 또는 게시글 업무 규칙 위반 시 발생하는 예외 처리
    // 서비스에서 지정한 401·403·404 등의 HTTP 상태 코드를 유지하여 반환
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage() != null
                                ? exception.getMessage()
                                : "입력값을 확인해 주세요.",
                        Map.of()
                )
        );
    }

    // 인증·인가·리소스 조회 오류의 기존 HTTP 상태 코드 유지
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

    // JSON 문법 오류 또는 요청 데이터의 DTO 변환 실패 처리
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
}
