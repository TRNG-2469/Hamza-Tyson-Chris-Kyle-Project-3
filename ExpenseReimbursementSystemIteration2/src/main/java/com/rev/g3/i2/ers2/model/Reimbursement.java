package com.rev.g3.i2.ers2.model;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;

import java.util.Objects;

public class Reimbursement {
    private int reimbursementId;
    private double amount;
    private String description;
    private Type type;
    private Status status;
    private int authorId;
    private int resolverId;

    private Reimbursement() {}

    public Reimbursement(int reimbursementId, double amount, String description, Type type, Status status,
                         int authorId, int resolverId) {
        this.reimbursementId = reimbursementId;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.status = status;
        this.authorId = authorId;
        this.resolverId = resolverId;
    }

    public int getReimbursementId() {
        return reimbursementId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getResolverId() {
        return resolverId;
    }

    public void setResolverId(int resolverId) {
        this.resolverId = resolverId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public void setReimbursementId(int reimbursementId) {
        this.reimbursementId = reimbursementId;
    }

    @Override
    public String toString() {
        return "Reimbursement{" +
                "reimbursement_id=" + reimbursementId +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", author_id=" + authorId +
                ", resolver_id=" + resolverId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reimbursement that)) return false;
        return getReimbursementId() == that.getReimbursementId() &&
                Double.compare(getAmount(), that.getAmount()) == 0 &&
                getAuthorId() == that.getAuthorId() &&
                getResolverId() == that.getResolverId() &&
                Objects.equals(getDescription(), that.getDescription()) &&
                getType() == that.getType() &&
                getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReimbursementId(), getAmount(), getDescription(),
                getType(), getStatus(), getAuthorId(), getResolverId());
    }
}
