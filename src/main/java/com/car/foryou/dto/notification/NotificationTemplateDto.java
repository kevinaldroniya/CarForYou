package com.car.foryou.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationTemplateDto {
    private Byte id;
    private String name;
    private Map<String, Object> data;
}
