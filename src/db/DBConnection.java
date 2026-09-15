package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static String requiredEnv(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("Missing environment variable: " + name);
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            requiredEnv("SMART_PARKING_DB_URL"),
            requiredEnv("SMART_PARKING_DB_USER"),
            requiredEnv("SMART_PARKING_DB_PASSWORD")
        );
    }
}
