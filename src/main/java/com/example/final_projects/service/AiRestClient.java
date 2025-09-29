package com.example.final_projects.service;

import com.example.final_projects.dto.template.AiApiResponse;
import com.example.final_projects.dto.template.AiErrorResponse;
import com.example.final_projects.dto.template.AiTemplateRequest;
import com.example.final_projects.dto.template.AiTemplateResponse;
import com.example.final_projects.entity.UserTemplateRequest;
import com.example.final_projects.exception.AiException;
import com.example.final_projects.exception.code.AiErrorCode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class AiRestClient {

    private static final Logger log = LoggerFactory.getLogger(AiRestClient.class);
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AiRestClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackTemplate")
    public ResponseEntity<AiApiResponse<AiTemplateResponse>> createTemplate(UserTemplateRequest userTemplateRequest) {
        AiTemplateRequest requestBody = new AiTemplateRequest(
                userTemplateRequest.getUserId(),
                userTemplateRequest.getRequestContent()
        );

        log.info("Sending request to AI server. URI: /ai/templates, UserId: {}", requestBody.userId());

        return restClient.post()
                .uri("/ai/templates")
                .body(requestBody)
                .exchange((request, response) -> {
                    try {
                        String responseBodyString = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        log.info("Received raw response from AI server. Status: {}, Raw Body:\n{}", response.getStatusCode(), responseBodyString);

                        AiApiResponse<AiTemplateResponse> apiResponse = objectMapper.readValue(
                                responseBodyString,
                                new TypeReference<AiApiResponse<AiTemplateResponse>>() {}
                        );

                        return ResponseEntity
                                .status(response.getStatusCode())
                                .body(apiResponse);

                    } catch (IOException e) {
                        log.error("Failed to read or parse AI server response body.", e);
                        throw new AiException(
                                AiErrorCode.AI_REQUEST_FAILED,
                                "Failed to process AI server response",
                                e
                        );
                    }
                });
    }

    private ResponseEntity<AiApiResponse<AiTemplateResponse>> fallbackTemplate(UserTemplateRequest userTemplateRequest, Throwable t) {
        log.error(
                "CircuitBreaker fallback triggered for UserId: {}. Root cause: {} - {}",
                userTemplateRequest.getUserId(),
                t.getClass().getName(),
                t.getMessage(),
                t
        );

        AiErrorResponse error = new AiErrorResponse("SERVICE_UNAVAILABLE", "AI 서비스가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
        AiApiResponse<AiTemplateResponse> fallbackResponse = new AiApiResponse<>(null, null, error);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
}
