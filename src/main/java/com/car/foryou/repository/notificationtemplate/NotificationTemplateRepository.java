package com.car.foryou.repository.notificationtemplate;

import com.car.foryou.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Byte> {
    Optional<NotificationTemplate> findByName(String templateName);
}
