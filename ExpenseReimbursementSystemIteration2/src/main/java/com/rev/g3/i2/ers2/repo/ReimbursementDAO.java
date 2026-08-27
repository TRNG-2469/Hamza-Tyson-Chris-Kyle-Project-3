package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementDAO extends JpaRepository<Reimbursement, Integer> {
    // JpaRepository already gives: findAll(), findById(), save(), deleteById(), existsById()...
    // Reimbursement stores plain ids (authorId), so the department filter joins through User by id.

    @Query("SELECT r FROM Reimbursement r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:departmentId IS NULL OR r.authorId IN " +
            "   (SELECT u.userId FROM User u WHERE u.departmentId = :departmentId))")
    List<Reimbursement> queryReimbursements(@Param("status") Status status,
                                            @Param("departmentId") Integer departmentId);

    @Query("SELECT r FROM Reimbursement r WHERE r.authorId = :authorId AND " +
            "(:status IS NULL OR r.status = :status)")
    List<Reimbursement> queryReimbursementsByAuthorId(@Param("authorId") int authorId,
                                                      @Param("status") Status status);
}
