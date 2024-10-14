package com.car.foryou.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notifications.email-api.mailgun")
@Getter
@Setter
public class MailgunProperties {
    private String apiKey;
    private String domain;
    private String fromEmail;
}
