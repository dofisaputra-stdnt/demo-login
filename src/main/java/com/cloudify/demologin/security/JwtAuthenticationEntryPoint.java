package com.cloudify.demologin.security;

import com.cloudify.demologin.dto.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public JwtAuthenticationEntryPoint() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.addHeader("Content-Type", "application/json");
        response.addHeader("Accept", "*/*");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(mapper.writeValueAsString(buildResponse()));
        response.flushBuffer();
    }

    private BaseResponse<?> buildResponse() {
        BaseResponse<?> response = new BaseResponse<>();
        response.setMessage("Unauthorized");
        return response;
    }
}
