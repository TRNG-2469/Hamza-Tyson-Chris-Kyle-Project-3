package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.service.ReimbursementService;
import io.javalin.http.Context;

import java.util.List;

public class ReimbursementHandlerImp implements ReimbursementHandler {
    private final ReimbursementService reimbursementService;

    public ReimbursementHandlerImp(ReimbursementService reimbursementService) {
        this.reimbursementService = reimbursementService;
    }

    // Create

    @Override
    public void createReimbursement(Context ctx) {
        User author = ctx.sessionAttribute("user");
        Reimbursement reimbursement = ctx.bodyAsClass(Reimbursement.class);
        reimbursementService.createReimbursement(reimbursement, author);
        ctx.status(201).result("Reimbursement created successfully.");
    }

    // Read

    @Override
    public void queryReimbursements(Context ctx) {
        Integer departmentId = ctx.queryParam("departmentId") != null ? Integer.valueOf(ctx.queryParam("departmentId")) : null;
        Status status = null;
        if (ctx.queryParam("status") != null) {
            try {
                status = Status.valueOf(ctx.queryParam("status").toUpperCase());
            } catch (IllegalArgumentException e) {
                ctx.status(400).result("Invalid status: " + ctx.queryParam("status"));
                return;
            }
        }
        List<Reimbursement> reimbursements = reimbursementService.queryReimbursements(status, departmentId);
        if (!reimbursements.isEmpty()) {
            ctx.status(200).json(reimbursements);
        } else {
            ctx.status(200).result("No reimbursements found.");
        }
    }

    @Override
    public void queryReimbursementByAuthorId(Context ctx) {
        int authorId = Integer.parseInt(ctx.pathParam("userId"));
        Status status = null;
        if (ctx.queryParam("status") != null) {
            try {
                status = Status.valueOf(ctx.queryParam("status").toUpperCase());
            } catch (IllegalArgumentException e) {
                ctx.status(400).result("Invalid status: " + ctx.queryParam("status"));
                return;
            }
        }
        List<Reimbursement> reimbursements = reimbursementService.queryReimbursementsByAuthorId(authorId, status);
        if (!reimbursements.isEmpty()) {
            ctx.status(200).json(reimbursements);
        } else {
            ctx.status(200).result("No reimbursements found.");
        }
    }

    // Update

    @Override
    public void updateReimbursement(Context ctx) {
        Reimbursement reimbursement = ctx.bodyAsClass(Reimbursement.class);
        Reimbursement updatedReimbursement = reimbursementService.updateReimbursement(reimbursement);
        ctx.status(200).json(updatedReimbursement);
    }

    @Override
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
