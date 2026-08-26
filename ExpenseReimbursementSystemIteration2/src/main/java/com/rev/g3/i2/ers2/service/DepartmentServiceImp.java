package com.rev.g3.i2.ers2.service;

import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.repo.DepartmentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImp implements DepartmentService {
    private final DepartmentDAO departmentDAO;

    @Autowired
    public DepartmentServiceImp(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    @Override
    public Department queryDepartmentByDepartmentId(int departmentId) {
        if(departmentId <= 0){
            throw new IllegalArgumentException("Department ID cannot be negative or zero.");
        }
        return departmentDAO.findByDepartmentId(departmentId);
    }

    @Override
    public List<Department> queryDepartments() {
        return departmentDAO.findAll();
    }
}
