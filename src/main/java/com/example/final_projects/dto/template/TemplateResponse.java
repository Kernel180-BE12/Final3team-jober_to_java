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
