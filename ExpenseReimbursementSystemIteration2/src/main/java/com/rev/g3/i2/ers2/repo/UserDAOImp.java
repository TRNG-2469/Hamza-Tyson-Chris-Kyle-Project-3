package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.model.User;
import com.rev.g3.i2.ers2.utils.ConnectionFactory;
import com.rev.g3.i2.ers2.utils.UserFactory;
import java.sql.*;

public class UserDAOImp implements UserDAO {
    @Override
    public User searchByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setString(1, username);

            try (ResultSet result = prep.executeQuery()){
                if (result.next()) {
                    return UserFactory.createUser(result.getInt("id"), result.getString("username"),
                            result.getString("password"), result.getString("first_name"), result.getString("last_name"),
                            result.getString("role"), result.getInt("department_id"));
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    @Override
    public User register(User user) {
        String sql = "INSERT INTO users (username, password, first_name, last_name, role, department_id) VALUES(?, ?, ?, ?, ?, ?)";
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, user.getUsername());
            prep.setString(2, user.getPassword());
            prep.setString(3, user.getFirstName());
            prep.setString(4, user.getLastName());
            prep.setString(5, user.getRole().getDbValue());
            prep.setInt(6, user.getDepartmentId());
            prep.executeUpdate();
            try (ResultSet generatedKeys = prep.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Database error", e);
        }
        return user;
    }
}
