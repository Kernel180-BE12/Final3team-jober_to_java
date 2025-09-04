package com.example.final_projects.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorReason {
    private final int status;
    private final String code;
    private final String message;
}
