package com.example.final_projects.dto.template;

public record AiErrorResponse(
        String code,
        String message,
        String timestamp
) {}
