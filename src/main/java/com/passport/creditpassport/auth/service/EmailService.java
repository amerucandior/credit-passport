package com.passport.creditpassport.auth.service;

import com.passport.creditpassport.auth.exception.OtpDeliveryException;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

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

//    private static final String TOKEN = "1cc3844a870aea2198aee584bf5cc720";

//    public void sendOtpEmail(String to, String subject, String otpCode) {
//        final MailtrapConfig config = new MailtrapConfig.Builder()
//                .token(TOKEN)
//                .build();
//
//        final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);
//
//        final MailtrapMail mail = MailtrapMail.builder()
//                .from(new Address("hello@demomailtrap.co"))
//                .to(List.of(new Address(to)))
//                .subject(subject)
//                .text("Your OTP code is: " + otpCode)
//                .category("OTP")
//                .build();
//
//        try {
//            client.send(mail);
//        } catch (Exception e) {
//            throw new OtpDeliveryException("We could not send the OTP email. Please try again shortly.", e);
//        }
//    }
}