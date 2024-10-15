package com.car.foryou.config;

import com.car.foryou.utils.EmailSendGridProperties;
import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfiguration {
    private final EmailSendGridProperties sendGridProperties;

    public SendGridConfiguration(EmailSendGridProperties sendGridProperties) {
        this.sendGridProperties = sendGridProperties;
    }

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(sendGridProperties.getApiKey());
    }
}
