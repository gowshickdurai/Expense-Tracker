package com.expensetracker.service;

import com.expensetracker.db.ExpenseDAO;
import com.expensetracker.model.Expense;

import java.util.List;

public class ExpenseService {
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    public boolean addExpense(Expense expense) {
        return expenseDAO.addExpense(expense);
    }

    public boolean editExpense(Expense expense) {
        return expenseDAO.updateExpense(expense);
    }

    public boolean deleteExpense(int expenseId) {
        return expenseDAO.deleteExpense(expenseId);
    }

    public List<Expense> getExpensesForSession(int sessionId) {
        return expenseDAO.getExpensesBySession(sessionId);
    }

    public double getTotalExpenses(int sessionId) {
        return expenseDAO.getTotalExpensesBySession(sessionId);
    }

    public double getRemainingBalance(double budget, int sessionId) {
        return budget - getTotalExpenses(sessionId);
    }
}
