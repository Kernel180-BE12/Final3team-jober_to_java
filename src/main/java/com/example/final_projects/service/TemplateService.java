package com.example.final_projects.service;

import com.example.final_projects.dto.PageResponse;
import com.example.final_projects.dto.template.*;
import com.example.final_projects.entity.*;
import com.example.final_projects.exception.TemplateException;
import com.example.final_projects.exception.code.TemplateErrorCode;
import com.example.final_projects.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateHistoryRepository templateHistoryRepository;
    private final RestClient restClient;
    private final TemplateButtonRepository templateButtonRepository;
    private final TemplateVariableRepository templateVariableRepository;
    private final UserTemplateRequestRepository userTemplateRequestRepository;
    private final IndustryRepository industryRepository;
    private final PurposeRepository purposeRepository;

    public TemplateService(
            TemplateRepository templateRepository,
            TemplateHistoryRepository templateHistoryRepository,
            RestClient restClient,
            TemplateButtonRepository templateButtonRepository,
            TemplateVariableRepository templateVariableRepository,
            UserTemplateRequestRepository userTemplateRequestRepository,
            IndustryRepository industryRepository,
            PurposeRepository purposeRepository
    ) {
        this.templateRepository = templateRepository;
        this.templateHistoryRepository = templateHistoryRepository;
        this.restClient = restClient;
        this.templateButtonRepository = templateButtonRepository;
        this.templateVariableRepository = templateVariableRepository;
        this.userTemplateRequestRepository = userTemplateRequestRepository;
        this.industryRepository = industryRepository;
        this.purposeRepository = purposeRepository;
    }

    public PageResponse<TemplateResponse> getTemplates(Long userId, TemplateStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<Template> templatePage = templateRepository.findByUserIdAndStatus(userId, status, pageRequest);

        List<TemplateResponse> data = templatePage.getContent().stream()
                .map(TemplateResponse::from)
                .toList();

        return new PageResponse<>(data, page, size, templatePage.getTotalElements());
    }

    public TemplateResponse getTemplateById(Long templateId, Long userId) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다"));

        if (template.getUserId() == null || !template.getUserId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
        return TemplateResponse.from(template);
    }

    @Transactional
    public TemplateResponse createTemplate(Long userId, TemplateCreateRequest request) {
        UserTemplateRequest userRequest = createUserTemplateRequest(userId, request);

        try {
            AiTemplateResponse aiResponse = requestAiTemplate(userId, request);

            Template template = createAndSaveTemplate(userId, aiResponse, userRequest);

            saveTemplateHistory(template);

            markRequestCompleted(userRequest);

            return TemplateResponse.from(template);

        } catch (Exception e) {
            markRequestFailed(userRequest, e);
            throw e;
        }
    }

    private UserTemplateRequest createUserTemplateRequest(Long userId, TemplateCreateRequest request) {
        UserTemplateRequest userRequest = UserTemplateRequest.builder()
                .userId(userId)
                .requestContent(request.getRequestContent())
                .status(UserTemplateRequestStatus.PENDING)
                .build();
        return userTemplateRequestRepository.save(userRequest);
    }

    private AiTemplateResponse requestAiTemplate(Long userId, TemplateCreateRequest request) {
        AiTemplateResponse aiResponse = restClient.post()
                .uri("/ai/templates")
                .body(new AiTemplateRequest(userId, request.getRequestContent()))
                .retrieve()
                .body(AiTemplateResponse.class);

        if (aiResponse == null) {
            throw new IllegalStateException("AI 서버 응답이 없습니다");
        }
        return aiResponse;
    }

    private Template createAndSaveTemplate(Long userId, AiTemplateResponse aiResponse, UserTemplateRequest userRequest) {
        Template template = Template.builder()
                .userId(userId)
                .categoryId(aiResponse.categoryId())
                .title(aiResponse.title())
                .content(aiResponse.content())
                .imageUrl(aiResponse.imageUrl())
                .type(TemplateType.valueOf(aiResponse.type()))
                .isPublic(aiResponse.isPublic())
                .status(TemplateStatus.CREATED)
                .userTemplateRequest(userRequest)
                .build();

        templateRepository.save(template);

        saveTemplateButtons(template, aiResponse);
        saveTemplateVariables(template, aiResponse);
        saveTemplateIndustries(template, aiResponse);
        saveTemplatePurposes(template, aiResponse);

        return templateRepository.save(template);
    }

    private void saveTemplateButtons(Template template, AiTemplateResponse aiResponse) {
        if (aiResponse.buttons() == null) return;

        aiResponse.buttons().forEach(b -> templateButtonRepository.save(
                TemplateButton.builder()
                        .template(template)
                        .name(b.name())
                        .ordering(b.ordering())
                        .linkPc(b.linkPc())
                        .linkAnd(b.linkAnd())
                        .linkIos(b.linkIos())
                        .createdAt(LocalDateTime.now())
                        .build()
        ));
    }

    private void saveTemplateVariables(Template template, AiTemplateResponse aiResponse) {
        if (aiResponse.variables() == null) return;

        aiResponse.variables().forEach(v -> {
            TemplateVariable variable = TemplateVariable.builder()
                    .template(template)
                    .variableKey(v.variableKey())
                    .placeholder(v.placeholder())
                    .inputType(v.inputType())
                    .createdAt(LocalDateTime.now())
                    .build();
            templateVariableRepository.save(variable);
            template.getVariables().add(variable);
        });
    }

    private void saveTemplateIndustries(Template template, AiTemplateResponse aiResponse) {
        if (aiResponse.industries() == null) return;

        aiResponse.industries().forEach(i -> {
            Industry industry = industryRepository.findById(i.id())
                    .orElseThrow(() -> new IllegalArgumentException("Industry not found: " + i.id()));
            template.getIndustries().add(industry);
        });
    }

    private void saveTemplatePurposes(Template template, AiTemplateResponse aiResponse) {
        if (aiResponse.purposes() == null) return;

        aiResponse.purposes().forEach(p -> {
            Purpose purpose = purposeRepository.findById(p.id())
                    .orElseThrow(() -> new IllegalArgumentException("Purpose not found: " + p.id()));
            template.getPurposes().add(purpose);
        });
    }

    private void saveTemplateHistory(Template template) {
        templateHistoryRepository.save(
                TemplateHistory.builder()
                        .template(template)
                        .status(TemplateStatus.CREATED)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    private void markRequestCompleted(UserTemplateRequest userRequest) {
        userRequest.setStatus(UserTemplateRequestStatus.COMPLETED);
        userTemplateRequestRepository.save(userRequest);
    }

    private void markRequestFailed(UserTemplateRequest userRequest, Exception e) {
        userRequest.setStatus(UserTemplateRequestStatus.FAILED);
        userTemplateRequestRepository.save(userRequest);
    }

    @Transactional
    public TemplateApproveResponse approveTemplate(Long templateId, Long userId) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateException(TemplateErrorCode.TEMPLATE_NOT_FOUND));

        if (!template.getUserId().equals(userId)) {
            throw new TemplateException(TemplateErrorCode.FORBIDDEN_TEMPLATE);
        }

        if (template.getStatus() == TemplateStatus.APPROVE_REQUESTED) {
            throw new TemplateException(TemplateErrorCode.ALREADY_APPROVE_REQUESTED);
        }

        if (template.getStatus() != TemplateStatus.CREATED) {
            throw new TemplateException(TemplateErrorCode.APPROVE_REQUEST_FORBIDDEN);
        }

        template.setStatus(TemplateStatus.APPROVE_REQUESTED);

        templateRepository.save(template);

        templateHistoryRepository.save(
                TemplateHistory.builder()
                        .template(template)
                        .status(TemplateStatus.APPROVE_REQUESTED)
                        .build()
        );

        return new TemplateApproveResponse(template.getId(), template.getStatus().name());
    }
}
