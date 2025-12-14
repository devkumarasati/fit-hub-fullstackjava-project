package com.fitplanhub.dao;

import com.fitplanhub.model.User;
import com.fitplanhub.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    public boolean followTrainer(int userId, int trainerId) {
        String sql = "INSERT INTO follows (follow_id, user_id, trainer_id) " +
                "VALUES (follow_seq.NEXTVAL, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setInt(2, trainerId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {

            System.out.println("Error following trainer: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public boolean unfollowTrainer(int userId, int trainerId) {
        String sql = "DELETE FROM follows WHERE user_id = ? AND trainer_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setInt(2, trainerId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error unfollowing trainer: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public boolean isFollowing(int userId, int trainerId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE user_id = ? AND trainer_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setInt(2, trainerId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error checking follow status: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public List<User> getFollowedTrainers(int userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN follows f ON u.user_id = f.trainer_id " +
                "WHERE f.user_id = ? " +
                "ORDER BY f.followed_at DESC";

        List<User> trainers = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            rs = stmt.executeQuery();

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
            System.out.println("Error getting followed trainers: " + e.getMessage());
            return trainers;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public int getFollowerCount(int trainerId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE trainer_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trainerId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            System.out.println("Error getting follower count: " + e.getMessage());
            return 0;
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