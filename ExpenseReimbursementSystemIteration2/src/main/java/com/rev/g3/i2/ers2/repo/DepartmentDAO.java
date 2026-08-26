package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentDAO extends JpaRepository<Department, Integer> {
    Department findByDepartmentId(int departmentId);
}