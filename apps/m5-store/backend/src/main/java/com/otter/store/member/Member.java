package com.otter.store.member;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 서비스에서 암호화한 비밀번호 해시만 전달받는 회원 생성 경로
    public Member(String name, String loginId, String passwordHash) {
        this.name = name;
        this.loginId = loginId;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    private void initializeCreatedAt() {
        // 최초 저장 직전 서버 시각 기록
        createdAt = LocalDateTime.now();
    }
}