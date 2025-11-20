package com.coursehub.identity_service.service.abstracts;

public interface EmailService {
    void sendVerifyingEmail(String email, String activationCode);
}
