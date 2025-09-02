package com.example.final_projects.dto.template;

import com.example.final_projects.dto.PageRequest;
import com.example.final_projects.entity.TemplateStatus;
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
            throw new IllegalArgumentException("잘못된 status 값입니다: " + status);
        }
        if (!ALLOWED_STATUSES.contains(templateStatus)) {
            throw new IllegalArgumentException("허용되지 않은 status 값입니다: " + status);
        }
        return templateStatus;
    }
}
