package com.otter.ott.auth.dto;

// 로그인 및 현재 회원 조회 응답
// 비밀번호 해시를 제외한 회원 정보만 전달
public record LoginResponse(
        Long memberId,
        String name,
        String loginId
) {
}