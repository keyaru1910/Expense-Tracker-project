package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    private static final String USER = "root"; // change if needed
    private static final String PASSWORD = "Lolmemay12345@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
