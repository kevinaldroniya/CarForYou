package com.car.foryou.service.notification;

import com.car.foryou.dto.notification.NotificationTemplateDto;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.model.NotificationTemplate;
import com.car.foryou.service.email.EmailMailgunService;
import com.car.foryou.service.email.EmailSendGridService;
import com.car.foryou.service.whatsapp.WhatsappTwilioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final WhatsappTwilioService whatsappTwilioService;
    private final EmailMailgunService emailMailgunService;
    private final EmailSendGridService emailSendGridService;

    public NotificationServiceImpl(WhatsappTwilioService whatsappTwilioService, EmailMailgunService emailMailgunService, EmailSendGridService emailSendGridService) {
        this.whatsappTwilioService = whatsappTwilioService;
        this.emailMailgunService = emailMailgunService;
        this.emailSendGridService = emailSendGridService;
    }

    @Override
    public String sendNotification(String channel, String title, NotificationTemplateDto message, String to) {
        switch (channel){
            case "email":
//                emailMailgunService.sendSingleEmail(title, message, to);
                emailSendGridService.sendSingleEmail(title, message, to);
                break;
            case "whatsapp":
                whatsappTwilioService.sendSingleWhatsapp(title, message, to);
                break;
            default:
                throw new InvalidRequestException("Invalid channel: " + channel, HttpStatus.BAD_REQUEST);
        }
        return "";
    }
}
