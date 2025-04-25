package com.example.Boilerplate_JWTBasedAuthentication.dto.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private int statusCode;
    private String message;
    private String error;
    private T data;
    private String path;
    private String timestamp;

    public static String getCurrentPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getRequestURI();
        }

        return "unknown";
    }

    // Helper static - success and data
    public static <T> RestResponse<T> success(T data, String message) {
        return RestResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .path(getCurrentPath())
                .timestamp(Instant.now().toString())
                .build();
    }

    // Helper static - success and no data
    public static <T> RestResponse<T> success(String message) {
        return success(null, message);
    }

    // Helper static - error
    public static <T> RestResponse<T> error(int status, String message, String error) {
        return RestResponse.<T>builder()
                .statusCode(status)
                .message(message)
                .error(error)
                .path(getCurrentPath())
                .timestamp(Instant.now().toString())
                .build();
    }
}
