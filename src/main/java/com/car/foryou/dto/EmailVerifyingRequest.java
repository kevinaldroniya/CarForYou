package com.car.foryou.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailVerifyingRequest {

    @Email(message = "Please input valid email")
    private String email;

    @NotNull
    private Integer otp;
}
