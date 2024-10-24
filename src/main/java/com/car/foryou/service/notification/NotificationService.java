package com.car.foryou.service.notification;

import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;

import java.util.Set;

public interface NotificationService {
    String sendNotification(NotificationChannel channel, String title, MessageTemplate message, String to);
    String sendBatchNotification(NotificationChannel channel, String title, MessageTemplate message, Set<String> to);
}
