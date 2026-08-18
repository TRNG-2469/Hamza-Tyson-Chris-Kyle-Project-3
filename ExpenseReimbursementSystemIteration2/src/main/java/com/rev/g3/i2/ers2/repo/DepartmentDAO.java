package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.model.Department;

import java.util.List;

public interface DepartmentDAO {
    Department queryDepartmentByDepartmentId(int departmentId);
    List<Department> queryDepartments();
}
