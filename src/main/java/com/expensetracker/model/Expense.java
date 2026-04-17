package com.expensetracker.model;

import java.time.LocalDate;

public class Expense {
    private int id;
    private int sessionId;
    private String title;
    private double amount;
    private String category;
    private LocalDate date;

    public Expense() {}

    public Expense(int id, int sessionId, String title, double amount, String category, LocalDate date) {
        this.id = id;
        this.sessionId = sessionId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
