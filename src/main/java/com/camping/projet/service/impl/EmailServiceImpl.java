package com.camping.projet.service.impl;

import com.camping.projet.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${camping.app.baseUrl}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = baseUrl + "/api/auth/verify?token=" + token;
        String message = "Welcome to CampConnect! Please verify your account by clicking the link below:\n"
                + verificationUrl;

        sendEmail(to, "Account Verification", message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = baseUrl + "/api/auth/reset-password?token=" + token;
        String message = "You requested a password reset. Please click the link below to set a new password:\n"
                + resetUrl;

        sendEmail(to, "Password Reset Request", message);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            // In a production app, you might want to retry or queue this
        }
    }
}
