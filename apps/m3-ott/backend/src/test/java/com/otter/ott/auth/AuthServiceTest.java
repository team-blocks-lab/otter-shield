package com.otter.ott.auth;

import com.otter.ott.auth.dto.SignupRequest;
import com.otter.ott.member.Member;
import com.otter.ott.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // 실제 DB 연결 없이 회원 저장·조회 응답을 대신하는 모의 객체
        memberRepository = mock(MemberRepository.class);

        // 평문 저장 방지 및 해시 일치 여부 검증을 위한 실제 암호화 객체
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthService(
                memberRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("회원가입 시 비밀번호 해시 저장 및 회원 번호 반환")
    void signupStoresPasswordHash() {
        SignupRequest request = new SignupRequest(
                "테스트회원",
                "member01",
                "TestPass123!"
        );

        Member savedMember = mock(Member.class);

        when(memberRepository.existsByLoginId("member01"))
                .thenReturn(false);
        when(savedMember.getMemberId()).thenReturn(1L);
        when(memberRepository.save(any(Member.class)))
                .thenReturn(savedMember);

        Long memberId = authService.signup(request);

        // Repository에 전달된 실제 회원 객체를 꺼내 저장 내용 확인
        ArgumentCaptor<Member> captor =
                ArgumentCaptor.forClass(Member.class);

        verify(memberRepository).save(captor.capture());

        Member submittedMember = captor.getValue();

        assertEquals(1L, memberId);
        assertEquals("테스트회원", submittedMember.getName());
        assertEquals("member01", submittedMember.getLoginId());

        // 평문과 다른 값의 저장 및 원래 비밀번호와의 BCrypt 일치 확인
        assertNotEquals(
                request.password(),
                submittedMember.getPasswordHash()
        );
        assertTrue(passwordEncoder.matches(
                request.password(),
                submittedMember.getPasswordHash()
        ));
    }

    @Test
    @DisplayName("중복 아이디 회원가입 거절")
    void signupRejectsDuplicateLoginId() {
        SignupRequest request = new SignupRequest(
                "테스트회원",
                "member01",
                "TestPass123!"
        );

        when(memberRepository.existsByLoginId("member01"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.signup(request)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        // 중복 검사 실패 시 DB 저장 미호출 확인
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("UTF-8 기준 72바이트 초과 비밀번호 거절")
    void signupRejectsPasswordOverByteLimit() {
        // 한글 25자: 글자 수 제한 이내지만 UTF-8 기준 75바이트
        SignupRequest request = new SignupRequest(
                "테스트회원",
                "member01",
                "가".repeat(25)
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.signup(request)
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        // 입력 오류 단계에서 DB 접근 차단 확인
        verifyNoInteractions(memberRepository);
    }
}