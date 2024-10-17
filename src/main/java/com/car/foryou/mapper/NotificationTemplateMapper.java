package com.car.foryou.mapper;

import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.notification.NotificationTemplateDto;
import com.car.foryou.exception.ConversionException;
import com.car.foryou.model.NotificationTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotificationTemplateMapper {
    private final ObjectMapper objectMapper;

    public NotificationTemplateMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NotificationTemplateDto toDto(NotificationTemplate notificationTemplate) {
        try {
            return NotificationTemplateDto.builder()
                    .id(notificationTemplate.getId())
                    .name(notificationTemplate.getName())
                    .channel(NotificationChannel.fromValue(notificationTemplate.getChannel()))
                    .bodyMessage(notificationTemplate.getBodyMessage())
                    .data(objectMapper.readValue(notificationTemplate.getData(), new TypeReference<Set<String>>() {}))
                    .build();
        }catch (JsonProcessingException e){
            throw new ConversionException("NotificationTemplate","NotificationTemplateDto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public NotificationTemplate toEntity(NotificationTemplateDto notificationTemplateDto) {
        try {
            return NotificationTemplate.builder()
                    .name(notificationTemplateDto.getName())
                    .channel(notificationTemplateDto.getChannel().getValue())
                    .bodyMessage(notificationTemplateDto.getBodyMessage())
                    .data(objectMapper.writeValueAsString(notificationTemplateDto.getData()))
                    .build();
        }catch (JsonProcessingException e){
            throw new ConversionException("NotificationTemplateDto","NotificationTemplate", HttpStatus.BAD_REQUEST);
        }
    }
}
