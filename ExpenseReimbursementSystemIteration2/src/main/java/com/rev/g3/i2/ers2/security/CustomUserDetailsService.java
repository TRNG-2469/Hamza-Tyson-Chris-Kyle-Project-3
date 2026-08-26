package com.rev.g3.i2.ers2.security;

import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.repo.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
// Spring Security's AuthenticationManager calls this automatically during login —
// this is the bridge between "an authentication attempt happened" and "here's your database"
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    @Autowired
    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            // only throw in spring security filer chain
            throw UsernameNotFoundException.fromUsername(username);

        }
        return new UserPrincipal(user);
    }
}
