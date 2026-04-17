package com.expensetracker.model;

import java.time.LocalDate;

public class BudgetSession {
    private int id;
    private double totalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE | CLOSED

    public BudgetSession() {}

    public BudgetSession(int id, double totalBudget, LocalDate startDate, LocalDate endDate, String status) {
        this.id = id;
        this.totalBudget = totalBudget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Session #" + id + " [" + status + "] Rs." + totalBudget;
    }
}
