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

    @Pattern(regexp = "^\\+62\\d{10,14}$", message = "Phone number should be valid and contain 10 to 14 digits starting with +62")
    private String phoneNumber;
    private String group;
}
