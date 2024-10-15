package com.car.foryou.service.notification;

import com.car.foryou.dto.notification.NotificationTemplateDto;

public interface NotificationService {
    String sendNotification(String channel, String title, NotificationTemplateDto message, String to);
}
