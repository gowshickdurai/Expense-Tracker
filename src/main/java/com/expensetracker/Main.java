package com.expensetracker;

import com.expensetracker.db.DBConnection;
import com.expensetracker.ui.MainFrame;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Apply FlatLaf Dark theme before any UI is created
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not apply FlatLaf theme", e);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                DBConnection.initialize();
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to start Expense Tracker", e);
                JOptionPane.showMessageDialog(null,
                        "Application failed to start:\n" + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
