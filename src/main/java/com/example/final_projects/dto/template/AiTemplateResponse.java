package com.example.final_projects.dto.template;

import java.time.LocalDateTime;
import java.util.List;

public record AiTemplateResponse(
        Long id,
        Long userId,
        Long categoryId,
        String title,
        String content,
        String imageUrl,
        String type,
        Boolean isPublic,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ButtonDto> buttons,
        List<VariableDto> variables,
        List<IndustryDto> industries,
        List<PurposeDto> purposes
) {
    public record ButtonDto(Long id, String name, int ordering,
                            String linkPc, String linkAnd, String linkIos) {}
    public record VariableDto(Long id, String variableKey, String placeholder, String inputType) {}
    public record IndustryDto(Long id, String name) {}
    public record PurposeDto(Long id, String name) {}
}