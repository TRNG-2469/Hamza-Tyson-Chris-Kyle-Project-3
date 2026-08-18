package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.model.User;

public interface UserDAO {
    public User searchByUsername(String username);
    public User register(User user);
}
