package com.car.foryou.config;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailgunConfig {
    @Value("${notification.email-api.mailgun.api-key}")
    private final String apiKey;

    public MailgunConfig(String apiKey) {
        this.apiKey = apiKey;
    }

    @Bean
    public MailgunMessagesApi mailgunMessagesApi(){
        return MailgunClient.config(apiKey).createApi(MailgunMessagesApi.class);
    }
}
