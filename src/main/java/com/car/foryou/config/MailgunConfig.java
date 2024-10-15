package com.car.foryou.config;

import com.car.foryou.utils.MailgunProperties;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailgunConfig {

    private final MailgunProperties mailgunProperties;

    public MailgunConfig(MailgunProperties mailgunProperties) {
        this.mailgunProperties = mailgunProperties;
    }

    @Bean
    public MailgunMessagesApi mailgunMessagesApi(){
        return MailgunClient.config(mailgunProperties.getApiKey()).createApi(MailgunMessagesApi.class);
    }
}
