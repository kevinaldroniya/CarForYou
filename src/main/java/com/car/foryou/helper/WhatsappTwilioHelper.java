package com.car.foryou.helper;

import com.car.foryou.config.TwilioConfiguration;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.utils.WhatsappTwilioProperties;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.service.user.UserService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsappTwilioHelper {

    private final UserService userService;
    private final WhatsappTwilioProperties whatsappTwilioProperties;
    private final TwilioConfiguration twilioConfiguration;

    public WhatsappTwilioHelper(UserService userService, WhatsappTwilioProperties whatsappTwilioProperties, TwilioConfiguration twilioConfiguration) {
        this.userService = userService;
        this.whatsappTwilioProperties = whatsappTwilioProperties;
        this.twilioConfiguration = twilioConfiguration;
    }

    public void sendSingleWhatsapp(String title, MessageTemplate message, String recipient) {
        twilioConfiguration.initTwilio();
        UserResponse user = userService.getUserResponseByEmailOrUsernameOrPhoneNumber(recipient);
        String toUser = recipient;
        if (user.getFirstName() != null){
            toUser = user.getFirstName();
        }
        String finalMessage = message.getBodyMessage();
        finalMessage = finalMessage.replace("${title}", title);
        finalMessage = finalMessage.replace("${recipient}", toUser);
        for (String key : message.getData().keySet()){
            finalMessage = finalMessage.replace("${"+ key +"}", String.valueOf(message.getData().get(key)));
        }
        if (finalMessage.contains("${company_name}")){
            finalMessage = finalMessage.replace("${company_name}", "Adventure Guild");
        }
        Message singleMessage = Message.creator(
               new PhoneNumber("whatsapp:" + recipient),
                new PhoneNumber("whatsapp:" + whatsappTwilioProperties.getFromNumber()),
                finalMessage)
               .create();
        log.info("Whatsapp message sent with SID: {}", singleMessage.getSid());
    }

    public void sendBulkWhatsapp(String title, MessageTemplate message, String[] recipients) {
        for (String recipient : recipients) {
            sendSingleWhatsapp(title, message, recipient);
        }
        log.info("Bulk whatsapp message sent to {} recipients", recipients.length);
    }
}
