package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.service.UserService;
import io.javalin.http.Context;

public class UserHandlerImp implements UserHandler{
    private final UserService userService;

    public UserHandlerImp(UserService userService) {
        this.userService = userService;
    }

    @Override
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

    @Override
    public void register(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        User registeredUser = userService.register(user);
        if (registeredUser != null) {
            ctx.status(201).json(registeredUser);
        } else {
            ctx.status(400).result("Failed to register user.");
        }
    }

    public void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.status(200).result("Logged out successfully.");
    }

    public void requireLogin(Context ctx) {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.status(401).result("You must be logged in.");
            ctx.skipRemainingHandlers();
        }
    }

    public void requireManager(Context ctx) {
        User user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.status(401).result("You must be logged in.");
            ctx.skipRemainingHandlers();
            return;
        }
        if (user.getRole() != Role.MANAGER) {
            ctx.status(403).result("Manager access required.");
            ctx.skipRemainingHandlers();
        }
    }
}
