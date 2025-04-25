package com.example.Boilerplate_JWTBasedAuthentication.config;

import com.example.Boilerplate_JWTBasedAuthentication.dto.common.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        RestResponse<Void> restResponse = RestResponse.error(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden ...",
                accessDeniedException.getMessage()
        );

        objectMapper.writeValue(response.getWriter(), restResponse);
    }
}
