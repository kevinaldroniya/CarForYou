package com.car.foryou.controller;

import com.car.foryou.dto.user.UserFilterParam;
import com.car.foryou.dto.user.UserRequest;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<UserResponse>> getAllUserByFilter(@Valid @ModelAttribute UserFilterParam filterParam
                                                                 ){
        Page<UserResponse> allUsers = userService.getAllUsers(filterParam);
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request){
        UserResponse user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
