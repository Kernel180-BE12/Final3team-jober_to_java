package com.example.final_projects.config;

import com.example.final_projects.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.time.Duration;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @org.springframework.beans.factory.annotation.Value("${app.security.csrf.enabled:false}")
    private boolean csrfEnabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of(
                "http://localhost:3000", // dev
                "http://localhost:5173", // vite dev server
                "https://lee.telosform.shop", // vercel url
                "https://www.telosform.shop", // vercel url
                "https://final-team3-fe.vercel.app" // vercel url
                //배포 url 넣어야함
        ));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        c.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                // CSRF: dev/prod 토글
                .csrf(csrf -> {
                    if (!csrfEnabled) {
                        csrf.disable();
                    } else {
                        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
                        // 크로스사이트면 쿠키도 None+Secure (HTTPS 전제)
                        repo.setCookieCustomizer(c -> {
                            c.sameSite("None");
                            c.secure(true);
                            c.path("/");
                        });
                        repo.setHeaderName("X-CSRF-Token");
                        csrf.csrfTokenRepository(repo)
                                .ignoringRequestMatchers(
                                        "/actuator/**",
                                        "/swagger-ui/**", "/v3/api-docs/**"
                                );
                    }
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 전부 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 공개 경로 (로그인/가입/OTP/CSRF만 공개)
                        .requestMatchers(
                                "/actuator/**",
                                "/api/auth/csrf",
                                "/api/auth/email/**",
                                "/", "/index.html", "/favicon.ico",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
                        ).permitAll()
                        .requestMatchers(
                           "/admin/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(new com.example.final_projects.security.UserAuthEntryPoint(objectMapper))
                    .accessDeniedHandler(new com.example.final_projects.security.UserAccessDeniedHandler(objectMapper))
                );
        // JWT 필터 활성화: 예외는 EntryPoint/DeniedHandler가 통일 포맷으로 처리
         http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
