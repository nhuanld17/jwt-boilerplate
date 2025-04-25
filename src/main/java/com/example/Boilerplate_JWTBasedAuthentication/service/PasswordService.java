package com.example.Boilerplate_JWTBasedAuthentication.service;

import com.example.Boilerplate_JWTBasedAuthentication.entity.PasswordResetToken;
import com.example.Boilerplate_JWTBasedAuthentication.entity.User;
import com.example.Boilerplate_JWTBasedAuthentication.exception.custome.InvalidResetPasswordTokenException;
import com.example.Boilerplate_JWTBasedAuthentication.repository.PasswordResetTokenRepository;
import com.example.Boilerplate_JWTBasedAuthentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSenderImpl mailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Tạo token
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(15, ChronoUnit.MINUTES);

        // Nếu token đã tồn tại, cập nhật
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user)
                .map(existingToken -> {
                    existingToken.setToken(token);
                    existingToken.setExpiryDate(expiry);
                    return existingToken;
                })
                .orElse(
                        PasswordResetToken.builder()
                                .user(user)
                                .token(token)
                                .expiryDate(expiry)
                                .build()
                );

        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:3000/api/password/reset?token=" + token;
        sendLinkResetPassword(resetLink, user.getUsername());
    }

    @Async
    public void sendLinkResetPassword(String resetLink, String username) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(username);
        mail.setSubject("Password Reset Request");
        mail.setText("Click the link to reset your password: " + resetLink);

        mailSender.send(mail);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) throws InvalidResetPasswordTokenException {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidResetPasswordTokenException("Invalid reset password token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidResetPasswordTokenException("Invalid reset password token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
