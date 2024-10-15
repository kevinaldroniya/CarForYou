package com.car.foryou.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notifications.email-api.sendgrid")
@Getter
@Setter
public class EmailSendGridProperties {
    private String apiKey;
    private String fromEmail;
}
