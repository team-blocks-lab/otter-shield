package com.otter.intranet.config;

import com.otter.intranet.auth.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 게시판 API의 로그인 및 최초 설정 검사 적용
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/post", "/api/post/**");
    }
}