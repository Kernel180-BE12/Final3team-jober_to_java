package com.example.final_projects.support;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {
    private final SecureRandom rng = new SecureRandom();

    public String numeric(int n){
        StringBuilder sb = new StringBuilder(n);
        for(int i = 0; i < n; i++) sb.append(rng.nextInt(10));
        return sb.toString();
    }
}
