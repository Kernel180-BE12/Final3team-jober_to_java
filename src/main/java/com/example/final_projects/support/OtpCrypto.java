package com.example.final_projects.support;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class OtpCrypto {

    public String sha256WithPepper(String value, String pepper){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pepper.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch(Exception e){
            throw new IllegalStateException("OTP hashing error", e);
        }
    }

    public boolean slowEquals(String a, String b){
        if(a == null || b == null) return false;
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        if(x.length != y.length) return false;
        int r= 0;
        for (int i = 0; i < x.length; i++) r |= (x[i] ^ y[i]);
        return r == 0;
    }

    // 평문 OTP와 해시의 일치 여부 검증
    public boolean verify(String plainOtp, String storedHash, String pepper) {
        String inputHash = sha256WithPepper(plainOtp, pepper);
        return slowEquals(inputHash, storedHash);
    }
}
