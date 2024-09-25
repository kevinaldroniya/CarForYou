package com.car.foryou.dto.auth;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
