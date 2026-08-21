package com.rev.g3.i2.ers2.controller;

import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DepartmentHandler {
    private final DepartmentService departmentService;

    public DepartmentHandler(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<Department> findDepartmentById(@PathVariable int id) {
        return ResponseEntity.ok().body(departmentService.findDepartmentById(id));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> findAll() {
        return ResponseEntity.ok().body(departmentService.findAll());
    }
}
