package com.rev.g3.i2.ers2.dto;

import com.rev.g3.i2.ers2.enums.Role;

public class AuthResponse {
    // response body for Post/login, outputs

    // this is the token that will be sent back to user if login is successful and verify who user are on other request
    private String token;
    private String username;
    private Role role;

    public AuthResponse(String token, String username, Role role ) {

    }



}
