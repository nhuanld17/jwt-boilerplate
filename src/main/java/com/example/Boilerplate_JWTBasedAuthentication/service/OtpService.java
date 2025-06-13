package com.example.Boilerplate_JWTBasedAuthentication.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class OtpService {
    private final StringRedisTemplate redisTemplate;

    public String generateOtp(String email) {
        StringBuilder stringBuilder = new StringBuilder(6);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(secureRandom.nextInt(10)); // chỉ 0-9
        }
        redisTemplate.opsForValue().set(getOtpKey(email), stringBuilder.toString(), 1, TimeUnit.MINUTES);
        return stringBuilder.toString();
    }

    public boolean verifyOtp(String email, String inputOtp) {
        String key = getOtpKey(email);
        String cachedOtp = redisTemplate.opsForValue().get(key);
        return inputOtp.equals(cachedOtp);
    }

    private String getOtpKey(String email) {
        return "OTP_" + email;
    }
}
