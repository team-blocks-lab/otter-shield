package com.otter.store.auth;

import com.otter.store.auth.dto.LoginRequest;
import com.otter.store.auth.dto.MemberResponse;
import com.otter.store.auth.dto.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    public static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Long>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        Long memberId = authService.signup(request);

        // 가입 완료 후 자동 로그인 없이 회원 번호 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("memberId", memberId));
    }

    @PostMapping("/login")
    public MemberResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        MemberResponse member = authService.login(request);

        // 인증 성공 후 기존 세션 폐기 및 새 세션 발급
        HttpSession previousSession = httpRequest.getSession(false);

        if (previousSession != null) {
            previousSession.invalidate();
        }

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(LOGIN_MEMBER_ID, member.memberId());

        return member;
    }

    @GetMapping("/me")
    public MemberResponse getCurrentMember(
            @SessionAttribute(
                    name = LOGIN_MEMBER_ID,
                    required = false
            ) Long memberId
    ) {
        return authService.getCurrentMember(memberId);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.noContent().build();
    }
}