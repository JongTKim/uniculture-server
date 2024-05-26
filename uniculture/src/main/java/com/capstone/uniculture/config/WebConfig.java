package com.capstone.uniculture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 모든 Origin 허용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE") // 허용할 HTTP 메서드 지정
                .allowedHeaders("*"); // 모든 요청 헤더 허용
    }
}