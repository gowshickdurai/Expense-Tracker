package com.expensetracker.ui.dialogs;

import com.expensetracker.model.BudgetSession;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryDialog extends JDialog {
    private final List<BudgetSession> sessions;
    private final ExpenseService expenseService;

    private JTable sessionTable;
    private JTable expenseTable;
    private DefaultTableModel expenseModel;

    public HistoryDialog(JFrame parent, List<BudgetSession> sessions, ExpenseService expenseService) {
        super(parent, "📜  Session History (Read-Only)", true);
        this.sessions       = sessions;
        this.expenseService = expenseService;
        setSize(900, 580);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 8));
        setBounds(getX(), getY(), getWidth(), getHeight());

        // ── Top: session list ─────────────────────────────────────────────
        String[] sCols = {"Session ID", "Budget (Rs.)", "Total Expense (Rs.)", "Balance (Rs.)", "Start Date", "End Date", "Status"};
        DefaultTableModel sessionModel = new DefaultTableModel(sCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (BudgetSession s : sessions) {
            double exp = expenseService.getTotalExpenses(s.getId());
            sessionModel.addRow(new Object[]{
                s.getId(),
                fmt(s.getTotalBudget()),
                fmt(exp),
                fmt(s.getTotalBudget() - exp),
                s.getStartDate(),
                s.getEndDate() != null ? s.getEndDate() : "-",
                s.getStatus()
            });
        }

        sessionTable = new JTable(sessionModel);
        sessionTable.setRowHeight(28);
        sessionTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sessionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane topScroll = new JScrollPane(sessionTable);
        topScroll.setBorder(BorderFactory.createTitledBorder("  Sessions  "));
        topScroll.setPreferredSize(new Dimension(880, 180));

        // ── Bottom: expenses of selected session ──────────────────────────
        String[] eCols = {"#", "Title", "Amount (Rs.)", "Category", "Date"};
        expenseModel = new DefaultTableModel(eCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        expenseTable = new JTable(expenseModel);
        expenseTable.setRowHeight(28);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        expenseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane botScroll = new JScrollPane(expenseTable);
        botScroll.setBorder(BorderFactory.createTitledBorder("  Expenses for Selected Session  "));

        // Listen for session selection
        sessionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadExpenses();
        });

        // Select first row
        if (sessions.size() > 0) {
            sessionTable.setRowSelectionInterval(0, 0);
            loadExpenses();
        }

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScroll, botScroll);
        split.setDividerLocation(210);
        split.setResizeWeight(0.35);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        btnPanel.add(close);

        add(split, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadExpenses() {
        int row = sessionTable.getSelectedRow();
        expenseModel.setRowCount(0);
        if (row < 0 || row >= sessions.size()) return;
        BudgetSession s = sessions.get(row);
        List<Expense> exps = expenseService.getExpensesForSession(s.getId());
        for (int i = 0; i < exps.size(); i++) {
            Expense e = exps.get(i);
            expenseModel.addRow(new Object[]{
                i + 1, e.getTitle(), fmt(e.getAmount()), e.getCategory(), e.getDate()
            });
        }
    }

    private String fmt(double v) { return String.format("Rs. %,.2f", v); }
}
