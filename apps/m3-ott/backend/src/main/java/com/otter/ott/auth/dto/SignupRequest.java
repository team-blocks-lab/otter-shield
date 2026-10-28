package com.otter.ott.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 회원가입 요청 전용 데이터
// 회원 식별자·가입일·비밀번호 해시는 클라이언트 입력 대상에서 제외
public record SignupRequest(

        @NotBlank(message = "이름을 입력해 주세요.")
        @Size(max = 50, message = "이름은 50자 이내로 입력해 주세요.")
        @Pattern(
                regexp = "\\S(?:.*\\S)?",
                message = "이름 앞뒤에 공백을 넣을 수 없습니다."
        )
        String name,

        @NotBlank(message = "아이디를 입력해 주세요.")
        @Pattern(
                regexp = "[a-z0-9]{4,30}",
                message = "아이디는 영문 소문자와 숫자 4~30자로 입력해 주세요."
        )
        String loginId,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Size(
                min = 8,
                max = 64,
                message = "비밀번호는 8~64자로 입력해 주세요."
        )
        String password
) {
}