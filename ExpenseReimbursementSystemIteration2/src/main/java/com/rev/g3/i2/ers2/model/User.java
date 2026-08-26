package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Entity
@Table(name="Users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
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
    @NotBlank(message="Role cannot be blank.")
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="departmentId", nullable=false)
    private int departmentId;
}
