package com.example.final_projects.service;

import com.example.final_projects.dto.template.AiTemplateRequest;
import com.example.final_projects.dto.template.AiTemplateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiRestClient {

    private final RestClient restClient;

    public AiRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public AiTemplateResponse createTemplate(Long userId, String requestContent) {
        AiTemplateResponse response = restClient.post()
                .uri("/ai/templates")
                .body(new AiTemplateRequest(userId, requestContent))
                .retrieve()
                .body(AiTemplateResponse.class);

        if (response == null) {
            throw new IllegalStateException("AI 서버 응답이 없습니다");
        }
        return response;
    }
}
