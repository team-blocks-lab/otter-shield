package com.otter.intranet.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPasswordGeneratorTest {

    @Test
    void generateTestPassword() {

        // 실제 DB나 Spring Boot 실행 없이 암호화 객체 생성
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // 무작위 테스트 비밀번호 생성
        byte[] randomBytes = new byte[18];
        new SecureRandom().nextBytes(randomBytes);

        String password = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // BCrypt 해시 생성
        String passwordHash = encoder.encode(password);

        // 생성된 비밀번호와 해시가 일치하는지 확인
        assertTrue(encoder.matches(password, passwordHash));

        System.out.println("TEST_PASSWORD = " + password);
        System.out.println("PASSWORD_HASH = " + passwordHash);
    }
}