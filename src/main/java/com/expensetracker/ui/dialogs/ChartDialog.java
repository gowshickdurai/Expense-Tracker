package com.expensetracker.ui.dialogs;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ChartService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartDialog extends JDialog {
    private final List<Expense> expenses;

    public ChartDialog(JFrame parent, List<Expense> expenses) {
        super(parent, "📊  Expense Chart – Category Breakdown", true);
        this.expenses = expenses;
        setSize(680, 530);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        ChartService chartService = new ChartService();
        DefaultPieDataset<String> dataset = chartService.getCategoryDataset(expenses);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expenses by Category",
                dataset,
                true,   // legend
                true,   // tooltips
                false   // URLs
        );

        // Style the chart
        chart.setBackgroundPaint(UIManager.getColor("Panel.background"));
        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setBackgroundPaint(UIManager.getColor("Panel.background"));
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setShadowPaint(null);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(660, 460));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        btnPanel.add(close);

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        add(btnPanel,   BorderLayout.SOUTH);
    }
}
