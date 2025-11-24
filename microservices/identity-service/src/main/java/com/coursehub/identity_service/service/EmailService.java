package com.coursehub.identity_service.service;

public interface EmailService {
    void sendVerifyingEmail(String email, String activationCode);

    void sendEmailToResetPassword(String email, String tempResetCode);
}
