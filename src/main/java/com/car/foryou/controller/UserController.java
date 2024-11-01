package com.car.foryou.controller;

import com.car.foryou.dto.user.UserFilterParam;
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
public class UserController extends BaseApiControllerV1 {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<UserResponse>> getAllUserByFilter(@Valid @ModelAttribute UserFilterParam filterParam){
        Page<UserResponse> allUsers = userService.getAllUsersResponse(filterParam);
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserResponseById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(userService.getUserResponseById(id));
    }
}
