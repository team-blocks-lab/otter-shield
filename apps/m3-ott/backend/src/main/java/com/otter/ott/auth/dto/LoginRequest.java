package com.otter.ott.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 로그인 요청 전용 데이터
public record LoginRequest(

        @NotBlank(message = "아이디를 입력해 주세요.")
        @Size(max = 50, message = "아이디를 확인해 주세요.")
        String loginId,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Size(max = 64, message = "비밀번호를 확인해 주세요.")
        String password
) {
}