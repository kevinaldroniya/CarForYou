package com.car.foryou.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequestOtp {
    private String email;
}
