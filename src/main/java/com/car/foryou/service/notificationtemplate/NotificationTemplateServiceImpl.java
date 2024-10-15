package com.car.foryou.service.notificationtemplate;

import com.car.foryou.dto.notification.NotificationTemplateDto;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.NotificationTemplateMapper;
import com.car.foryou.model.NotificationTemplate;
import com.car.foryou.repository.notificationtemplate.NotificationTemplateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationTemplateServiceImpl implements NotificationTemplateService{

    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationTemplateMapper notificationTemplateMapper;

    private static final String NOTIFICATION_TEMPLATE = "NOTIFICATION_TEMPLATE";

    public NotificationTemplateServiceImpl(NotificationTemplateRepository notificationTemplateRepository, NotificationTemplateMapper notificationTemplateMapper) {
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.notificationTemplateMapper = notificationTemplateMapper;
    }

    @Override
    public List<NotificationTemplateDto> getAllNotificationTemplates() {
        List<NotificationTemplate> all = notificationTemplateRepository.findAll();
        return all.stream().map(notificationTemplateMapper::toDto).toList();
    }

    @Override
    public NotificationTemplateDto getNotificationTemplate(String templateName) {
        NotificationTemplate notificationTemplate = notificationTemplateRepository.findByName(templateName).orElseThrow(
                () -> new ResourceNotFoundException(NOTIFICATION_TEMPLATE, "name", templateName)
        );
        return notificationTemplateMapper.toDto(notificationTemplate);
    }

    @Override
    public NotificationTemplateDto createNotificationTemplate(NotificationTemplateDto notificationTemplateDto) {
        notificationTemplateRepository.findByName(notificationTemplateDto.getName()).ifPresent(
                notificationTemplate -> {
                    throw new ResourceAlreadyExistsException(NOTIFICATION_TEMPLATE, HttpStatus.CONFLICT);
                }
        );
        NotificationTemplate notificationTemplate = notificationTemplateMapper.toEntity(notificationTemplateDto);
        NotificationTemplate savedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        return notificationTemplateMapper.toDto(savedNotificationTemplate);
    }

    @Override
    public NotificationTemplateDto updateNotificationTemplate(Byte id, NotificationTemplateDto notificationTemplateDto) {
        NotificationTemplate notificationTemplate = notificationTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(NOTIFICATION_TEMPLATE, "id", id)
        );
        notificationTemplateRepository.findByName(notificationTemplateDto.getName()).ifPresent(
                existingNotificationTemplate -> {
                    if (!existingNotificationTemplate.getId().equals(id)){
                        throw new ResourceAlreadyExistsException(NOTIFICATION_TEMPLATE, HttpStatus.CONFLICT);
                    }
                }
        );
        NotificationTemplate entity = notificationTemplateMapper.toEntity(notificationTemplateDto);
        notificationTemplate.setName(entity.getName());
        notificationTemplate.setData(entity.getData());
        NotificationTemplate savedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        return notificationTemplateMapper.toDto(savedNotificationTemplate);
    }

    @Override
    public NotificationTemplateDto deleteNotificationTemplate(Byte id) {
        return null;
    }

    @Override
    public NotificationTemplateDto getNotificationTemplateDtoById(Byte id) {
        NotificationTemplate notificationTemplate = notificationTemplateRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(NOTIFICATION_TEMPLATE, "id", id)
        );
        notificationTemplate.setDeletedAt(Instant.now());
        NotificationTemplate savedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        return notificationTemplateMapper.toDto(savedNotificationTemplate);
    }
}
