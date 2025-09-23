package com.example.final_projects.service;

import com.example.final_projects.dto.template.AiTemplateRequest;
import com.example.final_projects.dto.template.AiTemplateResponse;
import com.example.final_projects.entity.UserTemplateRequest;
import com.example.final_projects.entity.UserTemplateRequestStatus;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Import(AiRestClient.class)
class AiRestClientTest {

    @Autowired
    private AiRestClient aiRestClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${resilience4j.circuitbreaker.instances.aiService.waitDurationInOpenState}")
    private Duration waitDuration;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8000))
            .build();

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + wireMock.getPort())
                .build();
    }

    private void stub500Error(String url) {
        wireMock.stubFor(WireMock.post(url)
                .willReturn(WireMock.aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));
    }

    @Test
    void InternalServerError_예외가_발생해야_한다() {
        stub500Error("/ai/templates");

        Throwable thrown = catchThrowable(() -> restClient().post()
                .uri("/ai/templates")
                .body(new AiTemplateRequest(123L, "테스트 내용"))
                .retrieve()
                .body(AiTemplateResponse.class));

        then(thrown).as("서버가 500을 반환하면 InternalServerError 예외가 발생해야 한다")
                .isInstanceOf(HttpServerErrorException.InternalServerError.class);
    }

    @Test
    void 연속된_실패_이후_CircuitBreaker가_OPEN_상태가_되어야_한다() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("aiService");

        cb.getEventPublisher()
                .onStateTransition(event -> System.out.println("[CB] 상태 전환: " + event.getStateTransition()))
                .onError(event -> System.out.println("[CB] 호출 실패: " + event.getEventType()));

        for (int i = 0; i < 5; i++) {
            UserTemplateRequest userRequest = UserTemplateRequest.builder()
                    .userId(1L)
                    .requestContent("fail-case")
                    .status(UserTemplateRequestStatus.PENDING) // 기본값
                    .build();

            assertThrows(RuntimeException.class, () ->
                    aiRestClient.createTemplate(userRequest));
        }

        assertThat(cb.getState())
                .as("CircuitBreaker는 연속 실패 후 OPEN 상태가 되어야 한다")
                .isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void waitDuration이_지난_후_HALF_OPEN_상태로_전환되어야_한다() throws InterruptedException {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("aiService");
        cb.transitionToOpenState();

        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        Thread.sleep(waitDuration.toMillis() + 500);

        assertThat(cb.getState())
                .as("waitDuration 이후 HALF_OPEN 상태로 전환되어야 한다")
                .isEqualTo(CircuitBreaker.State.HALF_OPEN);
    }
}
