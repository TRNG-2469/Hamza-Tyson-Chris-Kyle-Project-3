package com.rev.g3.i2.ers2.controller;

import io.javalin.http.Context;

public interface ReimbursementHandler {
    // Create
    void createReimbursement(Context ctx);
    // Read
    void queryReimbursements(Context ctx);
    void queryReimbursementByAuthorId(Context ctx);
    // Update
    void updateReimbursement(Context ctx);
    void resolveReimbursement(Context ctx);
}
