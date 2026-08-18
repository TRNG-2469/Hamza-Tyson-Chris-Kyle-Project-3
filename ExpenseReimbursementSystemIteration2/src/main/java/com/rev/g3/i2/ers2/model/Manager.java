package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Role;

public class Manager extends User{
    private Manager() {
        super(0, "", "", "", "", Role.MANAGER, 0);
    }

    public Manager(int userId, String username, String password, String firstName, String lastName, int departmentId) {
        super(userId, username, password, firstName, lastName, Role.MANAGER, departmentId);
    }
}
