package com.car.foryou.service.notificationtemplate;

import com.car.foryou.dto.notification.NotificationTemplateDto;

import java.util.List;

public interface NotificationTemplateService {
    List<NotificationTemplateDto> getAllNotificationTemplates();
    NotificationTemplateDto getNotificationTemplate(String templateName);
    NotificationTemplateDto createNotificationTemplate(NotificationTemplateDto notificationTemplateDto);
    NotificationTemplateDto updateNotificationTemplate(Byte id, NotificationTemplateDto notificationTemplateDto);
    NotificationTemplateDto deleteNotificationTemplate(Byte id);
    NotificationTemplateDto getNotificationTemplateDtoById(Byte id);
}
