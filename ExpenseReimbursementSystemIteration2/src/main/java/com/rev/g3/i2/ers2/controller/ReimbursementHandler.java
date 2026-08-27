package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.security.UserPrincipal;
import com.rev.g3.i2.ers2.service.ReimbursementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Reimbursement> createReimbursement(@AuthenticationPrincipal UserPrincipal user,
                                                             @Valid @RequestBody Reimbursement reimbursement) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(reimbursementService.createReimbursement(reimbursement, user.getUser()));
    }

    // Read
    @GetMapping("/reimbursements/{id}")
    public ResponseEntity<List<Reimbursement>> queryReimbursementByAuthorId(
            @PathVariable int id,
            @RequestParam(name = "status", required = false) String status) {
        Status statusEnum;
        try {
            statusEnum = parseStatus(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reimbursementService.queryReimbursementsByAuthorId(id, statusEnum));
    }

    @GetMapping("/manager/reimbursements")
    public ResponseEntity<List<Reimbursement>> queryReimbursements(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "departmentId", required = false) Integer departmentId) {
        Status statusEnum;
        try {
            statusEnum = parseStatus(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reimbursementService.queryReimbursements(statusEnum, departmentId));
    }

    // Update (employee edits a still-pending reimbursement)
    @PatchMapping("/reimbursements/{id}")
    public ResponseEntity<Reimbursement> updateReimbursement(@PathVariable int id,
                                                             @Valid @RequestBody Reimbursement reimbursement) {
        return ResponseEntity.ok(reimbursementService.updateReimbursement(id, reimbursement));
    }

    // Resolve (manager approves / denies). Body only needs {"status": "APPROVED"} or {"status": "DENIED"}.
    @PatchMapping("/manager/reimbursements/{id}")
    public ResponseEntity<Reimbursement> resolveReimbursement(@AuthenticationPrincipal UserPrincipal manager,
                                                              @PathVariable int id,
                                                              @RequestBody Reimbursement body) {
        if (manager == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (body.getStatus() == null) {
            throw new IllegalArgumentException("Status is required to resolve a reimbursement.");
        }
        return ResponseEntity.ok(reimbursementService.resolveReimbursement(id, manager.getUser(), body.getStatus()));
    }

    /** Accepts either the enum name ("APPROVED") or the db value ("approved"); null stays null. */
    private static Status parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Status.fromDbValue(status);
        }
    }
}
