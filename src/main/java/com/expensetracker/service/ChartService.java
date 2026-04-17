package com.expensetracker.service;

import com.expensetracker.model.Expense;
import org.jfree.data.general.DefaultPieDataset;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChartService {

    public DefaultPieDataset<String> getCategoryDataset(List<Expense> expenses) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Double> totals = new TreeMap<>();
        for (Expense e : expenses) {
            totals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        totals.forEach(dataset::setValue);
        return dataset;
    }
}
