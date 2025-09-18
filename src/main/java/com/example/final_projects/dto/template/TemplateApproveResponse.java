package com.example.final_projects.dto.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemplateApproveResponse {
    private Long templateId;
    private String status;
}
