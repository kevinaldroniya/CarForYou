package com.car.foryou.service.impl;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsappTwilioService {

    @Value("${notification.whatsapp-api.twilio.from-whatsapp}")
    private final String fromNumber;

    public WhatsappTwilioService(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public void sendSingleWhatsapp(String title, String message, String recipient) {
        String finalMessage = title + "\n" + message;
        Message singleMessage = Message.creator(
               new PhoneNumber("whatsapp:" + recipient),
                new PhoneNumber("whatsapp:" + fromNumber),
                finalMessage)
               .create();
        log.info("Whatsapp message sent with SID: {}", singleMessage.getSid());
    }

    public void sendBulkWhatsapp(String title, String message, String[] recipients) {
        for (String recipient : recipients) {
            sendSingleWhatsapp(title, message, recipient);
        }
        log.info("Bulk whatsapp message sent to {} recipients", recipients.length);
    }
}
