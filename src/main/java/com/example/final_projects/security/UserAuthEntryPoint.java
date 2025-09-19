package com.example.final_projects.security;

import com.example.final_projects.dto.ApiResult;
import com.example.final_projects.dto.ErrorResponse;
import com.example.final_projects.exception.user.UserErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class UserAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper om;
    public UserAuthEntryPoint(ObjectMapper om){ this.om = om; }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
                         org.springframework.security.core.AuthenticationException e) throws IOException {
        var code = UserErrorCode.AUTH_REQUIRED;
        var body = ApiResult.<Void>builder()
                .data(null)
                .error(ErrorResponse.of(code.code(), code.getDefaultMessage()))
                .build();
        res.setStatus(code.getStatus().value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        om.writeValue(res.getOutputStream(), body);
    }
}
