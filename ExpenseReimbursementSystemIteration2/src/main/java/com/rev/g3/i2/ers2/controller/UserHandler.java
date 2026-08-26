package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"/", "/login"})
    public void login(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        User authenticatedUser = userService.login(
                user.getUsername(),
                user.getPassword()
        );
        if (authenticatedUser == null) {
            ctx.status(401).result("Invalid username or password.");
            return;
        }
        ctx.sessionAttribute("user", authenticatedUser);
        ctx.status(200).json(authenticatedUser);
    }

    @GetMapping("/register")
    public void register(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        User registeredUser = userService.register(user);
        if (registeredUser != null) {
            ctx.status(201).json(registeredUser);
        } else {
            ctx.status(400).result("Failed to register user.");
        }
    }

    @GetMapping("/logout")
    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.status(200).result("Logged out successfully.");
    }
}
