package com.car.foryou.service;

public interface NotificationService {
    String sendNotification(String channel, String title, String message, String to);
}
