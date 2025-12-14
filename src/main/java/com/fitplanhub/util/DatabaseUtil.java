package com.fitplanhub.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orclpdb1";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "satya";

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established");
            return conn;
        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            throw e;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.out.println("Error closing connection");
                e.printStackTrace();
            }
        }
    }
}
