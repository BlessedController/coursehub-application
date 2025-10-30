package com.coursehub.identity_service.service.absttracts;

public interface IEmailService {
    void sendVerifyingEmail(String email, String activationCode);
}
