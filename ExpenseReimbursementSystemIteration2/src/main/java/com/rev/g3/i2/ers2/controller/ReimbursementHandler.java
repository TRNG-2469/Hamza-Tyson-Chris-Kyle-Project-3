package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.security.UserPrincipal;
import com.rev.g3.i2.ers2.service.ReimbursementService;
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
    public void createReimbursement(Context ctx) {
        User author = ctx.sessionAttribute("user");
        Reimbursement reimbursement = ctx.bodyAsClass(Reimbursement.class);
        reimbursementService.createReimbursement(reimbursement, author);
        ctx.status(201).result("Reimbursement created successfully.");
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
    public void updateReimbursement(Context ctx) {
        Reimbursement reimbursement = ctx.bodyAsClass(Reimbursement.class);
        Reimbursement updatedReimbursement = reimbursementService.updateReimbursement(reimbursement);
        ctx.status(200).json(updatedReimbursement);
    }

    @PatchMapping("/manager/reimbursements/{id}")
    public void resolveReimbursement(Context ctx) {
        Reimbursement resolution = ctx.bodyAsClass(Reimbursement.class);
        Status status = resolution.getStatus();
        int reimbursementId = Integer.parseInt(ctx.pathParam("reimbursementId"));
        if (status == null) {
            ctx.status(400).result("Status parameter is required.");
            return;
        }
        User manager = ctx.sessionAttribute("user");
        Reimbursement updatedReimbursement = reimbursementService.resolveReimbursement(reimbursementId, manager, status);
        ctx.status(200).json(updatedReimbursement);
    }
}
