package com.example.final_projects.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(0) // ✅ 최우선 체인
public class AllowAllSecurity {
    @Bean
    SecurityFilterChain allowAll(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")                // ✅ 모든 요청 이 체인이 먼저 받음
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a.anyRequest().permitAll());
        return http.build();
    }
}
