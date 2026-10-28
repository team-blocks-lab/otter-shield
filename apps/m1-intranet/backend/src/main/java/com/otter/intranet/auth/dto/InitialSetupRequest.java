package com.otter.intranet.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InitialSetupRequest(
        @NotBlank(message = "새 비밀번호를 입력해 주세요.")
        @Size(min = 8, max = 64,
                message = "비밀번호는 8~64자로 입력해 주세요.")
        String newPassword,

        @NotBlank(message = "닉네임을 입력해 주세요.")
        @Size(min = 2, max = 30,
                message = "닉네임은 2~30자로 입력해 주세요.")
        @Pattern(
                regexp = "\\S(?:.*\\S)?",
                message = "닉네임 앞뒤의 공백을 제거해 주세요."
        )
        String nickname
) {
}