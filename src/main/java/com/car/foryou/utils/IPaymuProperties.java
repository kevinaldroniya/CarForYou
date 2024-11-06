package com.car.foryou.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment.iPaymu")
@Getter
@Setter
public class IPaymuProperties {
    private String virtualAccount;
    private String apiKey;
    private String paymentUrl;
}
