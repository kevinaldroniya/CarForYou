package com.car.foryou.dto.notification;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class NotificationTemplateDto {
    private Byte id;
    private String name;
    private NotificationChannel channel;
    private Set<String> data;
    private String bodyMessage;
}
