package com.otter.intranet.employee;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_number", nullable = false, unique = true, length = 30)
    private String employeeNumber;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nickname", length = 30)
    private String nickname;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;
}
