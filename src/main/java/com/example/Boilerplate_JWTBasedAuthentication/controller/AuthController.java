package com.example.Boilerplate_JWTBasedAuthentication.controller;

import com.example.Boilerplate_JWTBasedAuthentication.dto.common.RestResponse;
import com.example.Boilerplate_JWTBasedAuthentication.dto.request.LoginRequest;
import com.example.Boilerplate_JWTBasedAuthentication.dto.request.RegisterRequest;
import com.example.Boilerplate_JWTBasedAuthentication.dto.respone.AuthResponse;
import com.example.Boilerplate_JWTBasedAuthentication.exception.custome.*;
import com.example.Boilerplate_JWTBasedAuthentication.service.AuthService;
import com.example.Boilerplate_JWTBasedAuthentication.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "API for login and register")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    @Operation(summary = "Register by email & password", description = "Create a user base on email & password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully create a user"),
            @ApiResponse(responseCode = "409", description = "This email is used for another account")
    })
    public ResponseEntity<RestResponse<Void>> register(
            @Parameter(description = "Register info - email & password", required = true)
            @RequestBody RegisterRequest request
    ) throws UsernameExistedException, RoleNotFoundException {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.success("User registered successfully")
        );
    }


    @PostMapping("/login")
    @Operation(
            summary = "Login by email and password",
            description = "Authenticate a user using email and password. Returns access and refresh tokens if successful."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "Email not found")
    })
    public ResponseEntity<RestResponse<AuthResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.login(request);

        /**
         * - Tạo cookie với tên là refresh_token với giá trị là chuỗi refreshtoken từ đối tượng
         * AuthResponse sau khi xác nhận email và password.
         * - httpOnly(true): Cookie chỉ được gửi qua giao thức HTTP/HTTPS, ngăn chặn truy cập từ javascript
         * (phòng chống tấn công XSS).
         * - secure(false): Cookie được gửi qua HTTP thường (không yêu cầu HTTPS). Nếu secure(true), cookie
         * chỉ gửi qua HTTPS (nên dùng trong môi trường production).
         * - path("/"): Cookie có hiệu lực cho tất cả các path trên domain hiện tại.
         * - maxAge(Duration.ofDays(7)): Thời gian tồn tại của cookie là 7 ngày.
         * - sameSite("Strict"): Cookie chỉ được gửi khi request xuất phát từ cùng origin. Chống tấn công CSRF:
         * Ngăn các site khác lợi dụng cookie này.
         */
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        // Thêm cookie vào header của phản hồi
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.status(HttpStatus.OK).body(
                RestResponse.success(authResponse, "User logged in successfully")
        );
    }

    @Operation(
            summary = "Logout user",
            description = "Logout by deleting refresh token both in DB and browser cookie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "404", description = "Refresh token not found in the database")
    })
    @PostMapping("/logout")
    public ResponseEntity<RestResponse<Void>> logout(
            @Parameter(
                    description = "Refresh token stored in the cookie",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) throws RefreshTokenNotFoundException {
        // Xóa refresh_token trong db
        authService.logout(refreshToken);

        // Xóa cookie ở phía client
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(
                RestResponse.success("Logged out successfully")
        );
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Re-create access token",
            description = "Generate a new access token using the refresh token from the request cookie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "404", description = "Refresh token not found"),
            @ApiResponse(responseCode = "401", description = "Refresh token has expired")
    })
    public ResponseEntity<RestResponse<AuthResponse>> refreshToken(
            @Parameter(
                    description = "Refresh token stored in cookie",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @CookieValue("refresh_token") String refreshToken
    ) throws RefreshTokenNotFoundException, ExpiredRefreshTokenException {
        AuthResponse authResponse = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(
                RestResponse.success(authResponse, "Token refreshed successfully")
        );
    }

    @GetMapping("/confirm")
    @Operation(
            summary = "Confirm account email",
            description = "Activate a user account using a verification token sent via email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account confirmed successfully"),
            @ApiResponse(responseCode = "404", description = "Verification token not found"),
            @ApiResponse(responseCode = "400", description = "Verification token has expired")
    })
    public ResponseEntity<RestResponse<Void>> confirmEmail(
            @Parameter(
                    description = "Token from email verification link",
                    required = true,
                    example = "a1b2c3d4e5f6g7h8i9"
            )
            @RequestParam String token
    ) throws VeryficationTokenNotFoundException, ExpiredVeryficationToken {

        emailVerificationService.confirmToken(token);

        return ResponseEntity.ok(
                RestResponse.success("Account confirm successfully")
        );
    }
}
