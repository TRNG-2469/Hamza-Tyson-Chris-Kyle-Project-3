package com.rev.g3.i2.ers2.dto;

import com.rev.g3.i2.ers2.enums.Role;

public class AuthResponse {
    // response body for Post/login, outputs

    // this is the token that will be sent back to user if login is successful and verify who user are on other request
    private String token;
    private String username;
    private Role role;
    // userid for login and later call that need userid parameter
    private int userId;

    public AuthResponse(String token, String username, Role role, int userId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;


    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }
}
