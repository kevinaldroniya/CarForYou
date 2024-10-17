package com.car.foryou.dto.notification;

import com.car.foryou.exception.InvalidRequestException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationChannel {
    EMAIL("email"),
    WHATSAPP("whatsapp");

    private final String value;

    NotificationChannel(String value) {
        this.value = value;
    }

    public static NotificationChannel fromValue(String value) {
        for (NotificationChannel channel : NotificationChannel.values()) {
            if (channel.value.equalsIgnoreCase(value)) {
                return channel;
            }
        }
        throw new InvalidRequestException("Invalid notification channel: " + value, HttpStatus.BAD_REQUEST);
    }
}
