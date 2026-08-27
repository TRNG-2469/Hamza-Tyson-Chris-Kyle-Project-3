package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Objects;

@Entity
@Table(name="Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @NotBlank(message="Username cannot be blank.")
    private String username;
    @NotBlank(message="Password cannot be blank.")
    private String password;
    @NotBlank(message="First name cannot be blank.")
    @Pattern(regexp="^[a-zA-Z]+$", message="First name cannot include numbers.")
    private String firstName;
    @NotBlank(message="Last name cannot be blank.")
    @Pattern(regexp="^[a-zA-Z]+$", message="Last name cannot include numbers.")
    private String lastName;
    //NotBlank cannot be use with enum type
    @NotNull(message="Role cannot be blank.")
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="departmentId", nullable=false)
    private int departmentId;
}
