package com.example.final_projects.dto.template;

import com.example.final_projects.entity.TemplateStatus;
import com.example.final_projects.entity.TemplateType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record AiTemplateResponse(
        Long id,
        Long userId,
        String categoryId,
        String title,
        String content,
        String imageUrl,
        String type,
        Boolean isPublic,
        String status,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt,
        List<ButtonDto> buttons,
        List<VariableDto> variables,
        List<IndustryDto> industries,
        List<PurposeDto> purposes
) {
    public record ButtonDto(
            Long id,
            String name,
            int ordering,
            String linkPc,
            String linkAnd,
            String linkIos
    ) {}

    public record VariableDto(
            Long id,
            String variableKey,
            String placeholder,
            String inputType
    ) {}

    public record IndustryDto(
            Long id,
            String name
    ) {}

    public record PurposeDto(
            Long id,
            String name
    ) {}

    public TemplateStatus safeStatus() {
        if (status == null || status.isBlank()) {
            return TemplateStatus.CREATED;
        }
        try {
            return TemplateStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return TemplateStatus.CREATED;
        }
    }

    public TemplateType safeType() {
        if (type == null || type.isBlank()) {
            return TemplateType.MESSAGE;
        }
        try {
            return TemplateType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return TemplateType.MESSAGE;
        }
    }
}
