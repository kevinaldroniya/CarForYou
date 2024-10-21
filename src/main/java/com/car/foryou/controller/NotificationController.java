package com.car.foryou.controller;

import com.car.foryou.dto.notification.NotificationFCMRequest;
import com.car.foryou.dto.notification.NotificationRequest;
import com.car.foryou.service.fcm.FCMService;
import com.car.foryou.service.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final FCMService fcmService;

    public NotificationController(NotificationService notificationService, FCMService fcmService) {
        this.notificationService = notificationService;
        this.fcmService = fcmService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        String response = notificationService.sendNotification(request.getChannel(), request.getTitle(), request.getMessage(), request.getRecipient());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-fcm")
    public ResponseEntity<String> sendFCMNotification(@RequestBody NotificationFCMRequest request) {
        fcmService.sendNotification(request.getToken(), request.getTitle(), request.getMessage());
        return ResponseEntity.ok("Notification sent successfully");
    }
}
