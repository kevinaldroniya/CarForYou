package com.car.foryou.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String group;
}
