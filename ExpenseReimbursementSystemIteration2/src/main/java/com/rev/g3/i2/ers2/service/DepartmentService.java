package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.model.Department;

import java.util.List;

public interface DepartmentService {
    Department queryDepartmentByDepartmentId(int id);
    List<Department> queryDepartments();
}
