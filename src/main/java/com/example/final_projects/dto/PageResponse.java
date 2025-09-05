package com.example.final_projects.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long total;
}
