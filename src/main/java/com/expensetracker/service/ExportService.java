package com.expensetracker.service;

import com.expensetracker.db.ExpenseDAO;
import com.expensetracker.model.BudgetSession;
import com.expensetracker.model.Expense;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportService {
    private static final Logger LOGGER = Logger.getLogger(ExportService.class.getName());
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    public boolean exportToExcel(String filePath,
            BudgetSession activeSession,
            List<Expense> currentExpenses,
            List<BudgetSession> allSessions) {
        try (Workbook wb = new XSSFWorkbook()) {
            buildCurrentSheet(wb, activeSession, currentExpenses);
            buildHistorySheet(wb, allSessions);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Excel export failed", e);
            return false;
        }
    }

    // ── Sheet 1: Current Session ────────────────────────────────────────────
    private void buildCurrentSheet(Workbook wb, BudgetSession session, List<Expense> expenses) {
        Sheet sheet = wb.createSheet("Current Session");
        CellStyle hdr = headerStyle(wb);
        CellStyle amt = amountStyle(wb);

        CellStyle titleStyle = wb.createCellStyle();
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        // Session info (Header)
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Current Session-Budget: ₹" + session.getTotalBudget());
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // Table header
        Row th = sheet.createRow(1);
        String[] cols = { "S.No.", "Title", "Category", "Amount(₹)", "Date" };
        for (int i = 0; i < cols.length; i++)
            cell(th, i, cols[i], hdr);

        // Data
        double total = 0;
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            Row r = sheet.createRow(2 + i);
            cell(r, 0, String.valueOf(i + 1));
            cell(r, 1, e.getTitle());
            cell(r, 2, e.getCategory());
            amtCell(r, 3, e.getAmount(), amt);
            cell(r, 4, e.getDate().toString());
            total += e.getAmount();
        }

        // Totals
        int tr = 2 + expenses.size();

        CellStyle centerSummaryStyle = wb.createCellStyle();
        Font summaryFont = wb.createFont();
        summaryFont.setBold(true);
        centerSummaryStyle.setFont(summaryFont);
        centerSummaryStyle.setAlignment(HorizontalAlignment.CENTER);

        // Total Expenses
        Row totRow = sheet.createRow(tr);
        Cell totCell = totRow.createCell(0);
        totCell.setCellValue(String.format("Total Expenses: %,.2f", total));
        totCell.setCellStyle(centerSummaryStyle);
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 4));

        // Balance
        Row remRow = sheet.createRow(tr + 1);
        Cell remCell = remRow.createCell(0);
        remCell.setCellValue(String.format("Balance: %,.2f", session.getTotalBudget() - total));
        remCell.setCellStyle(centerSummaryStyle);
        sheet.addMergedRegion(new CellRangeAddress(tr + 1, tr + 1, 0, 4));

        for (int i = 0; i < 5; i++)
            sheet.autoSizeColumn(i);
    }

    // ── Sheet 2: History ────────────────────────────────────────────────────
    private void buildHistorySheet(Workbook wb, List<BudgetSession> sessions) {
        Sheet sheet = wb.createSheet("History");
        CellStyle hdr = headerStyle(wb);
        CellStyle amt = amountStyle(wb);

        Row th = sheet.createRow(0);
        String[] cols = { "Session ID", "Budget (Rs.)", "Total Expense (Rs.)", "Balance (Rs.)", "Start Date",
                "End Date", "Status" };
        for (int i = 0; i < cols.length; i++)
            cell(th, i, cols[i], hdr);

        for (int i = 0; i < sessions.size(); i++) {
            BudgetSession s = sessions.get(i);
            double exp = expenseDAO.getTotalExpensesBySession(s.getId());
            Row r = sheet.createRow(i + 1);
            cell(r, 0, String.valueOf(s.getId()));
            amtCell(r, 1, s.getTotalBudget(), amt);
            amtCell(r, 2, exp, amt);
            amtCell(r, 3, s.getTotalBudget() - exp, amt);
            cell(r, 4, s.getStartDate().toString());
            cell(r, 5, s.getEndDate() != null ? s.getEndDate().toString() : "-");
            cell(r, 6, s.getStatus());
        }
        for (int i = 0; i < 7; i++)
            sheet.autoSizeColumn(i);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private void cell(Row r, int col, String val) {
        r.createCell(col).setCellValue(val);
    }

    private void cell(Row r, int col, String val, CellStyle style) {
        Cell c = r.createCell(col);
        c.setCellValue(val);
        if (style != null)
            c.setCellStyle(style);
    }

    private void amtCell(Row r, int col, double val, CellStyle style) {
        Cell c = r.createCell(col);
        c.setCellValue(val);
        c.setCellStyle(style);
    }

    private CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        return s;
    }

    private CellStyle amountStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        s.setDataFormat(df.getFormat("#,##0.00"));
        return s;
    }
}
