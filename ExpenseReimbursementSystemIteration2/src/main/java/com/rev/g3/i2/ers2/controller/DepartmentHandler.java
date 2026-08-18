package com.rev.g3.i2.ers2.controller;

import io.javalin.http.Context;

public interface DepartmentHandler {
    void findDepartmentById(Context ctx);
    void findAll(Context ctx);
}
