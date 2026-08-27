package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "Reimbursements")
@Data
@NoArgsConstructor
public class Reimbursement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reimbursementId;

    @Positive(message = "Reimbursements cannot be negative.")
    @Max(value = 10000, message = "Reimbursement cannot exceed $10000")
    private double amount;

    @NotBlank(message = "Description cannot be blank.")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Type cannot be null.")
    @Column(nullable = false)
    private Type type;

    // Not validated on input: the service forces PENDING on create.
    @Column(nullable = false)
    private Status status;

    // Plain foreign-key columns (see User.departmentId).
    @Column(name = "authorId", nullable = false)
    private int authorId;

    @Column(name = "resolverId")
    private Integer resolverId;
}
