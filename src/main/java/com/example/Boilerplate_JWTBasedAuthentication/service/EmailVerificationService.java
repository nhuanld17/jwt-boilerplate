package com.example.Boilerplate_JWTBasedAuthentication.service;

import com.example.Boilerplate_JWTBasedAuthentication.entity.User;
import com.example.Boilerplate_JWTBasedAuthentication.entity.VeryficationToken;
import com.example.Boilerplate_JWTBasedAuthentication.exception.custome.ExpiredVeryficationToken;
import com.example.Boilerplate_JWTBasedAuthentication.exception.custome.VeryficationTokenNotFoundException;
import com.example.Boilerplate_JWTBasedAuthentication.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final JavaMailSenderImpl mailSender;

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();

        VeryficationToken verificationToken = VeryficationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        verificationTokenRepository.deleteByUser(user);
        verificationTokenRepository.save(verificationToken);

        String confirmUrl = "http://localhost:3000/api/auth/confirm?token=" + token;
        sendVerificationLink(confirmUrl, user.getUsername());
    }

    @Async
    public void sendVerificationLink(String confirmUrl, String username) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(username);
            mail.setSubject("Confirm your registration");
            mail.setText("Please click the link to confirm your account: " + confirmUrl);

            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Exception occurred while sending email", e);
            throw e;
        }
    }

    public void confirmToken(String token) throws VeryficationTokenNotFoundException, ExpiredVeryficationToken {
        VeryficationToken veryficationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VeryficationTokenNotFoundException("VeryficationToken Not Found"));

        if (veryficationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ExpiredVeryficationToken("Expired Veryfication Token");
        }

        User user = veryficationToken.getUser();
        user.setEnabled(true); // kích hoạt tài khoản
        verificationTokenRepository.delete(veryficationToken);
    }
}
