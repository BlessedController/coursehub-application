package com.coursehub.identity_service.service.concretes;


import com.coursehub.commons.exceptions.ActivationMailCouldNotSendException;
import com.coursehub.identity_service.config.MailPropertiesConfig;
import com.coursehub.identity_service.service.abstracts.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MailPropertiesConfig mailProperties;
    private final JavaMailSender mailSender;

    @Value("${identity-service.host}")
    private String host;

    @Value("${identity-service.auth-base-url}")
    private String identityBaseUrl;


    @Override
    public void sendVerifyingEmail(String email, String activationCode) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailProperties.getFrom());
            helper.setTo(email);
            helper.setSubject("Activation Email");

            String activationUrl = buildUrl() + "/verify/" + activationCode;


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

    private String buildUrl() {
        return host + (identityBaseUrl.startsWith("/") ? identityBaseUrl : "/" + identityBaseUrl);
    }

}