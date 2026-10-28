package com.otter.ott.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 회원가입 시 로그인 ID 중복 여부 조회
    boolean existsByLoginId(String loginId);

    // 로그인 시 로그인 ID에 해당하는 회원 조회
    Optional<Member> findByLoginId(String loginId);
}