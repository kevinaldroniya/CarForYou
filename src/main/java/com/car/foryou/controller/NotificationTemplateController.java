package com.car.foryou.controller;

import com.car.foryou.dto.notification.NotificationTemplateDto;
import com.car.foryou.service.notificationtemplate.NotificationTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificationTemplate")
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    public NotificationTemplateController(NotificationTemplateService notificationTemplateService) {
        this.notificationTemplateService = notificationTemplateService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationTemplateDto>> getAllNotificationTemplates(){
        List<NotificationTemplateDto> notificationTemplates = notificationTemplateService.getAllNotificationTemplates();
        return ResponseEntity.ok(notificationTemplates);
    }

    @GetMapping("/{templateName}")
    public ResponseEntity<NotificationTemplateDto> getNotificationTemplateByName(@PathVariable("templateName") String templateName){
        NotificationTemplateDto notificationTemplate = notificationTemplateService.getNotificationTemplate(templateName);
        return ResponseEntity.ok(notificationTemplate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationTemplateDto> getNotificationTemplateById(@PathVariable("id") Byte id){
        NotificationTemplateDto notificationTemplateDto = notificationTemplateService.deleteNotificationTemplate(id);
        return ResponseEntity.ok(notificationTemplateDto);
    }

    @PostMapping
    public ResponseEntity<NotificationTemplateDto> createNotificationTemplate(@RequestBody NotificationTemplateDto notificationTemplateDto){
        NotificationTemplateDto notificationTemplate = notificationTemplateService.createNotificationTemplate(notificationTemplateDto);
        return new ResponseEntity<>(notificationTemplate, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationTemplateDto> updateNotificationTemplate(@PathVariable("id") Byte id, @RequestBody NotificationTemplateDto notificationTemplateDto){
        NotificationTemplateDto templateDto = notificationTemplateService.updateNotificationTemplate(id, notificationTemplateDto);
        return ResponseEntity.ok(templateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NotificationTemplateDto> deleteNotificationTemplate(@PathVariable("id") Byte id){
        NotificationTemplateDto notificationTemplateDto = notificationTemplateService.deleteNotificationTemplate(id);
        return ResponseEntity.ok(notificationTemplateDto);
    }
}
