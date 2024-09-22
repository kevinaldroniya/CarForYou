package com.car.foryou.util.mapper;

import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class UserMapper {

    public UserResponse mapToUserResponse(User user){
     try {
         return UserResponse.builder()
                 .firstName(user.getFirstName())
                 .lastName(user.getLastName())
                 .username(user.getUsername())
                 .email(user.getEmail())
                 .phoneNumber(user.getPhoneNumber())
                 .group(user.getGroup().getName())
                 .isVerified(user.isVerified())
                 .build();
     }catch (Exception e){
         throw new RuntimeException("Error while mapping user to userResponse");
     }
    }

    public User mapToUser(UserRequest userRequest, Group group){
        try {
            return User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .email(userRequest.getEmail())
                    .username(userRequest.getUsername())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .password(userRequest.getPassword())
                    .group(group)
                    .isVerified(false)
                    .createdAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                    .createdBy(1L)
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping userRequest to user");
        }
    }
}
