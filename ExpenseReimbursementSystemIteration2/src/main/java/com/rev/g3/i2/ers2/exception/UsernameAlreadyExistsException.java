package com.rev.g3.i2.ers2.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    // unchecked exception thrown from the UserServiceImp when register() finds a duplicate username
    public UsernameAlreadyExistsException(String username){
        super("Username already exist: " + username);



    }

}
