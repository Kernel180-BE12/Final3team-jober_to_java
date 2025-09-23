package com.example.final_projects.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpStatusCodeException;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerConfigCustomizer aiServiceCircuitBreakerCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of("aiService", builder -> builder
                        .recordException(throwable -> {
                            if (throwable instanceof HttpStatusCodeException ex) {
                                return ex.getStatusCode().is5xxServerError(); // 500대만 실패로 카운트
                            }
                            return true;
                        })
                );
    }
}
