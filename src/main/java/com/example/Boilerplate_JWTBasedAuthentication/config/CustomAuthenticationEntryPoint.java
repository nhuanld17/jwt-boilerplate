package com.example.Boilerplate_JWTBasedAuthentication.config;

import com.example.Boilerplate_JWTBasedAuthentication.dto.common.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> restResponse = RestResponse.builder()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                .message("Token không hợp lệ")
                .error(Optional.ofNullable(authException.getCause())
                        .map(Throwable::getMessage)
                        .orElse(authException.getMessage()))
                .path(request.getRequestURI())
                .timestamp(Instant.now().toString())
                .build();

        objectMapper.writeValue(response.getWriter(), restResponse);
    }
}
