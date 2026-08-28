package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.dto.AuthResponse;
import com.rev.g3.i2.ers2.dto.LoginRequest;
import com.rev.g3.i2.ers2.dto.UserResponse;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.security.JwtService;
import com.rev.g3.i2.ers2.security.UserPrincipal;
import com.rev.g3.i2.ers2.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserHandler {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserHandler(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService)
    {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // checks the password against CustomUserDetailsService + PasswordEncoder and throws BadCredentialsException if wrong
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        // the authenticated user pulled back out of the Authentication result
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        // sign a fresh token for this login
        String token = jwtService.generateToken(principal);
        AuthResponse response = new AuthResponse(token, principal.getUsername(), principal.getUser().getRole());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        // JwtAuthFilter already verified the token and loaded this user before this method ever runs
        User user = principal.getUser();
        UserResponse response = new UserResponse(user.getUserId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {

        // Find the user by the ID from the URL
        User user = userService.findById(id);

        // If user does not exist return 404 Not Found
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Convert User entity into UserResponse DTO
        UserResponse response =
                new UserResponse(user.getUserId(), user.getUsername(), user.getRole());

        // Return the user data with HTTP 200 OK
        return ResponseEntity.ok(response);
    }
}



