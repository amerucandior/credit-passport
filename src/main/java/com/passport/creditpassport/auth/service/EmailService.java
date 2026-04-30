package com.passport.creditpassport.auth.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private  final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String subject, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ianmwirigi638@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(otpCode);

        mailSender.send(message);
        log.info("otp email sent to {}", to);
    }
}