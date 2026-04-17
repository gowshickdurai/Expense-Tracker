package com.expensetracker.ui.dialogs;

import com.expensetracker.model.Expense;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddExpenseDialog extends JDialog {
    private boolean confirmed = false;
    private Expense expense;
    private final int sessionId;

    private JTextField titleField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JTextField dateField;

    public static final String[] CATEGORIES = {
        "Food", "Transport", "Utilities", "Bills", "Health",
        "Shopping", "Entertainment", "Education", "Rent", "Other"
    };

    public AddExpenseDialog(JFrame parent, int sessionId, Expense existing) {
        super(parent, existing == null ? "➕  Add New Expense" : "✏  Edit Expense", true);
        this.sessionId = sessionId;
        this.expense   = existing;
        setSize(460, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));

        form.add(bold("Title:")); 
        titleField = new JTextField(); titleField.setName("expenseTitleField"); 
        form.add(titleField);

        form.add(bold("Amount (Rs.):"));
        amountField = new JTextField(); amountField.setName("expenseAmountField");
        form.add(amountField);

        form.add(bold("Category:"));
        categoryCombo = new JComboBox<>(CATEGORIES); categoryCombo.setName("expenseCategoryCombo");
        form.add(categoryCombo);

        form.add(bold("Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString()); dateField.setName("expenseDateField");
        form.add(dateField);

        // Pre-fill when editing
        if (expense != null) {
            titleField.setText(expense.getTitle());
            amountField.setText(String.valueOf(expense.getAmount()));
            categoryCombo.setSelectedItem(expense.getCategory());
            dateField.setText(expense.getDate().toString());
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton cancel = new JButton("Cancel"); cancel.setName("cancelExpenseBtn");
        JButton save   = new JButton(expense == null ? "Add Expense" : "Save Changes");
        save.setName("saveExpenseBtn");
        save.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> save());
        btns.add(cancel); btns.add(save);

        root.add(form, BorderLayout.CENTER);
        root.add(btns, BorderLayout.SOUTH);
        add(root);
    }

    private void save() {
        String title  = titleField.getText().trim();
        String amtStr = amountField.getText().trim();
        String cat    = (String) categoryCombo.getSelectedItem();
        String dtStr  = dateField.getText().trim();

        if (title.isEmpty())        { err("Title cannot be empty.");             return; }
        if (title.length() > 100)   { err("Title max 100 characters.");          return; }
        double amt;
        try {
            amt = Double.parseDouble(amtStr);
            if (amt <= 0) { err("Amount must be greater than zero."); return; }
        } catch (NumberFormatException ex) { err("Enter a valid amount."); return; }
        LocalDate dt;
        try { dt = LocalDate.parse(dtStr); }
        catch (Exception ex) { err("Date must be in YYYY-MM-DD format."); return; }

        if (expense == null) { expense = new Expense(); expense.setSessionId(sessionId); }
        expense.setTitle(title);
        expense.setAmount(amt);
        expense.setCategory(cat);
        expense.setDate(dt);
        confirmed = true;
        dispose();
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    public boolean isConfirmed() { return confirmed; }
    public Expense getExpense()  { return expense; }
}
