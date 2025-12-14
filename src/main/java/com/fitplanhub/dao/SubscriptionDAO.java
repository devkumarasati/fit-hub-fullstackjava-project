package com.fitplanhub.dao;

import com.fitplanhub.model.FitnessPlan;
import com.fitplanhub.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {

    public boolean subscribeToPlan(int userId, int planId) {
        String sql = "INSERT INTO subscriptions (subscription_id, user_id, plan_id, status) " +
                "VALUES (subscription_seq.NEXTVAL, ?, ?, 'ACTIVE')";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setInt(2, planId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {

            System.out.println("Error subscribing to plan: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public boolean isUserSubscribed(int userId, int planId) {
        String sql = "SELECT COUNT(*) FROM subscriptions " +
                "WHERE user_id = ? AND plan_id = ? AND status = 'ACTIVE'";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setInt(2, planId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error checking subscription: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public List<FitnessPlan> getUserSubscribedPlans(int userId) {
        String sql = "SELECT p.*, u.full_name as trainer_name " +
                "FROM fitness_plans p " +
                "JOIN subscriptions s ON p.plan_id = s.plan_id " +
                "JOIN users u ON p.trainer_id = u.user_id " +
                "WHERE s.user_id = ? AND s.status = 'ACTIVE' " +
                "ORDER BY s.purchase_date DESC";

        List<FitnessPlan> plans = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                FitnessPlan plan = new FitnessPlan();
                plan.setPlanId(rs.getInt("plan_id"));
                plan.setTrainerId(rs.getInt("trainer_id"));
                plan.setTitle(rs.getString("title"));
                plan.setDescription(rs.getString("description"));
                plan.setPrice(rs.getDouble("price"));
                plan.setDurationDays(rs.getInt("duration_days"));
                plan.setCreatedAt(rs.getTimestamp("created_at"));
                plan.setTrainerName(rs.getString("trainer_name"));
                plans.add(plan);
            }

            return plans;

        } catch (SQLException e) {
            System.out.println("Error getting subscribed plans: " + e.getMessage());
            return plans;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public boolean cancelSubscription(int userId, int planId) {
        String sql = "UPDATE subscriptions SET status = 'CANCELLED' " +
                "WHERE user_id = ? AND plan_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setInt(2, planId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error cancelling subscription: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public int getSubscriberCount(int planId) {
        String sql = "SELECT COUNT(*) FROM subscriptions " +
                "WHERE plan_id = ? AND status = 'ACTIVE'";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, planId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            System.out.println("Error getting subscriber count: " + e.getMessage());
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