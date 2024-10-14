package com.car.foryou.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notifications.whatsapp-api.twilio")
@Getter
@Setter
public class WhatsappTwilioProperties {
    private String accountSid;
    private String authToken;
    private String fromNumber;
}
