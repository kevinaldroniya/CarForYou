package com.car.foryou.service.user;

import com.car.foryou.dto.user.UserFilterParam;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.dto.user.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserResponse> getAllUsers(UserFilterParam userFilterParam);

    UserResponse getUserById(int id);

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(int id, UserRequest request);

    UserResponse deleteUser(int id);

    UserResponse getUserByEmailOrUsernameOrPhoneNumber(String emailOrUsernameOrPhoneNumber);
}
