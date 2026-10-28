package com.otter.intranet.auth;

import com.otter.intranet.auth.dto.InitialSetupRequest;
import com.otter.intranet.auth.dto.LoginRequest;
import com.otter.intranet.auth.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String LOGIN_EMPLOYEE_ID = "LOGIN_EMPLOYEE_ID";

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        LoginResponse response = authService.login(request);

        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                LOGIN_EMPLOYEE_ID, response.employeeId()
        );

        return response;
    }

    @GetMapping("/me")
    public LoginResponse me(HttpServletRequest request) {
        return authService.getCurrentEmployee(
                requireEmployeeId(request)
        );
    }

    @PostMapping("/initial-setup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeInitialSetup(
            @Valid @RequestBody InitialSetupRequest request,
            HttpServletRequest httpRequest
    ) {
        authService.completeInitialSetup(
                requireEmployeeId(httpRequest),
                request
        );

        httpRequest.changeSessionId();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

    private Long requireEmployeeId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null
                && session.getAttribute(LOGIN_EMPLOYEE_ID)
                instanceof Long employeeId) {
            return employeeId;
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "로그인이 필요합니다."
        );
    }
}