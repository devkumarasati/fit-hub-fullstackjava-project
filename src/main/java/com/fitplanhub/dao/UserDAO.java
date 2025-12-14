package com.fitplanhub.dao;

import com.fitplanhub.model.User;
import com.fitplanhub.util.DatabaseUtil;
import com.fitplanhub.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (user_id, full_name, email, password_hash, user_type) " +
                "VALUES (user_seq.NEXTVAL, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getUserType());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(rs.getString("user_type"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }

            return null;

        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }

            return null;

        } catch (SQLException e) {
            System.out.println("Error getting user: " + e.getMessage());
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public List<User> getAllTrainers() {
        String sql = "SELECT * FROM users WHERE user_type = 'TRAINER'";
        List<User> trainers = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                User trainer = new User();
                trainer.setUserId(rs.getInt("user_id"));
                trainer.setFullName(rs.getString("full_name"));
                trainer.setEmail(rs.getString("email"));
                trainer.setUserType(rs.getString("user_type"));
                trainer.setCreatedAt(rs.getTimestamp("created_at"));
                trainers.add(trainer);
            }

            return trainers;

        } catch (SQLException e) {
            System.out.println("Error getting trainers: " + e.getMessage());
            return trainers;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}