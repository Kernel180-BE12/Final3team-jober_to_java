package com.example.final_projects.dto.template;

import com.example.final_projects.dto.PageRequest;
import com.example.final_projects.entity.TemplateStatus;
import com.example.final_projects.exception.TemplateException;
import com.example.final_projects.exception.code.TemplateErrorCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TemplateSearchRequest extends PageRequest {
    private static final Set<TemplateStatus> ALLOWED_STATUSES =
            Set.of(TemplateStatus.APPROVE_REQUESTED, TemplateStatus.APPROVED, TemplateStatus.REJECTED);

    @NotBlank(message = "status는 필수입니다.")
    private String status;

    public TemplateStatus validateStatus() {
        TemplateStatus templateStatus;
        try {
            templateStatus = TemplateStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new TemplateException(TemplateErrorCode.INVALID_STATUS);
        }

        if (!ALLOWED_STATUSES.contains(templateStatus)) {
            throw new TemplateException(TemplateErrorCode.FORBIDDEN_STATUS);
        }

        return templateStatus;
    }
}
