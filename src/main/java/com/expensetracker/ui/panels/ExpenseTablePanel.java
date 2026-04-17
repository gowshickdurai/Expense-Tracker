package com.expensetracker.ui.panels;

import com.expensetracker.model.Expense;
import com.expensetracker.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ExpenseTablePanel extends JPanel {
    private final MainFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private List<Expense> expenses;

    private static final String[] COLS = {"S.No.", "Date", "Category", "Title", "Amount (Rs.)", "    ", "   "};

    public ExpenseTablePanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        initTable();
    }

    private void initTable() {
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5 || c == 6; }
            @Override public Class<?> getColumnClass(int c)       { return String.class; }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        // Column widths
        int[] widths = {40, 100, 120, 220, 120, 70, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Right-align values
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int c = 0; c < 5; c++) table.getColumnModel().getColumn(c).setCellRenderer(right);

        // Button columns
        table.getColumnModel().getColumn(5).setCellRenderer(new BtnRenderer("Edit"));
        table.getColumnModel().getColumn(5).setCellEditor(
                new BtnEditor("Edit", () -> doEdit()));
        table.getColumnModel().getColumn(6).setCellRenderer(new BtnRenderer("Delete"));
        table.getColumnModel().getColumn(6).setCellEditor(
                new BtnEditor("Delete", () -> doDelete()));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("  Current Session Expenses  "));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadExpenses(List<Expense> list) {
        this.expenses = list;
        model.setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            Expense e = list.get(i);
            model.addRow(new Object[]{
                i + 1,
                e.getDate().toString(),
                e.getCategory(),
                e.getTitle(),
                String.format("Rs. %,.2f", e.getAmount()),
                "Edit",
                "Delete"
            });
        }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row >= 0 && expenses != null && row < expenses.size())
            frame.openAddExpenseDialog(expenses.get(row));
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0 || expenses == null || row >= expenses.size()) return;
        Expense e = expenses.get(row);
        int ans = JOptionPane.showConfirmDialog(frame,
                "Delete \"" + e.getTitle() + "\"?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {
            frame.getExpenseService().deleteExpense(e.getId());
            frame.refreshUI();
        }
    }

    // ── Inner: button renderer ──────────────────────────────────────────────
    static class BtnRenderer extends JButton implements TableCellRenderer {
        BtnRenderer(String label) { setText(label); setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            setText(v != null ? v.toString() : "");
            return this;
        }
    }

    // ── Inner: button editor ────────────────────────────────────────────────
    static class BtnEditor extends DefaultCellEditor {
        private final JButton btn;
        private final Runnable action;

        BtnEditor(String label, Runnable action) {
            super(new JCheckBox());
            this.action = action;
            btn = new JButton(label);
            btn.addActionListener(e -> { fireEditingStopped(); action.run(); });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean sel, int row, int col) {
            t.setRowSelectionInterval(row, row);
            btn.setText(v != null ? v.toString() : "");
            return btn;
        }

        @Override public Object getCellEditorValue() { return btn.getText(); }
    }
}
