package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.enums.Status;
import com.rev.g3.i2.ers2.enums.Type;
import com.rev.g3.i2.ers2.model.Reimbursement;
import com.rev.g3.i2.ers2.utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReimbursementDAOImp implements ReimbursementDAO{

    // Create

    @Override
    public Reimbursement createReimbursement(Reimbursement reimbursement) {
        String sql = "INSERT INTO reimbursements(amount, description, type, author_id) VALUES(?, ?, ?, ?)";
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prep.setDouble(1, reimbursement.getAmount());
            prep.setString(2, reimbursement.getDescription());
            prep.setString(3, reimbursement.getType().getDbValue());
            prep.setInt(4, reimbursement.getAuthorId());
            prep.executeUpdate();
            try (ResultSet generatedKeys = prep.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reimbursement.setReimbursementId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating reimbursement failed, no ID obtained.");
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Database error", e);
        }
        return reimbursement;
    }

    // Read

    @Override
    public List<Reimbursement> queryReimbursements(Status status, Integer departmentId) {
        StringBuilder sql = new StringBuilder("""
        SELECT r.*
        FROM reimbursements r
        JOIN users u ON r.author_id = u.id
        WHERE 1 = 1
        """);
        if (status != null) {
            sql.append(" AND status = ?");
        }
        if (departmentId != null) {
            sql.append(" AND department_id = ?");
        }
        sql.append(";");
        List<Reimbursement> allReimbs = new ArrayList<>();
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (status != null) {
                prep.setString(paramIndex++, status.getDbValue());
            }
            if (departmentId != null) {
                prep.setInt(paramIndex++, departmentId);
            }
            try(ResultSet result = prep.executeQuery()){
                while(result.next()){
                    allReimbs.add(mapResultSetToReimbursement(result));
                }
            }
            return allReimbs;
        } catch(SQLException e){
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public List<Reimbursement> queryReimbursementsByAuthorId(int authorId, Status status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM reimbursements WHERE author_id = ?");
        if (status != null) {
            sql.append(" AND status = ?");
        }
        sql.append(";");
        List<Reimbursement> allReimbs = new ArrayList<>();
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            prep.setInt(paramIndex++, authorId);
            if (status != null) {
                prep.setString(paramIndex++, status.getDbValue());
            }
            try(ResultSet result = prep.executeQuery()){
                while(result.next()){
                    allReimbs.add(mapResultSetToReimbursement(result));
                }
            }
            return allReimbs;
        } catch(SQLException e){
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public Reimbursement queryReimbursementByReimbursementId(int reimbursementId) {
        String sql = "SELECT * FROM reimbursements WHERE reimbursement_id = ?;";
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setInt(1, reimbursementId);
            try(ResultSet result = prep.executeQuery()){
                if(result.next()){
                    return mapResultSetToReimbursement(result);
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    // Update

    @Override
    public Reimbursement updateReimbursement(Reimbursement reimbursement) {
        String sql = "UPDATE reimbursements SET amount = ?, description = ?, type = ?, status = ?, resolver_id = ? WHERE reimbursement_id = ?;";
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setDouble(1, reimbursement.getAmount());
            prep.setString(2, reimbursement.getDescription());
            prep.setString(3, reimbursement.getType().getDbValue());
            prep.setString(4, reimbursement.getStatus().getDbValue());
            prep.setInt(5, reimbursement.getResolverId());
            prep.setInt(6, reimbursement.getReimbursementId());
            prep.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Database error", e);
        }
        return reimbursement;
    }

    private Reimbursement mapResultSetToReimbursement(ResultSet result) throws SQLException {
        int reimbursementID = result.getInt(1);
        double amount = result.getDouble(2);
        String description = result.getString(3);
        Type type = Type.fromDbValue(result.getString(4));
        Status status = Status.fromDbValue(result.getString(5));
        int author = result.getInt(6);
        int resolver = result.getInt(7);
        return new Reimbursement(reimbursementID, amount, description, type, status, author, resolver);
    }
}
