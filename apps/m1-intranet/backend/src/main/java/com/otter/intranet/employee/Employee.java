package com.otter.intranet.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_number",
            nullable = false, unique = true, length = 30)
    private String employeeNumber;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nickname", length = 30)
    private String nickname;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

    public void completeInitialSetup(String passwordHash, String nickname) {
        if (!this.firstLogin) {
            throw new IllegalStateException("이미 최초 설정을 완료한 계정입니다.");
        }

        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.firstLogin = false;
    }
}