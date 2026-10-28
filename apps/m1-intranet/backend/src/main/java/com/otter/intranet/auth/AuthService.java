package com.otter.intranet.auth;

import com.otter.intranet.auth.dto.InitialSetupRequest;
import com.otter.intranet.auth.dto.LoginRequest;
import com.otter.intranet.auth.dto.LoginResponse;
import com.otter.intranet.employee.Employee;
import com.otter.intranet.employee.EmployeeRepository;
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

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        validatePasswordBytes(request.password());

        Employee employee = employeeRepository
                .findByEmployeeNumber(request.employeeNumber())
                .orElseThrow(this::invalidCredentials);

        // 입력한 비밀번호와 저장된 해시의 일치 여부 확인
        if (!passwordEncoder.matches(
                request.password(), employee.getPasswordHash())) {
            throw invalidCredentials();
        }

        return toResponse(employee);
    }

    public LoginResponse getCurrentEmployee(Long employeeId) {
        return toResponse(findEmployee(employeeId));
    }

    @Transactional
    public void completeInitialSetup(
            Long employeeId,
            InitialSetupRequest request
    ) {
        validatePasswordBytes(request.newPassword());

        Employee employee = findEmployee(employeeId);

        // 최초 설정의 중복 처리 차단
        if (!employee.isFirstLogin()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 최초 설정을 완료한 계정입니다."
            );
        }

        // 초기 비밀번호의 재사용 차단
        if (passwordEncoder.matches(
                request.newPassword(), employee.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "기존 비밀번호와 다른 비밀번호를 입력해 주세요."
            );
        }

        // 새 비밀번호 해시 및 닉네임 반영
        employee.completeInitialSetup(
                passwordEncoder.encode(request.newPassword()),
                request.nickname()
        );
    }

    private void validatePasswordBytes(String password) {
        // BCrypt 입력 제한에 따른 UTF-8 바이트 길이 검사
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "비밀번호는 UTF-8 기준 72바이트 이하여야 합니다."
            );
        }
    }

    private Employee findEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "로그인이 필요합니다."
                ));
    }

    private LoginResponse toResponse(Employee employee) {
        return new LoginResponse(
                employee.getEmployeeId(),
                employee.getNickname(),
                employee.isFirstLogin()
        );
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "직원 번호 또는 비밀번호가 올바르지 않습니다."
        );
    }
}