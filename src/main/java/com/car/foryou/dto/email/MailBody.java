package com.car.foryou.dto.email;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
