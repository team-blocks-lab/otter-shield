package com.otter.store.auth.dto;

// 비밀번호 해시를 제외한 로그인 회원 정보
public record MemberResponse(
        Long memberId,
        String name,
        String loginId
) {
}