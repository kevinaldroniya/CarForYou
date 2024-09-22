package com.car.foryou.service;

import com.car.foryou.dto.MailBody;

public interface EmailService {
    void sendSimpleMessage(MailBody mailBody);
}
