package com.otter.intranet.auth;

import com.otter.intranet.auth.dto.LoginRequest;
import com.otter.intranet.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AuthServiceTest {

    private EmployeeRepository employeeRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // 실제 DB 및 해시 처리와 분리된 테스트 객체 구성
        employeeRepository = mock(EmployeeRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authService = new AuthService(
                employeeRepository,
                passwordEncoder
        );
    }

    @Test
    void 비밀번호가_72바이트를_초과하면_로그인_거절() {
        // UTF-8 기준 75바이트의 한글 비밀번호
        LoginRequest request = new LoginRequest(
                "TEST002",
                "가".repeat(25)
        );

        // 입력 제한 초과에 따른 예외 발생 검증
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request)
        );

        // 잘못된 요청 응답 및 후속 처리 미실행 검증
        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );
        verifyNoInteractions(
                employeeRepository,
                passwordEncoder
        );
    }
}