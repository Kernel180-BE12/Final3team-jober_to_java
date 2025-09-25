package com.example.final_projects.service;

import com.example.final_projects.entity.UserTemplateRequest;
import com.example.final_projects.support.MailService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class AiRestClientTest {

    @Autowired
    private AiRestClient aiRestClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${resilience4j.circuitbreaker.instances.aiService.waitDurationInOpenState}")
    private Duration waitDuration;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MailService mailService() {
            return Mockito.mock(MailService.class);
        }
    }

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("rest.ai.base-url", wireMock::baseUrl);
    }

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry.circuitBreaker("aiService").reset();
    }

    @Test
    @DisplayName("연속된 실패 이후 CircuitBreaker가 OPEN 상태가 되어 fallback이 동작해야 한다")
    void circuitBreaker_should_open_after_consecutive_failures() {
        // given: AI 서버가 계속 503 서버 에러를 반환하도록 설정
        wireMock.stubFor(WireMock.post("/ai/templates")
                .willReturn(WireMock.serverError().withStatus(503)));

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("aiService");
        int failureThreshold = cb.getCircuitBreakerConfig().getMinimumNumberOfCalls();

        // when: 임계치만큼 일부러 실패를 발생시킴
        IntStream.range(0, failureThreshold).forEach(i -> {
            try {
                aiRestClient.createTemplate(UserTemplateRequest.builder().userId(1L).requestContent("fail").build());
            } catch (Exception e) {

            }
        });

        // then: 임계치를 넘었으므로, 다음 호출은 폴백(fallback)이 동작해야 함
        UserTemplateRequest finalRequest = UserTemplateRequest.builder().userId(103L).requestContent("final-call").build();
        ResponseEntity<?> responseEntity = aiRestClient.createTemplate(finalRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("waitDuration이 지난 후 HALF_OPEN 상태로 전환되어야 한다")
    void waitDuration_should_transition_to_half_open() throws InterruptedException {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("aiService");
        cb.transitionToOpenState();

        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        Thread.sleep(waitDuration.toMillis() + 500);

        assertThat(cb.getState())
                .as("waitDuration 이후 HALF_OPEN 상태로 전환되어야 한다")
                .isEqualTo(CircuitBreaker.State.HALF_OPEN);
    }
}
