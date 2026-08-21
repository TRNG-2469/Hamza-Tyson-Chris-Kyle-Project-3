package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {
}
