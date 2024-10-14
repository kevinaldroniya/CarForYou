package com.car.foryou.dto.notification;

import lombok.Getter;

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
            if (channel.value.equals(value)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("Invalid notification channel: " + value);
    }
}
