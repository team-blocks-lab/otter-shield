package com.otter.intranet.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "직원 번호를 입력해 주세요.")
        @Size(max = 30)
        String employeeNumber,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Size(max = 72)
        String password
) {
}