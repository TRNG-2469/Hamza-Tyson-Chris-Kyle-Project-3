package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Reimbursement;

import java.util.List;

public interface ReimbursementDAO {
    // Create
    Reimbursement createReimbursement(Reimbursement reimbursement);
    // Read
    List<Reimbursement> queryReimbursements(Status status, Integer departmentId);
    List<Reimbursement> queryReimbursementsByAuthorId(int authorId, Status status);
    Reimbursement queryReimbursementByReimbursementId(int reimbursementId);
    // Update
    Reimbursement updateReimbursement(Reimbursement reimbursement);

}
