package com.car.foryou.service.user;

import com.car.foryou.dto.user.UserFilterParam;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Integer id);
    User getUserByEmailOrUsernameOrPhoneNumber(String emailOrUsernameOrPhoneNumber);
    Page<UserResponse> getAllUsersResponse(UserFilterParam userFilterParam);

    UserResponse getUserResponseById(int id);

    UserResponse createUser(UserRequest request);

    UserResponse updateUserProfile(int id, UserRequest request);

    UserResponse deleteUser(int id);

    UserResponse getUserResponseByEmailOrUsernameOrPhoneNumber(String emailOrUsernameOrPhoneNumber);

}
