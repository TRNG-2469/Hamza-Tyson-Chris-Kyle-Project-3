package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.security.UserPrincipal;
import com.rev.g3.i2.ers2.service.ReimbursementService;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReimbursementHandler {
    private final ReimbursementService reimbursementService;

    public ReimbursementHandler(ReimbursementService reimbursementService) {
        this.reimbursementService = reimbursementService;
    }

    // Create
    @PostMapping("/reimbursements")
    public ResponseEntity createReimbursement(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody Reimbursement reimbursement) {
        int authorId = user.getUser().getUserId();
        reimbursementService.createReimbursement(reimbursement, authorId);
        return ResponseEntity.ok().build();
    }

    // Read
    @GetMapping("/reimbursements/{id}")
    public ResponseEntity<List<Reimbursement>> queryReimbursementByAuthorId(@PathVariable int id,
                                             @RequestParam(name="status", required=false) String status) {

        Status statusEnum = null;
        if (status != null) {
            try {
                statusEnum = Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok().body(reimbursementService.queryReimbursementsByAuthorId(id, statusEnum));
    }

    @GetMapping("/manager/reimbursements")
    public ResponseEntity<List<Reimbursement>> queryReimbursements(@Param("status") String status,
                                                                   @Param("departmentId") Integer departmentId) {

        Status statusEnum = null;
        if (status != null) {
            try {
                statusEnum = Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok().body(reimbursementService.queryReimbursements(statusEnum, departmentId));
    }

    // Update
    @PatchMapping("/reimbursements/{id}")
    public ResponseEntity updateReimbursement(@PathVariable int id, @Valid @RequestBody Reimbursement reimbursement) {
        reimbursementService.updateReimbursement(id, reimbursement);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/manager/reimbursements/{id}")
    public ResponseEntity resolveReimbursement(@PathVariable int id, @Valid @RequestBody Reimbursement reimbursement) {
        reimbursementService.updateReimbursement(id, reimbursement);
        return ResponseEntity.ok().build();
    }
}
