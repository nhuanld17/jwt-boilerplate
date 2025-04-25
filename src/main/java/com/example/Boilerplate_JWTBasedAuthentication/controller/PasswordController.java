package com.example.Boilerplate_JWTBasedAuthentication.controller;

import com.example.Boilerplate_JWTBasedAuthentication.dto.common.RestResponse;
import com.example.Boilerplate_JWTBasedAuthentication.dto.request.ResetPasswordRequest;
import com.example.Boilerplate_JWTBasedAuthentication.exception.custome.InvalidResetPasswordTokenException;
import com.example.Boilerplate_JWTBasedAuthentication.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/forgot")
    public ResponseEntity<RestResponse<Void>> forgotPassword(@RequestParam String email) {
        passwordService.requestPasswordReset(email);

        return ResponseEntity.ok(
                RestResponse.success("Reset link sent to email")
        );
    }

    @PostMapping("/reset")
    public ResponseEntity<RestResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) throws InvalidResetPasswordTokenException {
        passwordService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(
                RestResponse.success("Password reset successfully")
        );
    }
}
