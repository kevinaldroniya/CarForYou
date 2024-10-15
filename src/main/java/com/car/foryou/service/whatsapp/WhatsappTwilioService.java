package com.car.foryou.service.whatsapp;

import com.car.foryou.config.TwilioConfiguration;
import com.car.foryou.utils.WhatsappTwilioProperties;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.service.user.UserService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsappTwilioService {

    private final UserService userService;
    private final WhatsappTwilioProperties whatsappTwilioProperties;
    private final TwilioConfiguration twilioConfiguration;

    public WhatsappTwilioService(UserService userService, WhatsappTwilioProperties whatsappTwilioProperties, TwilioConfiguration twilioConfiguration) {
        this.userService = userService;
        this.whatsappTwilioProperties = whatsappTwilioProperties;
        this.twilioConfiguration = twilioConfiguration;
    }

    public void sendSingleWhatsapp(String title, String message, String recipient) {
        twilioConfiguration.initTwilio();
        UserResponse user = userService.getUserByEmailOrUsernameOrPhoneNumber(recipient);
        String toUser = recipient;
        if (user.getFirstName() != null){
            toUser = user.getFirstName();
        }
        String finalMessage = "[" + title + "] \n" +
                "Hello, " + toUser + "! \n" +
                message + "\n" +
                "Please do not share this message with anyone. \n";
        Message singleMessage = Message.creator(
               new PhoneNumber("whatsapp:" + recipient),
                new PhoneNumber("whatsapp:" + whatsappTwilioProperties.getFromNumber()),
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
