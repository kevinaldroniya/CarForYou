package com.car.foryou.config;

import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfiguration {
    @Value("${notification.whatsapp-api.twilio.account-sid}")
    private final String accountSid;

    @Value("${notification.whatsapp-api.twilio.auth-token}")
    private final String authToken;

    public TwilioConfiguration(String accountSid, String authToken) {
        this.accountSid = accountSid;
        this.authToken = authToken;
    }

    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }
}
