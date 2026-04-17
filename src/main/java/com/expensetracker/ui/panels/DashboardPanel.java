package com.expensetracker.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final JLabel budgetVal;
    private final JLabel spentVal;
    private final JLabel balanceVal;

    private static final Color COLOR_BUDGET  = new Color(30, 144, 255);
    private static final Color COLOR_SPENT   = new Color(220, 60, 60);
    private static final Color COLOR_BALANCE = new Color(34, 160, 80);
    private static final Color COLOR_NEG     = new Color(220, 60, 60);

    public DashboardPanel() {
        setLayout(new GridLayout(1, 3, 12, 0));
        setBorder(new EmptyBorder(10, 15, 10, 15));

        budgetVal  = valueLabel();
        spentVal   = valueLabel();
        balanceVal = valueLabel();

        add(card("Total Budget",    budgetVal,  COLOR_BUDGET));
        add(card("Total Expense",   spentVal,   COLOR_SPENT));
        add(card("Remaining Balance", balanceVal, COLOR_BALANCE));
    }

    private JPanel card(String title, JLabel valueLabel, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2, true),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(accent);

        p.add(titleLbl,    BorderLayout.NORTH);
        p.add(valueLabel,  BorderLayout.CENTER);
        return p;
    }

    private JLabel valueLabel() {
        JLabel l = new JLabel("Rs. 0.00", SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        return l;
    }

    public void update(double budget, double spent, double balance) {
        budgetVal.setText(fmt(budget));
        spentVal.setText(fmt(spent));
        balanceVal.setText(fmt(balance));
        // Turn balance red if overshot
        balanceVal.setForeground(balance < 0 ? COLOR_NEG : UIManager.getColor("Label.foreground"));
    }

    private String fmt(double v) { return String.format("Rs. %,.2f", v); }
}
