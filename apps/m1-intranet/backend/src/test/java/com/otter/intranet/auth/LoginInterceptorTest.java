package com.otter.intranet.auth;

import com.otter.intranet.auth.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LoginInterceptorTest {

    private AuthService authService;
    private LoginInterceptor loginInterceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        // DB 조회와 분리된 인터셉터 테스트 환경 구성
        authService = mock(AuthService.class);
        loginInterceptor = new LoginInterceptor(authService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("로그인 세션이 없는 요청의 접근 거절")
    void rejectsRequestWithoutSession() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> loginInterceptor.preHandle(
                        request, response, new Object()
                )
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        // 미인증 요청의 직원 조회 차단 검증
        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("최초 설정을 완료하지 않은 직원의 접근 거절")
    void rejectsEmployeeBeforeInitialSetup() {
        // 최초 설정 전 직원의 로그인 세션 구성
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LOGIN_EMPLOYEE_ID", 1L);
        request.setSession(session);

        when(authService.getCurrentEmployee(1L))
                .thenReturn(new LoginResponse(
                        1L, null, true
                ));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> loginInterceptor.preHandle(
                        request, response, new Object()
                )
        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                exception.getStatusCode()
        );
    }

    @Test
    @DisplayName("로그인 및 최초 설정을 완료한 직원의 접근 허용")
    void allowsEmployeeAfterInitialSetup() {
        // 최초 설정 완료 직원의 로그인 세션 구성
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LOGIN_EMPLOYEE_ID", 1L);
        request.setSession(session);

        when(authService.getCurrentEmployee(1L))
                .thenReturn(new LoginResponse(
                        1L, "테스트직원", false
                ));

        boolean result = loginInterceptor.preHandle(
                request, response, new Object()
        );

        assertTrue(result);
    }
}