package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.model.Department;
import com.rev.g3.i2.ers2.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImp implements DepartmentDAO{
    @Override
    public Department queryDepartmentByDepartmentId(int departmentId) {
        String sql = "SELECT * FROM departments WHERE department_id = ?;";
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setInt(1, departmentId);
            try(ResultSet result = prep.executeQuery()) {
                if(result.next()) {
                    return mapResultSetToDepartment(result);
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    @Override
    public List<Department> queryDepartments() {
        String sql = "SELECT * FROM departments;";
        List<Department> allDepts = new ArrayList<>();
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql);
            try(ResultSet result = prep.executeQuery()) {
                while(result.next()){
                    allDepts.add(mapResultSetToDepartment(result));
                }
            }
            return allDepts;
        } catch(SQLException e){
            throw new RuntimeException("Database error", e);
        }
    }

    private Department mapResultSetToDepartment(ResultSet result) throws SQLException{
        int deptId = result.getInt(1);
        String name = result.getString(2);
        return new Department(deptId, name);
    }
}
