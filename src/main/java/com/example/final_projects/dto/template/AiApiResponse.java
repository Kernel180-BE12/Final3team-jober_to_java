package com.example.final_projects.dto.template;

public record AiApiResponse<T>(
        T data,
        String message,
        AiErrorResponse error
) {
    public boolean isSuccess() {
        return error == null && data != null;
    }
}
