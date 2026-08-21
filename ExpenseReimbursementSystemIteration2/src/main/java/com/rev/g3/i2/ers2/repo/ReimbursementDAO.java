package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.model.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementDAO  extends JpaRepository<Reimbursement,Integer> {
    // Create
    Reimbursement createReimbursement(Reimbursement reimbursement);
    // Read
    List<Reimbursement> queryReimbursements(Status status, Integer departmentId);
    List<Reimbursement> queryReimbursementsByAuthorId(int authorId, Status status);
    Reimbursement queryReimbursementByReimbursementId(int reimbursementId);
    // Update
    Reimbursement updateReimbursement(Reimbursement reimbursement);

}
