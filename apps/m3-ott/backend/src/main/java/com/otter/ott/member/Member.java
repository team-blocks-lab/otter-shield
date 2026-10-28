package com.otter.ott.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    // DB의 AUTO_INCREMENT를 통한 회원 식별자 생성
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 동일 로그인 ID의 중복 가입 방지를 위한 고유값 지정
    @Column(
            name = "login_id",
            nullable = false,
            unique = true,
            length = 50
    )
    private String loginId;

    // 평문 비밀번호 대신 BCrypt 해시 보관
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // 회원 생성 시각 보관 및 이후 수정 차단
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 가입에 필요한 정보만 받는 회원 생성 경로
    // 비밀번호 암호화는 서비스에서 처리한 뒤 전달
    public Member(String name, String loginId, String passwordHash) {
        this.name = name;
        this.loginId = loginId;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    private void initializeCreatedAt() {
        // 최초 DB 저장 직전 서버 시각 기록
        this.createdAt = LocalDateTime.now();
    }
}