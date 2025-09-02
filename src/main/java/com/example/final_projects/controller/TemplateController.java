package com.example.final_projects.controller;

import com.example.final_projects.dto.PageResponse;
import com.example.final_projects.dto.template.TemplateCreateRequest;
import com.example.final_projects.dto.template.TemplateResponse;
import com.example.final_projects.security.CustomUserPrincipal;
import com.example.final_projects.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public PageResponse<TemplateResponse> getTemplates(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String status
    ) {
        return templateService.getTemplates(principal.getId(), status, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplateById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        TemplateResponse response = templateService.getTemplateById(id, principal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody TemplateCreateRequest templateCreateRequest
    ) {
        TemplateResponse response = templateService.createTemplate(principal.getId(), templateCreateRequest);
        return ResponseEntity.ok(response);
    }
}
