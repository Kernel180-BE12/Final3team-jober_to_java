package com.example.final_projects.service;

import com.example.final_projects.dto.template.AiTemplateRequest;
import com.example.final_projects.dto.template.AiTemplateResponse;
import com.example.final_projects.entity.UserTemplateRequest;
import com.example.final_projects.exception.AiException;
import com.example.final_projects.exception.code.AiErrorCode;
import com.example.final_projects.repository.UserTemplateRequestFailureLogRepository;
import com.example.final_projects.repository.UserTemplateRequestRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiRestClient {

    private final RestClient restClient;

    public AiRestClient(RestClient restClient,
                        UserTemplateRequestRepository userTemplateRequestRepository,
                        UserTemplateRequestFailureLogRepository failureLogRepository) {
        this.restClient = restClient;
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackTemplate")
    public AiTemplateResponse createTemplate(UserTemplateRequest userTemplateRequest) {
        AiTemplateResponse response = restClient.post()
                .uri("/ai/templates")
                .body(new AiTemplateRequest(userTemplateRequest.getUserId(), userTemplateRequest.getRequestContent()))
                .retrieve()
                .body(AiTemplateResponse.class);

        if (response == null) {
            throw new AiException(AiErrorCode.AI_REQUEST_FAILED);
        }
        return response;
    }

    private AiTemplateResponse fallbackTemplate(UserTemplateRequest userTemplateRequest, Throwable t) {
        throw new AiException(AiErrorCode.AI_REQUEST_FAILED);
    }
}
