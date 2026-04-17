package com.expensetracker.ui;

import com.expensetracker.db.DBConnection;
import com.expensetracker.model.BudgetSession;
import com.expensetracker.model.Expense;
import com.expensetracker.service.*;
import com.expensetracker.ui.dialogs.*;
import com.expensetracker.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    private BudgetSession activeSession;
    private final BudgetService  budgetService  = new BudgetService();
    private final ExpenseService expenseService = new ExpenseService();

    private DashboardPanel    dashboardPanel;
    private ExpenseTablePanel tablePanel;

    public MainFrame() {
        setTitle("💰 Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 600));
        setSize(1100, 720);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { DBConnection.close(); }
        });

        // Ensure active session exists
        activeSession = budgetService.getActiveSession();
        if (activeSession == null) {
            BudgetSetupDialog dlg = new BudgetSetupDialog(this);
            dlg.setVisible(true);
            if (dlg.getBudgetAmount() <= 0) System.exit(0);
            activeSession = budgetService.setupBudget(dlg.getBudgetAmount());
        }

        buildUI();
        refreshUI();
    }

    // ── UI Construction ─────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),   BorderLayout.NORTH);
        tablePanel = new ExpenseTablePanel(this);
        add(tablePanel,      BorderLayout.CENTER);
        add(buildSouthArea(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(10, 18, 8, 18));

        JLabel title = new JLabel("Expense Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        bar.add(title, BorderLayout.WEST);

        JButton settings = new JButton("Settings", IconUtil.createSettingsIcon());
        settings.setName("settingsBtn");
        settings.addActionListener(e -> openSettings());
        bar.add(settings, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSouthArea() {
        dashboardPanel = new DashboardPanel();
        JPanel south = new JPanel(new BorderLayout());
        south.add(dashboardPanel,  BorderLayout.NORTH);
        south.add(buildButtonBar(), BorderLayout.SOUTH);
        return south;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        bar.setBorder(BorderFactory.createEmptyBorder(2, 10, 8, 10));

        bar.add(btn("Add Expense", IconUtil.createAddIcon(), "addExpenseBtn", e -> openAddExpenseDialog(null)));
        bar.add(btn("Reset Budget", IconUtil.createResetIcon(), "resetBudgetBtn", e -> resetBudget()));
        bar.add(btn("View Charts", IconUtil.createChartIcon(), "viewChartsBtn", e -> openCharts()));
        bar.add(btn("View History", IconUtil.createHistoryIcon(), "viewHistoryBtn", e -> openHistory()));
        bar.add(btn("Export Excel", IconUtil.createExportIcon(), "exportExcelBtn", e -> exportExcel()));
        return bar;
    }

    private JButton btn(String label, Icon icon, String name, java.awt.event.ActionListener al) {
        JButton b = new JButton(label, icon);
        b.setName(name);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(160, 40));
        b.setFocusPainted(false);
        b.setIconTextGap(8);
        b.addActionListener(al);
        return b;
    }

    // ── Actions ─────────────────────────────────────────────────────────────

    public void openAddExpenseDialog(Expense existing) {
        AddExpenseDialog dlg = new AddExpenseDialog(this, activeSession.getId(), existing);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            if (existing == null) expenseService.addExpense(dlg.getExpense());
            else                  expenseService.editExpense(dlg.getExpense());
            refreshUI();
        }
    }

    private void resetBudget() {
        String input = JOptionPane.showInputDialog(this,
                "Enter new budget amount (Rs.):", "Reset / Update Budget",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;

        double newBudget;
        try {
            newBudget = Double.parseDouble(input.trim());
            if (newBudget <= 0) { warn("Budget must be greater than zero."); return; }
        } catch (NumberFormatException ex) { warn("Please enter a valid number."); return; }

        int ok = JOptionPane.showConfirmDialog(this,
                "This will reset current expenses and start a new session.\nContinue?",
                "Confirm Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            activeSession = budgetService.resetBudget(newBudget, activeSession.getId());
            refreshUI();
            JOptionPane.showMessageDialog(this,
                    "Budget reset! New session #" + activeSession.getId() + " started.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openCharts() {
        List<Expense> exps = expenseService.getExpensesForSession(activeSession.getId());
        if (exps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expenses to visualise.", "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new ChartDialog(this, exps).setVisible(true);
    }

    private void openHistory() {
        new HistoryDialog(this, budgetService.getAllSessions(), expenseService).setVisible(true);
    }

    private void exportExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export to Excel");
        fc.setSelectedFile(new File("ExpenseTracker_Export.xlsx"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Workbook (*.xlsx)", "xlsx"));

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".xlsx")) path += ".xlsx";

        ExportService svc = new ExportService();
        boolean ok = svc.exportToExcel(path,
                activeSession,
                expenseService.getExpensesForSession(activeSession.getId()),
                budgetService.getAllSessions());

        if (ok)
            JOptionPane.showMessageDialog(this, "Exported successfully:\n" + path,
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, "Export failed. Check logs.",
                    "Export Error", JOptionPane.ERROR_MESSAGE);
    }

    private void openSettings() {
        new SettingsDialog(this).setVisible(true);
    }

    // ── Refresh ──────────────────────────────────────────────────────────────

    public void refreshUI() {
        List<Expense> exps   = expenseService.getExpensesForSession(activeSession.getId());
        double spent          = expenseService.getTotalExpenses(activeSession.getId());
        double balance        = expenseService.getRemainingBalance(activeSession.getTotalBudget(), activeSession.getId());

        dashboardPanel.update(activeSession.getTotalBudget(), spent, balance);
        tablePanel.loadExpenses(exps);
        setTitle("💰 Expense Tracker  –  Session #" + activeSession.getId());
    }

    // ── Getters used by child panels ─────────────────────────────────────────

    public ExpenseService getExpenseService() { return expenseService; }
    public BudgetSession  getActiveSession()  { return activeSession; }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}
