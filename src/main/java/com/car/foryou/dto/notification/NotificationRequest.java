package com.car.foryou.dto.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private NotificationChannel channel;
    private String title;
    private MessageTemplate message;
    private String recipient;
}
