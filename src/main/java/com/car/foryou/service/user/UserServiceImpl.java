package com.car.foryou.service.user;

import com.car.foryou.dto.user.UserFilterParam;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import com.car.foryou.repository.group.GroupRepository;
import com.car.foryou.repository.user.UserRepository;
import com.car.foryou.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserMapper userMapper;

    private static final String USER = "USER";

    public UserServiceImpl(UserRepository userRepository, GroupRepository groupRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(USER, "ID", id)
        );
    }

    @Override
    public User getUserByEmailOrUsernameOrPhoneNumber(String emailOrUsernameOrPhoneNumber) {
        return  userRepository.findByEmailOrUsernameOrPhoneNumber(emailOrUsernameOrPhoneNumber, emailOrUsernameOrPhoneNumber, emailOrUsernameOrPhoneNumber).orElseThrow(
                () -> new ResourceNotFoundException("User","Email, Username, or Phone Number",emailOrUsernameOrPhoneNumber)
        );
    }

    @Override
    public Page<UserResponse> getAllUsersResponse(UserFilterParam userFilterParam) {
        Sort sort = userFilterParam.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(userFilterParam.getSortBy()).ascending() : Sort.by(userFilterParam.getSortBy()).descending();
        Pageable pageable = PageRequest.of(userFilterParam.getPage(), userFilterParam.getSize(), sort);
        Page<User> users = userRepository.findAllByFilter(userFilterParam.getUsername(), pageable);
        return users.map(userMapper::mapToUserResponse);
    }

    @Override
    public UserResponse getUserResponseById(int id) {
        User user = getUserById(id);
        return userMapper.mapToUserResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
      return null;
    }

    @Override
    public UserResponse updateUserProfile(int id, UserRequest request) {
        User user = getUserById(id);
//
//        Group group = groupRepository.findByName(request.getGroup()).orElseThrow(
//                () -> new RuntimeException("Group with given name : '"+request.getGroup()+"', was not found")
//        );

        updateUser(user, request);
        User saved = userRepository.save(user);
        return userMapper.mapToUserResponse(saved);
    }

    @Override
    public UserResponse deleteUser(int id) {
        return null;
    }

    @Override
    public UserResponse getUserResponseByEmailOrUsernameOrPhoneNumber(String emailOrUsernameOrPhoneNumber) {
        User user = getUserByEmailOrUsernameOrPhoneNumber(emailOrUsernameOrPhoneNumber);
        return userMapper.mapToUserResponse(user);
    }

    private void updateUser(User user, UserRequest request){
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUpdatedAt(Instant.now());
        user.setUpdatedBy(1);
    }
}
