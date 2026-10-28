package com.otter.ott.auth;

import com.otter.ott.auth.dto.LoginRequest;
import com.otter.ott.auth.dto.LoginResponse;
import com.otter.ott.auth.dto.SignupRequest;
import com.otter.ott.member.Member;
import com.otter.ott.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(SignupRequest request) {
        // BCrypt 입력 한도 초과에 따른 암호화 오류 방지
        validatePasswordBytes(request.password());

        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 사용 중인 아이디입니다."
            );
        }

        // 평문 비밀번호의 DB 저장 방지를 위한 해시 변환
        String passwordHash = passwordEncoder.encode(request.password());

        Member member = new Member(
                request.name(),
                request.loginId(),
                passwordHash
        );

        return memberRepository.save(member).getMemberId();
    }

    public LoginResponse login(LoginRequest request) {
        // BCrypt 한도를 초과하는 로그인 입력의 인증 거절
        if (request.password()
                .getBytes(StandardCharsets.UTF_8).length > 72) {
            throw unauthorized();
        }

        Member member = memberRepository
                .findByLoginId(request.loginId())
                .orElseThrow(this::unauthorized);

        // 입력한 평문과 저장된 해시의 일치 여부 검증
        if (!passwordEncoder.matches(
                request.password(),
                member.getPasswordHash()
        )) {
            throw unauthorized();
        }

        return toResponse(member);
    }

    public LoginResponse getCurrentMember(Long memberId) {
        if (memberId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        // 세션에 회원 번호가 있어도 실제 회원의 존재 여부 재확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "로그인이 필요합니다."
                ));

        return toResponse(member);
    }

    private LoginResponse toResponse(Member member) {
        return new LoginResponse(
                member.getMemberId(),
                member.getName(),
                member.getLoginId()
        );
    }

    private ResponseStatusException unauthorized() {
        // 아이디 존재 여부가 드러나지 않도록 인증 실패 문구 통일
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "아이디 또는 비밀번호를 확인해 주세요."
        );
    }

    private void validatePasswordBytes(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "비밀번호는 UTF-8 기준 72바이트 이내로 입력해 주세요."
            );
        }
    }
}