package com.car.foryou.mapper;

import com.car.foryou.dto.notification.NotificationTemplateDto;
import com.car.foryou.model.NotificationTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplateMapper {
    private final ObjectMapper objectMapper;

    public NotificationTemplateMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NotificationTemplateDto toDto(NotificationTemplate notificationTemplate) {
        return objectMapper.convertValue(notificationTemplate, NotificationTemplateDto.class);
    }

    public NotificationTemplate toEntity(NotificationTemplateDto notificationTemplateDto) {
        return objectMapper.convertValue(notificationTemplateDto, NotificationTemplate.class);
    }
}
