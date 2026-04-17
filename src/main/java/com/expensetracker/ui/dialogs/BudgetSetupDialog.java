package com.expensetracker.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class BudgetSetupDialog extends JDialog {
    private double budgetAmount = -1;
    private JTextField amountField;

    public BudgetSetupDialog(JFrame parent) {
        super(parent, "Welcome – Set Your Budget", true);
        setSize(430, 230);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel header = new JLabel("<html><b>Welcome to Expense Tracker!</b><br>"
                + "Please enter your total budget to begin a new session.</html>");
        header.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel form = new JPanel(new GridLayout(1, 2, 10, 0));
        JLabel lbl = new JLabel("Total Budget (Rs.):");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amountField = new JTextField();
        amountField.setName("budgetAmountField");
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(lbl);
        form.add(amountField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton btn = new JButton("Start Tracking");
        btn.setName("confirmBudgetBtn");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.addActionListener(e -> confirm());
        amountField.addActionListener(e -> confirm());
        btnPanel.add(btn);

        panel.add(header, BorderLayout.NORTH);
        panel.add(form,   BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private void confirm() {
        try {
            double v = Double.parseDouble(amountField.getText().trim());
            if (v <= 0) { warn("Budget must be greater than zero."); return; }
            budgetAmount = v;
            dispose();
        } catch (NumberFormatException ex) {
            warn("Please enter a valid numeric amount.");
        }
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    public double getBudgetAmount() { return budgetAmount; }
}
