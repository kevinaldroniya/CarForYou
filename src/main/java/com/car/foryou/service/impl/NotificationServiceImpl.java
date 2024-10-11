package com.car.foryou.service.impl;

import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final EmailSendGridService emailSendGridService;
    private final WhatsappTwilioService whatsappTwilioService;

    public NotificationServiceImpl(EmailSendGridService emailSendGridService, WhatsappTwilioService whatsappTwilioService) {
        this.emailSendGridService = emailSendGridService;
        this.whatsappTwilioService = whatsappTwilioService;
    }

    @Override
    public String sendNotification(String channel, String title, String message, String to) {
        switch (channel){
            case "email":
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
