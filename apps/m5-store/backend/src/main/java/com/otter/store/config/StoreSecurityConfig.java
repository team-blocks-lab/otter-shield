package com.otter.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
public class StoreSecurityConfig {

    @Bean
    public SecurityFilterChain storeApiSecurity(HttpSecurity http)
            throws Exception {
        return http
                .securityMatcher("/api/**")

                // 세션에 저장된 토큰으로 데이터 변경 요청 검증
                .csrf(csrf -> csrf.csrfTokenRepository(
                        new HttpSessionCsrfTokenRepository()
                ))

                // 로그인 및 데이터 소유권 확인은 기존 Store 코드에서 처리
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                // 기존 JSON 로그인·로그아웃 API 사용
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)

                .build();
    }
}