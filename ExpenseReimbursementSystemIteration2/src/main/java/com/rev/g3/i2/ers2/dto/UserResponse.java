package com.rev.g3.i2.ers2.dto;

import com.rev.g3.i2.ers2.enums.Role;

public class UserResponse {
    // response body for GET /user for frontend who's currently logged in

    private int userId;
    private String username;
    private Role role;

    public UserResponse(int userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
}