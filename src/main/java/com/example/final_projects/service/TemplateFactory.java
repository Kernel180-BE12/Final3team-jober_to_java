package com.example.final_projects.service;

import com.example.final_projects.dto.template.AiTemplateResponse;
import com.example.final_projects.entity.*;
import com.example.final_projects.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TemplateFactory {

    private final TemplateRepository templateRepository;
    private final IndustryRepository industryRepository;
    private final PurposeRepository purposeRepository;

    @Transactional
    public Template createFrom(Long userId, AiTemplateResponse aiResponse, UserTemplateRequest userRequest) {
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

        List<TemplateButton> newButtons = mapButtonsFromDto(aiResponse);
        List<TemplateVariable> newVariables = mapVariablesFromDto(aiResponse);
        template.addButtons(newButtons);
        template.addVariables(newVariables);
        associateIndustries(template, aiResponse);
        associatePurposes(template, aiResponse);
        return templateRepository.save(template);
    }

    private List<TemplateButton> mapButtonsFromDto(AiTemplateResponse aiResponse) {
        if (aiResponse.buttons() == null || aiResponse.buttons().isEmpty()) {
            return new ArrayList<>();
        }
        return aiResponse.buttons().stream()
                .map(b -> TemplateButton.builder()
                        .name(b.name())
                        .ordering(b.ordering())
                        .linkMo(b.linkMo())
                        .linkPc(b.linkPc())
                        .linkAnd(b.linkAnd())
                        .linkIos(b.linkIos())
                        .linkType(b.linkType())
                        .build())
                .toList();
    }

    private List<TemplateVariable> mapVariablesFromDto(AiTemplateResponse aiResponse) {
        if (aiResponse.variables() == null || aiResponse.variables().isEmpty()) {
            return new ArrayList<>();
        }
        return aiResponse.variables().stream()
                .map(v -> TemplateVariable.builder()
                        .variableKey(v.variableKey())
                        .placeholder(v.placeholder())
                        .inputType(v.inputType())
                        .build())
                .toList();
    }

    private void associateIndustries(Template template, AiTemplateResponse aiResponse) {
        if (aiResponse.industries() == null) return;
        for (AiTemplateResponse.IndustryDto industryDto : aiResponse.industries()) {
            industryRepository.findById(industryDto.id())
                    .ifPresent(industry -> template.getIndustries().add(industry));
        }
    }

    private void associatePurposes(Template template, AiTemplateResponse aiResponse) {
        if (aiResponse.purposes() == null) return;
        for (AiTemplateResponse.PurposeDto purposeDto : aiResponse.purposes()) {
            purposeRepository.findById(purposeDto.id())
                    .ifPresent(purpose -> template.getPurposes().add(purpose));
        }
    }
}
