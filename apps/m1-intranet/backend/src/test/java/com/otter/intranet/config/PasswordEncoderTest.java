package com.otter.intranet.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Test
    void 같은_비밀번호의_해시_검증_성공() {
        // 가상 계정의 초기 비밀번호
        String rawPassword = "1234";

        // 비밀번호 해시 생성
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 원본 비밀번호와 해시의 일치 여부 검증
        assertTrue(
                passwordEncoder.matches(rawPassword, encodedPassword)
        );

        // 실습 DB의 테스트 계정 설정용 해시 출력
        System.out.println("테스트 계정 비밀번호 해시: " + encodedPassword);
    }

    @Test
    void 다른_비밀번호의_해시_검증_실패() {
        String encodedPassword = passwordEncoder.encode("1234");

        // 잘못된 비밀번호의 인증 거절 여부 검증
        assertFalse(
                passwordEncoder.matches("wrong-password", encodedPassword)
        );
    }
}