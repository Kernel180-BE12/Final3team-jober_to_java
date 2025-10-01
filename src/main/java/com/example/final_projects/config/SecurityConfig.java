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
                "http://localhost:3000",
                "http://localhost:5173",
                "https://lee.telosform.shop",
                "https://www.telosform.shop",
                "https://final-team3-fe.vercel.app"
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
                .csrf(csrf -> {
                    if (!csrfEnabled) {
                        csrf.disable();
                    } else {
                        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
                        repo.setCookieCustomizer(c -> {
                            c.sameSite("None");
                            c.secure(false); // HTTP 환경이라 false, 운영 HTTPS는 true
                            c.path("/");
                        });
                        repo.setHeaderName("X-CSRF-Token");
                        csrf.csrfTokenRepository(repo)
                                .ignoringRequestMatchers(
                                        // ✅ AntPathRequestMatcher 대신 새로운 DSL
                                        request -> request.getRequestURI().startsWith("/api/auth/email/otp/")
                                                && request.getMethod().equalsIgnoreCase("POST"),
                                        request -> request.getRequestURI().equals("/api/auth/signup")
                                                && request.getMethod().equalsIgnoreCase("POST"),
                                        request -> request.getRequestURI().equals("/api/auth/login")
                                                && request.getMethod().equalsIgnoreCase("POST"),
                                        request -> request.getRequestURI().startsWith("/actuator/"),
                                        request -> request.getRequestURI().startsWith("/swagger-ui/"),
                                        request -> request.getRequestURI().startsWith("/v3/api-docs/")
                                );
                    }
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/actuator/**",
                                "/api/auth/csrf",
                                "/api/auth/email/**",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/token/refresh",
                                "/api/auth/logout",
                                "/", "/index.html", "/favicon.ico",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new com.example.final_projects.security.UserAuthEntryPoint(objectMapper))
                        .accessDeniedHandler(new com.example.final_projects.security.UserAccessDeniedHandler(objectMapper))
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
