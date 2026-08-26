package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok().build();
    }
}
