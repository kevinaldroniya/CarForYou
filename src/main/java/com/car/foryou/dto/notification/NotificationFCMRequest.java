package com.car.foryou.dto.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationFCMRequest {
    private String token;
    private String title;
    private String message;
}
