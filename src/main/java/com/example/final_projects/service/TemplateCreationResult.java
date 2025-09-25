package com.example.final_projects.service;

import com.example.final_projects.dto.template.AiTemplateResponse;
import com.example.final_projects.dto.template.TemplateResponse;

public sealed interface TemplateCreationResult {
    record Complete(TemplateResponse template) implements TemplateCreationResult {}

    record Incomplete(AiTemplateResponse partialTemplate) implements TemplateCreationResult {}
}
