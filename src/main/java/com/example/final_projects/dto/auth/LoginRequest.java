package com.example.final_projects.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @Email @NotBlank private String email;
    @NotBlank private String password;
    public LoginRequest(){}
    public LoginRequest(String e, String p){ this.email=e; this.password=p; }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
