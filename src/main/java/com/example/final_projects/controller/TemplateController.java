package com.example.final_projects.controller;

import com.example.final_projects.dto.PageResponse;
import com.example.final_projects.dto.template.TemplateResponse;
import com.example.final_projects.security.CustomUserPrincipal;
import com.example.final_projects.service.TemplateService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/api/templates")
    public PageResponse<TemplateResponse> getTemplates(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String status
    ) {
        return templateService.getTemplates(principal.getId(), status, page, size);
    }
}
