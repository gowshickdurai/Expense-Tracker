package comz.expensetracker.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static Connection connection;
    private static final String DB_PATH;

    static {
        String userHome = System.getProperty("user.home");
        File dbDir = new File(userHome, "ExpenseTracker");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        DB_PATH = new File(dbDir, "expensetracker.db").getAbsolutePath();
    }

    public static void initialize() throws SQLException {
        getConnection();
        createTables();
        LOGGER.info("Database initialized at: " + DB_PATH);
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            // Enable WAL mode for better performance
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA journal_mode=WAL");
                st.execute("PRAGMA foreign_keys=ON");
            }
        }
        return connection;
    }

    private static void createTables() throws SQLException {
        String sessions = """
            CREATE TABLE IF NOT EXISTS budget_sessions (
                id           INTEGER PRIMARY KEY AUTOINCREMENT,
                total_budget REAL    NOT NULL,
                start_date   TEXT    NOT NULL,
                end_date     TEXT,
                status       TEXT    NOT NULL DEFAULT 'ACTIVE'
            )
            """;
        String expenses = """
            CREATE TABLE IF NOT EXISTS expenses (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                session_id  INTEGER NOT NULL,
                title       TEXT    NOT NULL,
                amount      REAL    NOT NULL,
                category    TEXT    NOT NULL,
                date        TEXT    NOT NULL,
                FOREIGN KEY (session_id) REFERENCES budget_sessions(id)
            )
            """;
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sessions);
            stmt.execute(expenses);
        }
    }

    public static void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing DB connection", e);
            }
        }
    }
}
