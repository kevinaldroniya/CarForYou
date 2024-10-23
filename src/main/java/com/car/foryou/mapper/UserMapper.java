package com.car.foryou.mapper;

import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class UserMapper {

    public UserResponse mapToUserResponse(User user){
     try {
         return UserResponse.builder()
                 .id(user.getId())
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
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping userRequest to user");
        }
    }

    public UserInfoDetails mapUserToUserDetails(User user){
        try {
            Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getGroup().getName()));
            return UserInfoDetails.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping user to userInfoDetails");
        }
    }
}
