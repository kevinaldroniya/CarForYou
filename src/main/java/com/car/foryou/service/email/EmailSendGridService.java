package com.car.foryou.service.email;

import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.service.impl.TemplateLoader;
import com.car.foryou.service.user.UserService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailSendGridService {

    private final SendGrid sendGrid;
    private final EmailSendGridProperties sendGridProperties;
    private final TemplateLoader templateLoader;
    private final UserService userService;

    public EmailSendGridService(SendGrid sendGrid, TemplateLoader templateLoader, UserService userService) {
        this.sendGrid = sendGrid;
        this.templateLoader = templateLoader;
        this.userService = userService;
    }

    public void sendSingleEmail(String subject, String message, String recipient) {
        UserResponse user = userService.getUserByEmailOrUsernameOrPhoneNumber(recipient);
        String toUser = recipient;
        if(user.getUsername()!=null){
            toUser = user.getFirstName();
        }
        String htmlContent = templateLoader.loadTemplate("emailVerification.html");
        htmlContent = htmlContent.replace("{{message}}", message);
        htmlContent = htmlContent.replace("{{recipient}}", toUser);
        Email from = new Email(this.fromEmail);
        Email to = new Email(recipient);
        Content content = new Content("text/html", htmlContent);

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
