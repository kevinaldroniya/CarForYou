package com.car.foryou.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;
    private String username;
    private String password;

    @Pattern(regexp = "^0\\d{9,13}$", message = "Phone number should be valid and contain 10 to 14 digits starting with 0")
    private String phoneNumber;
}
