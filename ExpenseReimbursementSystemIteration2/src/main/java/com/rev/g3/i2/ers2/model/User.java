package com.rev.g3.i2.ers2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rev.g3.i2.ers2.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @NotBlank(message = "Username cannot be blank.")
    @Column(nullable = false, unique = true)
    private String username;

    // Accepted on input (register/login) but never written back out in a JSON response.
    @NotBlank(message = "Password cannot be blank.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "First name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name cannot include numbers.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name cannot include numbers.")
    private String lastName;

    // Stored as its lowercase db value via RoleConverter. Defaults to EMPLOYEE on registration.
    @Column(nullable = false)
    private Role role;

    // Plain foreign-key column. (A @ManyToOne needs an entity-typed field, not an int.)
    @Column(name = "departmentId", nullable = false)
    private int departmentId;
}
