package com.example.final_projects.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
        @Email @NotBlank private String email;
        @NotBlank(message = "비밀번호는 필수입니다.") @Size(min=8,max=64, message = "비밀번호는 8자 이상이어야 합니다.") private String password;
        @NotBlank @Size(min=1,max=30) private String name; // or name
        @NotBlank private String emailVerificationToken;

        public SignupRequest() {}
        public SignupRequest(String e, String p, String n, String e1){ this.email=e; this.password=p; this.name=n; this.emailVerificationToken=e1; }
        // getters/setters

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }
}

