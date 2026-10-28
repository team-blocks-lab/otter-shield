package com.otter.ott.auth;

import com.otter.ott.auth.dto.LoginRequest;
import com.otter.ott.auth.dto.LoginResponse;
import com.otter.ott.auth.dto.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // 로그인 회원 식별에 사용하는 서버 세션 속성명
    public static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";

    private final AuthService authService;

    @PostMapping(
            value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Long>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        Long memberId = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("memberId", memberId));
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        LoginResponse response = authService.login(request);

        // 로그인 이전 세션 폐기로 기존 세션 식별자의 재사용 방지
        HttpSession oldSession = httpRequest.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        // 인증 성공 후 생성한 세션에 회원 번호만 보관
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                LOGIN_MEMBER_ID,
                response.memberId()
        );

        return response;
    }

    @GetMapping("/me")
    public LoginResponse getCurrentMember(
            @SessionAttribute(
                    name = LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId
    ) {
        return authService.getCurrentMember(memberId);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest httpRequest
    ) {
        // 세션이 없는 요청에서 불필요한 신규 세션 생성 방지
        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.noContent().build();
    }
}