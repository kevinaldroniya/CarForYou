package com.car.foryou.service.email;

import com.car.foryou.dto.email.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender javaMailSender;
    private final ResourceLoader resourceLoader;

    public EmailServiceImpl(JavaMailSender javaMailSender, ResourceLoader resourceLoader) {
        this.javaMailSender = javaMailSender;
        this.resourceLoader = resourceLoader;
    }
//    @Override
//    public void sendSimpleMessage(MailBody mailBody) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(mailBody.to());
//        message.setFrom(from);
//        message.setSubject(mailBody.subject());
//        message.setText(mailBody.text());
//
//        javaMailSender.send(message);
//    }

    @Override
    public void sendSimpleMessage(MailBody mailBody) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(mailBody.to());
            helper.setFrom(from);
            helper.setSubject(mailBody.subject());

            // Load HTML content from resources
            Resource resource = resourceLoader.getResource("classpath:email-template.html");
            String htmlContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            // Replace the placeholder with the actual verification link
            String verificationLink = "http://localhost:8080/verify";
            htmlContent = htmlContent.replace("{{verification_link}}", verificationLink);

            helper.setText(htmlContent, true); // true indicates HTML content

            javaMailSender.send(message);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
