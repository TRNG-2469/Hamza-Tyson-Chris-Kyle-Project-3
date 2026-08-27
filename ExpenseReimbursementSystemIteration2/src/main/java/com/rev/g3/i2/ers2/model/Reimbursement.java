package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name="Reimbursements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reimbursement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reimbursementId;
    @Positive(message="Reimbursements cannot be negative.")
    @Max(value=10000, message="Reimbursement cannot exceed $10000")
    private double amount;
    @NotBlank(message="Description cannot be blank.")
    private String description;
    @NotNull(message="Type cannot be null.")
    @Enumerated(EnumType.STRING)
    private Type type;
    @NotNull(message="Status cannot be null.")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name="authorId", nullable=false)
    private int authorId;
    @Column(name="resolverId")
    private Integer resolverId;
}
