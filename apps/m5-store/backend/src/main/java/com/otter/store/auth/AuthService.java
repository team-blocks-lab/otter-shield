package com.otter.store.auth;

import com.otter.store.auth.dto.LoginRequest;
import com.otter.store.auth.dto.MemberResponse;
import com.otter.store.auth.dto.SignupRequest;
import com.otter.store.member.Member;
import com.otter.store.member.MemberRepository;
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
        // BCrypt의 UTF-8 입력 길이 제한 검사
        if (exceedsPasswordLimit(request.password())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "비밀번호는 UTF-8 기준 72바이트 이내로 입력해 주세요."
            );
        }

        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 사용 중인 아이디입니다."
            );
        }

        Member member = new Member(
                request.name(),
                request.loginId(),
                passwordEncoder.encode(request.password())
        );

        return memberRepository.save(member).getMemberId();
    }

    public MemberResponse login(LoginRequest request) {
        if (exceedsPasswordLimit(request.password())) {
            throw invalidCredentials();
        }

        Member member = memberRepository
                .findByLoginId(request.loginId())
                .orElseThrow(this::invalidCredentials);

        // 저장된 해시의 복호화 없이 입력 비밀번호의 일치 여부 검증
        if (!passwordEncoder.matches(
                request.password(),
                member.getPasswordHash()
        )) {
            throw invalidCredentials();
        }

        return toResponse(member);
    }

    public MemberResponse getCurrentMember(Long memberId) {
        return toResponse(requireMember(memberId));
    }

    public Member requireMember(Long memberId) {
        if (memberId == null) {
            throw loginRequired();
        }

        // 세션 회원 번호에 대응하는 실제 회원 존재 여부 확인
        return memberRepository.findById(memberId)
                .orElseThrow(this::loginRequired);
    }

    private boolean exceedsPasswordLimit(String password) {
        return password.getBytes(StandardCharsets.UTF_8).length > 72;
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getName(),
                member.getLoginId()
        );
    }

    private ResponseStatusException invalidCredentials() {
        // 아이디 존재 여부가 드러나지 않는 공통 인증 실패 응답
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "아이디 또는 비밀번호를 확인해 주세요."
        );
    }

    private ResponseStatusException loginRequired() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "로그인이 필요합니다."
        );
    }
}