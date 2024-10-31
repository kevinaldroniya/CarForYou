package com.car.foryou.helper;

import com.car.foryou.utils.EmailTemplateLoader;
import com.car.foryou.utils.MailgunProperties;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailMailgunHelper {

    private final MailgunMessagesApi mailgunMessagesApi;
    private final EmailTemplateLoader emailTemplateLoader;
    private final MailgunProperties mailgunProperties;

    public EmailMailgunHelper(MailgunMessagesApi mailgunMessagesApi, EmailTemplateLoader emailTemplateLoader, MailgunProperties mailgunProperties) {
        this.mailgunMessagesApi = mailgunMessagesApi;
        this.emailTemplateLoader = emailTemplateLoader;
        this.mailgunProperties = mailgunProperties;
    }

    public void sendSingleEmail(String title, String message, String recipient){
        String htmlContent = emailTemplateLoader.loadTemplate("emailVerification.html");
        Message sendMessage = Message.builder()
                .from(mailgunProperties.getFromEmail())
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
        MessageResponse messageResponse = mailgunMessagesApi.sendMessage(mailgunProperties.getDomain(), message);
        log.info("Email sent with response: {}", messageResponse);
    }
}
