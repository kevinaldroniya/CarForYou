package com.car.foryou.service.email;

import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.service.notification.TemplateLoader;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.user.UserService;
import com.car.foryou.utils.EmailSendGridProperties;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
public class EmailSendGridService {

    private final SendGrid sendGrid;
    private final EmailSendGridProperties sendGridProperties;
    private final TemplateLoader templateLoader;
    private final UserService userService;

    public EmailSendGridService(SendGrid sendGrid, EmailSendGridProperties sendGridProperties, TemplateLoader templateLoader, UserService userService) {
        this.sendGrid = sendGrid;
        this.sendGridProperties = sendGridProperties;
        this.templateLoader = templateLoader;
        this.userService = userService;
    }

    public void sendSingleEmail(String subject, MessageTemplate message, String recipient) {
        UserResponse user = userService.getUserByEmailOrUsernameOrPhoneNumber(recipient);
        String toUser = recipient;
        if(user.getUsername()!=null){
            toUser = user.getFirstName();
        }
        String htmlContent = templateLoader.loadTemplate(message.getName()+".html");
        Set<String> keySet = message.getData().keySet();
        for (String key : keySet){
            htmlContent = htmlContent.replace("${"+ key +"}", String.valueOf(message.getData().get(key)));
        }
        if (htmlContent.contains("${sender_name}")){
            htmlContent = htmlContent.replace("${sender_name}", String.format("%s %s", CustomUserDetailService.getLoggedInUserDetails().getFirstName(), CustomUserDetailService.getLoggedInUserDetails().getLastName()));
        }
        if (htmlContent.contains("${company_name}")){
            htmlContent = htmlContent.replace("${company_name}", "Adventure Guild");
        }
        htmlContent = htmlContent.replace("${recipient}", toUser);
        Email from = new Email(sendGridProperties.getFromEmail());
        Email to = new Email(recipient);
        Content content = new Content("text/html", htmlContent);

        Mail mail = new Mail(from, subject, to, content);
        sendEmail(mail);
    }

    public void sendBatchEmail(String subject, MessageTemplate message, Set<String> recipients) {
        for (String recipient : recipients) {
            sendSingleEmail(subject, message, recipient);
        }
    }

    private void sendEmail(Mail mail) {
     try {
         Request request = new Request();
         request.setMethod(Method.POST);
         request.setEndpoint("mail/send");
         request.setBody(mail.build());
         request.addHeader("Content-Type", "application/json"); // Add this line
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
