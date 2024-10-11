package com.car.foryou.service.impl;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailMailgunService {

    private final MailgunMessagesApi mailgunMessagesApi;
    private final TemplateLoader templateLoader;
    @Value("${notification.email-api.mailgun.domain}")
    private final String domain;
    @Value("${notification.email-api.mailgun.from-email}")
    private final String fromEmail;

    public EmailMailgunService(MailgunMessagesApi mailgunMessagesApi, TemplateLoader templateLoader, String domain, String fromEmail) {
        this.mailgunMessagesApi = mailgunMessagesApi;
        this.templateLoader = templateLoader;
        this.domain = domain;
        this.fromEmail = fromEmail;
    }

    public void sendSingleEmail(String title, String message, String recipient){
        String htmlContent = templateLoader.loadTemplate("email-template.html");
        Message sendMessage = Message.builder()
                .from(fromEmail)
                .to(recipient)
                .subject(title)
                .html(htmlContent)
                .text(message)
                .build();
        sendEmail(sendMessage);
    }

    public void sendBulkEmail(String title, String message, String[] recipients){
        for (String recipient : recipients) {
            sendSingleEmail(title, message, recipient);
        }
        log.info("Bulk email sent to {} recipients", recipients.length);
    }

    private void sendEmail(Message message){
        MessageResponse messageResponse = mailgunMessagesApi.sendMessage(domain, message);
        log.info("Email sent with response: {}", messageResponse);
    }
}