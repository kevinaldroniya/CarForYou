package com.car.foryou.controller;

import com.car.foryou.dto.notification.NotificationRequest;
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

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        String response = notificationService.sendNotification(request.getChannel(), request.getTitle(), request.getMessage(), request.getRecipient());
        return ResponseEntity.ok(response);
    }
}
