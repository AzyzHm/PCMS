package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import utils.passwordHasher;

import exceptions.DatabaseException;
import exceptions.SecurityException;

public class databaseConnection {
    private static final String DB_NAME = "databse/powerhouse.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;


    public static Connection getConnection() throws DatabaseException {
        try {
            Connection conn = DriverManager.getConnection(URL);
            try(Statement stmt = conn.createStatement()) {
                String sql = "PRAGMA foreign_keys = ON;"; // Enable foreign key constraints
                stmt.execute(sql);
            }
            return conn;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to database", e);
        }
    }

    public static void initializeDatabase() throws DatabaseException, SecurityException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "role TEXT NOT NULL," +
                "login TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "nom TEXT," +
                "prenom TEXT," +
                "date_naissance TEXT," +
                "adresse TEXT," +
                "telephone TEXT," +
                "email TEXT," +
                "poids REAL," +
                "first_login INTEGER DEFAULT 1" +
                ");";

        String createActivitiesTable = "CREATE TABLE IF NOT EXISTS activities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT NOT NULL," +
                "description TEXT," +
                "capacite_max INTEGER NOT NULL," +
                "horaires TEXT" +
                ");";

        // status: EN_ATTENTE | ACCEPTEE | REFUSEE
        String createEnrollmentsTable = "CREATE TABLE IF NOT EXISTS enrollments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "activity_id INTEGER NOT NULL," +
                "status TEXT DEFAULT 'EN_ATTENTE'," +
                "UNIQUE(user_id, activity_id)," +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY(activity_id) REFERENCES activities(id) ON DELETE CASCADE" +
                ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createActivitiesTable);
            stmt.execute(createEnrollmentsTable);

            String checkAdmin = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
            var rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String hashedAdminPass = passwordHasher.hashPassword("AzyzHm0110");
                String insertAdmin = "INSERT INTO users (role, login, password, nom, first_login) VALUES ('ADMIN', 'AzyzHm', '" + hashedAdminPass + "', 'Azyz', 0)";
                stmt.execute(insertAdmin);
            }
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database tables", e);
        }
    } 
}
