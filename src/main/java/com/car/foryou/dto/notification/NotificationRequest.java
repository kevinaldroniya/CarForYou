package com.car.foryou.dto.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private String channel;
    private String title;
    private String message;
    private String recipient;
}
