package com.fitplanhub.dao;

import com.fitplanhub.util.DatabaseUtil;
import com.fitplanhub.util.PasswordUtil;

import java.sql.*;

public class AuthTokenDAO {

    public String createToken(int userId) {
        String token = PasswordUtil.generateToken();
        String sql = "INSERT INTO auth_tokens (token_id, user_id, token, expires_at) " +
                "VALUES (token_seq.NEXTVAL, ?, ?, SYSTIMESTAMP + INTERVAL '24' HOUR)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setString(2, token);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                return token;
            }

            return null;

        } catch (SQLException e) {
            System.out.println("Error creating token: " + e.getMessage());
            return null;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public int validateToken(String token) {
        String sql = "SELECT user_id FROM auth_tokens " +
                "WHERE token = ? AND expires_at > SYSTIMESTAMP";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, token);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

            return -1;

        } catch (SQLException e) {
            System.out.println("Error validating token: " + e.getMessage());
            return -1;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public boolean deleteToken(String token) {
        String sql = "DELETE FROM auth_tokens WHERE token = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, token);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting token: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public void deleteExpiredTokens() {
        String sql = "DELETE FROM auth_tokens WHERE expires_at < SYSTIMESTAMP";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.out.println("Error deleting expired tokens: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, null);
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