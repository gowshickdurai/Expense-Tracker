package com.expensetracker.db;

import com.expensetracker.model.BudgetSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BudgetDAO {
    private static final Logger LOGGER = Logger.getLogger(BudgetDAO.class.getName());

    public BudgetSession getActiveSession() {
        String sql = "SELECT * FROM budget_sessions WHERE status = 'ACTIVE' LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching active session", e);
        }
        return null;
    }

    public BudgetSession createSession(double totalBudget, LocalDate startDate) {
        String sql = "INSERT INTO budget_sessions (total_budget, start_date, status) VALUES (?, ?, 'ACTIVE')";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, totalBudget);
            ps.setString(2, startDate.toString());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return new BudgetSession(keys.getInt(1), totalBudget, startDate, null, "ACTIVE");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating session", e);
        }
        return null;
    }

    public void closeSession(int sessionId, LocalDate endDate) {
        String sql = "UPDATE budget_sessions SET status = 'CLOSED', end_date = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, endDate.toString());
            ps.setInt(2, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing session", e);
        }
    }

    public List<BudgetSession> getAllSessions() {
        List<BudgetSession> list = new ArrayList<>();
        String sql = "SELECT * FROM budget_sessions ORDER BY id DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all sessions", e);
        }
        return list;
    }

    private BudgetSession map(ResultSet rs) throws SQLException {
        BudgetSession s = new BudgetSession();
        s.setId(rs.getInt("id"));
        s.setTotalBudget(rs.getDouble("total_budget"));
        s.setStartDate(LocalDate.parse(rs.getString("start_date")));
        String end = rs.getString("end_date");
        if (end != null) s.setEndDate(LocalDate.parse(end));
        s.setStatus(rs.getString("status"));
        return s;
    }
}
