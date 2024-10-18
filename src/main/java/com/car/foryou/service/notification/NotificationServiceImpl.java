package com.car.foryou.service.notification;

import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.notification.NotificationTemplateDto;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.service.email.EmailMailgunService;
import com.car.foryou.service.email.EmailSendGridService;
import com.car.foryou.service.notificationtemplate.NotificationTemplateService;
import com.car.foryou.service.whatsapp.WhatsappTwilioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final WhatsappTwilioService whatsappTwilioService;
    private final EmailMailgunService emailMailgunService;
    private final EmailSendGridService emailSendGridService;
    private final NotificationTemplateService notificationTemplateService;

    public NotificationServiceImpl(WhatsappTwilioService whatsappTwilioService, EmailMailgunService emailMailgunService, EmailSendGridService emailSendGridService, NotificationTemplateService notificationTemplateService) {
        this.whatsappTwilioService = whatsappTwilioService;
        this.emailMailgunService = emailMailgunService;
        this.emailSendGridService = emailSendGridService;
        this.notificationTemplateService = notificationTemplateService;
    }

    @Override
    public String sendNotification(NotificationChannel channel, String title, MessageTemplate message, String to) {
        validateMessageTemplate(message);
        String channelValue = channel.getValue();
        String response = "";
        String startRecipient = to.replace(to.substring(3, to.length()-3), "*****");
        switch (channelValue){
            case "email":
//                emailMailgunService.sendSingleEmail(title, message, to);
                emailSendGridService.sendSingleEmail(title, message, to);
                response = "Email sent successfully, sent to: '" + startRecipient + "', please check your email";
                break;
            case "whatsapp":
                whatsappTwilioService.sendSingleWhatsapp(title, message, to);
                response = "Whatsapp message sent successfully, sent to: '" + startRecipient + "', please check your whatsapp";
                break;
            default:
                throw new InvalidRequestException("Invalid channel: " + channel, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    private void validateMessageTemplate(MessageTemplate message){
        NotificationTemplateDto notificationTemplate = notificationTemplateService.getNotificationTemplate(message.getName());
        Map<String, Object> messageData = message.getData();
        String body =  notificationTemplate.getBodyMessage() == null ? "" : notificationTemplate.getBodyMessage();
        message.setBodyMessage(body);
        Set<String> data = notificationTemplate.getData();
        for (String dataKey : data){
            if (messageData.get(dataKey) == null){
                throw new InvalidRequestException(String.format("Invalid value for '%s'",dataKey), HttpStatus.BAD_REQUEST);
            }
        }
    }
}
