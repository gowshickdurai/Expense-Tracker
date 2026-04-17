package com.expensetracker.service;

import com.expensetracker.db.BudgetDAO;
import com.expensetracker.model.BudgetSession;

import java.time.LocalDate;
import java.util.List;

public class BudgetService {
    private final BudgetDAO budgetDAO = new BudgetDAO();

    public BudgetSession getActiveSession() {
        return budgetDAO.getActiveSession();
    }

    public BudgetSession setupBudget(double amount) {
        return budgetDAO.createSession(amount, LocalDate.now());
    }

    public BudgetSession resetBudget(double newAmount, int currentSessionId) {
        budgetDAO.closeSession(currentSessionId, LocalDate.now());
        return budgetDAO.createSession(newAmount, LocalDate.now());
    }

    public List<BudgetSession> getAllSessions() {
        return budgetDAO.getAllSessions();
    }
}
