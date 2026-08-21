package com.rev.g3.i2.ers2.exception;

public class DepartmentNotFoundException extends RuntimeException{

    // unchecked exception thrown when a ref departmentId DNE
    public DepartmentNotFoundException(int departmentId){
        super("Department not found with ID: " + departmentId);
    }
}

