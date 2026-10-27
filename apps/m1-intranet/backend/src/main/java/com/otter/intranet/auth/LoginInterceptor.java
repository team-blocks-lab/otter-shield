package com.otter.intranet.auth;

import com.otter.intranet.auth.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        // 기존 로그인 세션 조회
        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("LOGIN_EMPLOYEE_ID")
                instanceof Long employeeId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        // DB 기준의 직원 존재 여부 및 최초 설정 상태 확인
        LoginResponse employee =
                authService.getCurrentEmployee(employeeId);

        if (employee.firstLogin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "비밀번호와 닉네임을 먼저 설정해 주세요."
            );
        }

        return true;
    }
}