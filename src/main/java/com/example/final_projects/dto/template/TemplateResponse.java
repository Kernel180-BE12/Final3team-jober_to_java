package com.example.final_projects.dto.template;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TemplateResponse {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String content;
    private String imageUrl;
    private String type;
    private Boolean isPublic;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ButtonResponse> buttons;
    private List<VariableResponse> variables;
    private List<IndustryResponse> industries;
    private List<PurposeResponse> purposes;

    public static TemplateResponse from(com.example.final_projects.entity.Template template) {
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
                        .map(b -> ButtonResponse.builder()
                                .id(b.getId())
                                .name(b.getName())
                                .ordering(b.getOrdering())
                                .linkPc(b.getLinkPc())
                                .linkAnd(b.getLinkAnd())
                                .linkIos(b.getLinkIos())
                                .build())
                        .toList())
                .variables(template.getVariables().stream()
                        .map(v -> VariableResponse.builder()
                                .id(v.getId())
                                .variableKey(v.getVariableKey())
                                .placeholder(v.getPlaceholder())
                                .inputType(v.getInputType())
                                .build())
                        .toList())
                .industries(template.getIndustries().stream()
                        .map(i -> IndustryResponse.builder()
                                .id(i.getId())
                                .name(i.getName())
                                .build())
                        .toList())
                .purposes(template.getPurposes().stream()
                        .map(p -> PurposeResponse.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .build())
                        .toList())
                .build();
    }

    @Getter
    @Builder
    public static class ButtonResponse {
        private Long id;
        private String name;
        private Integer ordering;
        private String linkPc;
        private String linkAnd;
        private String linkIos;
    }

    @Getter
    @Builder
    public static class VariableResponse {
        private Long id;
        private String variableKey;
        private String placeholder;
        private String inputType;
    }

    @Getter
    @Builder
    public static class IndustryResponse {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class PurposeResponse {
        private Long id;
        private String name;
    }
}
