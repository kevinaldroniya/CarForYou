package com.car.foryou.service.email;

import com.car.foryou.dto.email.MailBody;

public interface EmailService {
    void sendSimpleMessage(MailBody mailBody);
}
