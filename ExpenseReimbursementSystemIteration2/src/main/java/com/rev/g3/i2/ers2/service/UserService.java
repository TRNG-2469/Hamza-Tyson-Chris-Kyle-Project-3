package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public User searchByUsername(String username);
    public User login(String username, String password);
    public User register(User user);
    // diplay name at id
    public User findById(int id);

}
