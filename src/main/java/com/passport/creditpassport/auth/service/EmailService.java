package com.passport.creditpassport.auth.service;

import com.passport.creditpassport.exception.OtpDeliveryException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String FROM = "ianmwirigi638@gmail.com";

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String subject, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(otpCode);

        try {
            mailSender.send(message);
            log.info("OTP email sent to {}", to);
        } catch (MailSendException ex) {
            // e.g. SMTP connection refused, timeout — client can retry later
            throw OtpDeliveryException.retryable("Failed to deliver OTP to recipient", ex);
        } catch (MailException ex) {
            // e.g. invalid address, auth failure — retrying won't help
            throw OtpDeliveryException.nonRetryable("Failed to deliver OTP to recipient", ex);
        }
    }
}