package com.expensetracker.ui.dialogs;

import com.formdev.flatlaf.*;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {

    private static final String[] THEME_NAMES = {
        "Dark (Default)",
        "Light"
    };

    public SettingsDialog(JFrame parent) {
        super(parent, "⚙  Settings", true);
        setSize(380, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
        JLabel lbl = new JLabel("UI Theme:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JComboBox<String> themeCombo = new JComboBox<>(THEME_NAMES);
        themeCombo.setName("themeCombo");

        // Pre-select current theme
        String current = UIManager.getLookAndFeel().getClass().getSimpleName().toLowerCase();
        if (current.contains("dark")) {
            themeCombo.setSelectedIndex(0);
        } else {
            themeCombo.setSelectedIndex(1);
        }

        row.add(lbl);
        row.add(themeCombo);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton apply = new JButton("Apply");
        apply.setName("applyThemeBtn");
        apply.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        apply.addActionListener(e -> applyTheme(themeCombo.getSelectedIndex()));
        btnPanel.add(cancel);
        btnPanel.add(apply);

        panel.add(row,      BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private void applyTheme(int index) {
        try {
            switch (index) {
                case 0 -> UIManager.setLookAndFeel(new FlatDarkLaf());
                case 1 -> UIManager.setLookAndFeel(new FlatLightLaf());
            }
            // Update all open windows
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to apply theme: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
