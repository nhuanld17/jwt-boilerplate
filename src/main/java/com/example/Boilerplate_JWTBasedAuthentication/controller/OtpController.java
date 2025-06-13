package com.example.Boilerplate_JWTBasedAuthentication.controller;

import com.example.Boilerplate_JWTBasedAuthentication.service.OtpService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/otp")
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/generate")
    public String generate(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        return "OTP sent: " + otp;
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String email, @RequestParam String otp) {
        boolean result = otpService.verifyOtp(email, otp);
        return result ? "OTP verified" : "OTP invalid or expired";
    }
}
