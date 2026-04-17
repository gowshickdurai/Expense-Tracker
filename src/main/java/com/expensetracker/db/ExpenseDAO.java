package com.expensetracker.db;

import com.expensetracker.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpenseDAO {
    private static final Logger LOGGER = Logger.getLogger(ExpenseDAO.class.getName());

    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (session_id, title, amount, category, date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, expense.getSessionId());
            ps.setString(2, expense.getTitle());
            ps.setDouble(3, expense.getAmount());
            ps.setString(4, expense.getCategory());
            ps.setString(5, expense.getDate().toString());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) expense.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding expense", e);
            return false;
        }
    }

    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET title=?, amount=?, category=?, date=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, expense.getTitle());
            ps.setDouble(2, expense.getAmount());
            ps.setString(3, expense.getCategory());
            ps.setString(4, expense.getDate().toString());
            ps.setInt(5, expense.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating expense", e);
            return false;
        }
    }

    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting expense", e);
            return false;
        }
    }

    public List<Expense> getExpensesBySession(int sessionId) {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE session_id=? ORDER BY date DESC, id DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching expenses", e);
        }
        return list;
    }

    public double getTotalExpensesBySession(int sessionId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE session_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error computing total expenses", e);
        }
        return 0.0;
    }

    private Expense map(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.setId(rs.getInt("id"));
        e.setSessionId(rs.getInt("session_id"));
        e.setTitle(rs.getString("title"));
        e.setAmount(rs.getDouble("amount"));
        e.setCategory(rs.getString("category"));
        e.setDate(LocalDate.parse(rs.getString("date")));
        return e;
    }
}
