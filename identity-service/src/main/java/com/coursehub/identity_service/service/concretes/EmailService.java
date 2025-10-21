package com.coursehub.identity_service.service.concretes;


import com.coursehub.identity_service.config.MailPropertiesConfig;
import com.coursehub.identity_service.exception.ActivationMailCouldNotSendException;
import com.coursehub.identity_service.service.absttracts.IEmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@FieldDefaults(level = PRIVATE)
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    final MailPropertiesConfig mailProperties;
    JavaMailSenderImpl mailSender;
    private static final String HOST_NAME = "http://localhost:8081/v1/user";

    @PostConstruct
    private void init() {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    @Override
    public void sendVerifyingEmail(String email, String activationCode) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailProperties.getFrom());
            helper.setTo(email);
            helper.setSubject("Activation Email");

            String activationUrl = String.format(
                    "%s/verify/%s",
                    HOST_NAME,
                    activationCode
            );

            String htmlContent = String.format(
                    "Please activate your account by clicking the link below:<br>" +
                            "<a href=\"%s\">Click Here</a>",
                    activationUrl
            );

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MailException | MessagingException e) {
            log.error("Failed to send mail to {}", email, e);
            throw new ActivationMailCouldNotSendException("Mail could not be sent");
        }
    }

}