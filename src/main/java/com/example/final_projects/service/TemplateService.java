package com.example.final_projects.service;

import com.example.final_projects.dto.PageResponse;
import com.example.final_projects.dto.template.TemplateResponse;
import com.example.final_projects.entity.Template;
import com.example.final_projects.entity.TemplateStatus;
import com.example.final_projects.repository.TemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public PageResponse<TemplateResponse> getTemplates(Long userId, String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<Template> templatePage = templateRepository.findByUserIdAndStatus(userId, TemplateStatus.valueOf(status), pageRequest);

        List<TemplateResponse> data = templatePage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(data, page, size, templatePage.getTotalElements());
    }

    private TemplateResponse toResponse(Template template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .userId(template.getUserId())
                .categoryId(template.getCategoryId())
                .title(template.getTitle())
                .content(template.getContent())
                .imageUrl(template.getImageUrl())
                .type(template.getType().name())
                .isPublic(template.getIsPublic())
                .status(template.getStatus().name())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .buttons(template.getButtons().stream()
                        .map(b -> TemplateResponse.ButtonResponse.builder()
                                .id(b.getId())
                                .name(b.getName())
                                .ordering(b.getOrdering())
                                .linkPc(b.getLinkPc())
                                .linkAnd(b.getLinkAnd())
                                .linkIos(b.getLinkIos())
                                .build())
                        .collect(Collectors.toList()))
                .variables(template.getVariables().stream()
                        .map(v -> TemplateResponse.VariableResponse.builder()
                                .id(v.getId())
                                .variableKey(v.getVariableKey())
                                .placeholder(v.getPlaceholder())
                                .inputType(v.getInputType())
                                .build())
                        .collect(Collectors.toList()))
                .industries(template.getIndustries().stream()
                        .map(i -> TemplateResponse.IndustryResponse.builder()
                                .id(i.getId())
                                .name(i.getName())
                                .build())
                        .collect(Collectors.toList()))
                .purposes(template.getPurposes().stream()
                        .map(p -> TemplateResponse.PurposeResponse.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
