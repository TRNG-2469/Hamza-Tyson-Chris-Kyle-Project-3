package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ReimbursementService {
    // Create
    Reimbursement createReimbursement(Reimbursement reimbursement, User author);
    // Read
    List<Reimbursement> queryReimbursements(Status status, Integer departmentId);
    List<Reimbursement> queryReimbursementsByAuthorId(int authorId, Status status);
    Reimbursement queryReimbursementByReimbursementId(int reimbursementId);
    // Update
    Reimbursement updateReimbursement(int id, Reimbursement reimbursement);
    Reimbursement resolveReimbursement(int reimbursementId, User manager, Status status);
}
