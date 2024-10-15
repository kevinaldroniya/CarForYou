package com.car.foryou.config;

import com.car.foryou.utils.WhatsappTwilioProperties;
import com.twilio.Twilio;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfiguration {
   private final WhatsappTwilioProperties whatsappTwilioProperties;
   private boolean isTwilioInitialized = false;

    public TwilioConfiguration(WhatsappTwilioProperties whatsappTwilioProperties) {
        this.whatsappTwilioProperties = whatsappTwilioProperties;
    }

    public void initTwilio() {
        if (!isTwilioInitialized) {
            Twilio.init(whatsappTwilioProperties.getAccountSid(), whatsappTwilioProperties.getAuthToken());
            isTwilioInitialized = true;
        }
    }
}
