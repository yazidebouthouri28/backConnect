package com.camping.projet.service;

public interface IEmailService {
    void sendVerificationEmail(String to, String token);

    void sendPasswordResetEmail(String to, String token);
}
