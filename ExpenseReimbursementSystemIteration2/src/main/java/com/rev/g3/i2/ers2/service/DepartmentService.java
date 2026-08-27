package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.model.Department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {
    Department findByDepartmentId(int id);
    List<Department> queryDepartments();
}
