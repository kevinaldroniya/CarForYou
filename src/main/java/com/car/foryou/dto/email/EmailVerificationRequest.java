package com.car.foryou.dto.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailVerificationRequest {
    private String email;
}
