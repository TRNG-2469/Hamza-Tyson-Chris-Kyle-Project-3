package com.rev.g3.i2.ers2.security;

import com.rev.g3.i2.ers2.enums.Role;
import com.rev.g3.i2.ers2.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final User user;

    // constructor injection of user object type User
    public UserPrincipal(User user){
        this.user = user;
    }
    // provide implementation to the interface method defined in UserDetails
    @Override
    // get user role enum, turn to string, by naming convention ROLE_ + the role i am getting
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole() == null ? Role.EMPLOYEE : user.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override
    public String getPassword() {
        // return the bcryp password and compare to the PasswordEncoder
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // get original user object
    public User getUser(){
        return user;
    }

}