package org.example.projectpms2207068;

import java.sql.*;

public class DatabaseHandler {

    private static Connection connect() {
        String url = "jdbc:sqlite:mydb.db";   // SQLite file in project folder
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void initializeDB() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + " id integer PRIMARY KEY AUTOINCREMENT,"
                + " username text NOT NULL UNIQUE,"
                + " password text NOT NULL"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean createUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // username may already exist
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
