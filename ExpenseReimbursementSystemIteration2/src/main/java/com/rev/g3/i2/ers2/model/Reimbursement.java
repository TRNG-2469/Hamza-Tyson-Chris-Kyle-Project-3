package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
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
    @Max(value=1000, message="Reimbursement cannot exceed $1000")
    private double amount;
    @NotBlank(message="Description cannot be blank.")
    private String description;
    @NotBlank(message="Type cannot be blank.")
    private Type type;
    @NotBlank(message="Status cannot be blank.")
    private Status status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userID", nullable=false)
    private int authorId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userID", nullable=true)
    private Integer resolverId;
}
