package com.example.Boilerplate_JWTBasedAuthentication.exception.global;

import com.example.Boilerplate_JWTBasedAuthentication.dto.common.RestResponse;
import com.example.Boilerplate_JWTBasedAuthentication.exception.custome.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Khi không tìm thấy user trong DB (ví dụ: login với username không đúng).
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RestResponse<Void>> handleUsernameNotFound(UsernameNotFoundException exception) {
        log.warn("User not found: {}", exception.getMessage());

        RestResponse<Void> response = RestResponse.error(
                HttpStatus.NOT_FOUND.value(),
                "USER NOT FOUND",
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Xử lý trường hợp sai username hoặc password khi login.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        log.warn("Bad credentials: {}", exception.getMessage());

        RestResponse<Void> response = RestResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID USERNAME OR PASSWORD",
                exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Xử lý khi token đã hết hạn (expired token).
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<RestResponse<Void>> handleExpiredToken(ExpiredJwtException ex) {
        log.warn("Expired JWT: {}", ex.getMessage());
        RestResponse<Void> response = RestResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "TOKEN HAS EXPIRED",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Xử lý khi token không hợp lệ (ví dụ: không phải là JWT).
    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<RestResponse<Void>> handleUnsupportedToken(UnsupportedJwtException ex) {
        log.warn("Unsupported JWT: {}", ex.getMessage());

        RestResponse<Void> response = RestResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "UNSUPPORTED TOKEN",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Xử lý khi token bị hỏng (malformed token).
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<RestResponse<Void>> handleMalformedToken(MalformedJwtException ex) {
        log.warn("Malformed JWT: {}", ex.getMessage());

        RestResponse<Void> response = RestResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "MALFORMED TOKEN",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Xử lý khi user không có quyền truy cập vào tài nguyên (ví dụ: không có role phù hợp).
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        RestResponse<Void> response = RestResponse.error(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS DENIED",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // trường hợp role chưa được tạo, cái này thực tế ko dùng
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<RestResponse<Void>> handleRoleNotExistException(RoleNotFoundException e) {
        log.error("Role not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                RestResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        "ROLE NOT FOUND",
                        e.getMessage()
                )
        );
    }

    // Khi đăng kí với username đã được sử dụng
    @ExceptionHandler(UsernameExistedException.class)
    public ResponseEntity<RestResponse<Void>> handleUserAlreadyExistedException(UsernameExistedException e) {
        log.warn("Username already existed: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                RestResponse.error(
                        HttpStatus.CONFLICT.value(),
                        "USERNAME ALREADY EXISTED",
                        e.getMessage()
                )
        );
    }

    // Khi refreshtoken hết hạn
    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<RestResponse<Void>> handleExpiredRefreshToken(ExpiredRefreshTokenException e) {
        log.warn("Expired refreshtoken: {}", e.getMessage());

        RestResponse<Void> restResponse = RestResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "EXPIRED REFRESHTOKEN",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restResponse);
    }

    // Khi refreshtoken không hợp lệ
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<RestResponse<Void>> handleInvalidRefreshToken(InvalidRefreshTokenException e) {
        log.warn("Invalid refreshtoken: {}", e);

        RestResponse<Void> restResponse = RestResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID REFRESHTOKEN: {}",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restResponse);
    }

    // Khi token resetpassword không hợp lệ
    @ExceptionHandler(InvalidResetPasswordTokenException.class)
    public ResponseEntity<RestResponse<Void>> handleInvalidResetPasswordToken(InvalidResetPasswordTokenException e) {
        log.warn("Invalid resetpassword token: {}", e.getMessage());

        RestResponse<Void> restResponse = RestResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID RESETPASSWORD TOKEN",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<RestResponse<Void>> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        log.warn("Refresh Token Not Found: {}", e.getMessage());

        RestResponse<Void> restResponse = RestResponse.error(
                HttpStatus.NOT_FOUND.value(),
                "The provided refresh token does not exist",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(VeryficationTokenNotFoundException.class)
    public ResponseEntity<RestResponse<Void>> handleVeryficationTokenNotFound(VeryficationTokenNotFoundException e) {
        log.warn("Veryficationtoken not found: {}", e.getMessage());

        RestResponse<Void> restResponse = RestResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Veryfication not found",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                RestResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        "Veryfication not found",
                        e.getMessage()
                )
        );
    }

    @ExceptionHandler(ExpiredVeryficationToken.class)
    public ResponseEntity<RestResponse<Void>> handleExpiredVeryficationToken(ExpiredRefreshTokenException e) {
        log.warn("Expired veryfication token: {}", e.getMessage());

        RestResponse<Void> restResponse = RestResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Veryfication not found",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    // Bắt tất cả các exception còn lại để tránh application bị crash.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Internal Error: {}", ex.getMessage(), ex);

        RestResponse<Void> response = RestResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "AN ERROR OCCURRED",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
