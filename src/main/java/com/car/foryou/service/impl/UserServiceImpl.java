package com.car.foryou.service.impl;

import com.car.foryou.dto.user.UserFilterParam;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import com.car.foryou.repository.GroupRepository;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.UserService;
import com.car.foryou.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, GroupRepository groupRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponse> getAllUsers(UserFilterParam userFilterParam) {
        Sort sort = userFilterParam.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(userFilterParam.getSortBy()).ascending() : Sort.by(userFilterParam.getSortBy()).descending();
        Pageable pageable = PageRequest.of(userFilterParam.getPage(), userFilterParam.getSize(), sort);
        Page<User> users = userRepository.findAllByFilter(userFilterParam.getUsername(), pageable);
        return users.map(userMapper::mapToUserResponse);
    }

    @Override
    public UserResponse getUserById(int id) {
        return null;
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        Group group = groupRepository.findByName(request.getGroup()).orElseThrow(
                () -> new RuntimeException("Groups with given name : '" +request.getGroup()+ "'")
        );
        userRepository.findByEmailOrUsernameOrPhoneNumber(
                request.getEmail(), request.getUsername(), request.getPhoneNumber())
                .ifPresent(user -> {
                    throw new RuntimeException("User already exists");
                });
        User user = userMapper.mapToUser(request, group);
        User save = userRepository.save(user);
        return userMapper.mapToUserResponse(save);
    }

    @Override
    public UserResponse updateUser(int id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User with given id : '" +id+ "', was not found")
        );

        Group group = groupRepository.findByName(request.getGroup()).orElseThrow(
                () -> new RuntimeException("Group with given name : '"+request.getGroup()+"', was not found")
        );

        User updatedUser = updateUser(user, request, group);
        User saved = userRepository.save(updatedUser);
        return userMapper.mapToUserResponse(saved);
    }

    @Override
    public UserResponse deleteUser(int id) {
        return null;
    }

    private User updateUser(User user, UserRequest request, Group group){
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setGroup(group);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUpdatedAt((int) ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
        user.setUpdatedBy(1);
        return user;
    }
}
