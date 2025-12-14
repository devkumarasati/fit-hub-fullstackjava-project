package com.fitplanhub.dao;

import com.fitplanhub.model.FitnessPlan;
import com.fitplanhub.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FitnessPlanDAO {

    public boolean createPlan(FitnessPlan plan) {
        String sql = "INSERT INTO fitness_plans (plan_id, trainer_id, title, description, price, duration_days) " +
                "VALUES (plan_seq.NEXTVAL, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, plan.getTrainerId());
            stmt.setString(2, plan.getTitle());
            stmt.setString(3, plan.getDescription());
            stmt.setDouble(4, plan.getPrice());
            stmt.setInt(5, plan.getDurationDays());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error creating plan: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public List<FitnessPlan> getAllPlans() {
        String sql = "SELECT p.*, u.full_name as trainer_name " +
                "FROM fitness_plans p " +
                "JOIN users u ON p.trainer_id = u.user_id " +
                "ORDER BY p.created_at DESC";

        List<FitnessPlan> plans = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                FitnessPlan plan = extractPlanFromResultSet(rs);
                plans.add(plan);
            }

            return plans;

        } catch (SQLException e) {
            System.out.println("Error getting all plans: " + e.getMessage());
            return plans;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public FitnessPlan getPlanById(int planId) {
        String sql = "SELECT p.*, u.full_name as trainer_name " +
                "FROM fitness_plans p " +
                "JOIN users u ON p.trainer_id = u.user_id " +
                "WHERE p.plan_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, planId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractPlanFromResultSet(rs);
            }

            return null;

        } catch (SQLException e) {
            System.out.println("Error getting plan by ID: " + e.getMessage());
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public List<FitnessPlan> getPlansByTrainer(int trainerId) {
        String sql = "SELECT p.*, u.full_name as trainer_name " +
                "FROM fitness_plans p " +
                "JOIN users u ON p.trainer_id = u.user_id " +
                "WHERE p.trainer_id = ? " +
                "ORDER BY p.created_at DESC";

        List<FitnessPlan> plans = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trainerId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                FitnessPlan plan = extractPlanFromResultSet(rs);
                plans.add(plan);
            }

            return plans;

        } catch (SQLException e) {
            System.out.println("Error getting trainer plans: " + e.getMessage());
            return plans;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public boolean updatePlan(FitnessPlan plan) {
        String sql = "UPDATE fitness_plans SET title = ?, description = ?, " +
                "price = ?, duration_days = ? WHERE plan_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, plan.getTitle());
            stmt.setString(2, plan.getDescription());
            stmt.setDouble(3, plan.getPrice());
            stmt.setInt(4, plan.getDurationDays());
            stmt.setInt(5, plan.getPlanId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating plan: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public boolean deletePlan(int planId) {
        String sql = "DELETE FROM fitness_plans WHERE plan_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, planId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting plan: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public List<FitnessPlan> getPlansFromFollowedTrainers(int userId) {
        String sql = "SELECT p.*, u.full_name as trainer_name " +
                "FROM fitness_plans p " +
                "JOIN users u ON p.trainer_id = u.user_id " +
                "JOIN follows f ON p.trainer_id = f.trainer_id " +
                "WHERE f.user_id = ? " +
                "ORDER BY p.created_at DESC";

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
                FitnessPlan plan = extractPlanFromResultSet(rs);
                plans.add(plan);
            }

            return plans;

        } catch (SQLException e) {
            System.out.println("Error getting followed plans: " + e.getMessage());
            return plans;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    private FitnessPlan extractPlanFromResultSet(ResultSet rs) throws SQLException {
        FitnessPlan plan = new FitnessPlan();
        plan.setPlanId(rs.getInt("plan_id"));
        plan.setTrainerId(rs.getInt("trainer_id"));
        plan.setTitle(rs.getString("title"));
        plan.setDescription(rs.getString("description"));
        plan.setPrice(rs.getDouble("price"));
        plan.setDurationDays(rs.getInt("duration_days"));
        plan.setCreatedAt(rs.getTimestamp("created_at"));
        plan.setTrainerName(rs.getString("trainer_name"));
        return plan;
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