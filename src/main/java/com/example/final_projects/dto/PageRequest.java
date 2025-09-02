package com.example.final_projects.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest {
    @Min(value = 1, message = "page는 1 이상이어야 합니다.")
    private int page = 1;
    @Min(value = 1, message = "size는 1 이상이어야 합니다.")
    @Max(value = 100, message = "size는 100 이하여야 합니다.")
    private int size = 10;
}
