package com.car.foryou.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "encryption")
@Getter
@Setter
public class EncryptionProperties {
    private String secretKey;
    private String algorithm;
}
