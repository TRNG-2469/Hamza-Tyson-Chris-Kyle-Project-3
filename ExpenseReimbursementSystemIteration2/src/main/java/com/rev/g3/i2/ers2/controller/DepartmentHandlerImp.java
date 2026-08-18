package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.service.DepartmentService;
import io.javalin.http.Context;

import java.util.List;

public class DepartmentHandlerImp implements DepartmentHandler {
    private final DepartmentService departmentService;

    public DepartmentHandlerImp(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Override
    public void findDepartmentById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Department foundDepartment = departmentService.queryDepartmentByDepartmentId(id);
        if (foundDepartment != null) {
            ctx.status(200).json(foundDepartment);
        } else {
            ctx.status(404).result("Department not found.");
        }
    }

    @Override
    public void findAll(Context ctx) {
        List<Department> departments = departmentService.queryDepartments();
        ctx.status(200).json(departments);
    }
}
