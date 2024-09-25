package com.car.foryou.service;

import com.car.foryou.dto.auth.MailBody;

public interface EmailService {
    void sendSimpleMessage(MailBody mailBody);
}
