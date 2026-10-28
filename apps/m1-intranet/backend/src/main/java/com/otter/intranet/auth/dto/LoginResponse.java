package com.otter.intranet.auth.dto;

public record LoginResponse(
        Long employeeId,
        String nickname,
        boolean firstLogin
) {
}