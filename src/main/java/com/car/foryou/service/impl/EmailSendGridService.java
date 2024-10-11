package com.car.foryou.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailSendGridService {

    private final SendGrid sendGrid;
    @Value("${twilio.sendgrid.from-email}")
    private final String fromEmail;

    public EmailSendGridService(SendGrid sendGrid, String fromEmail) {
        this.sendGrid = sendGrid;
        this.fromEmail = fromEmail;
    }

    public void sendSingleEmail(String subject, String message, String recipient) {
        Email from = new Email(this.fromEmail);
        Email to = new Email(recipient);
        Content content = new Content("text/html", message);

        Mail mail = new Mail(from, subject, to, content);
        sendEmail(mail);
    }

    private void sendEmail(Mail mail) {
     try {
         Request request = new Request();
         request.setMethod(Method.POST);
         request.setEndpoint("mail/send");
         request.setBody(mail.build());
         Response response = sendGrid.api(request);
         int statusCode = response.getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new RuntimeException("Failed to send email with status code: " + statusCode);
            }
     } catch (IOException e) {
         throw new RuntimeException(e);
     }
    }
}
