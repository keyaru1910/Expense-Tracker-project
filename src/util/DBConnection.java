package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp tiện ích quản lý kết nối cơ sở dữ liệu MySQL (JDBC Connection).
 * Cấu hình tham số hỗ trợ đầy đủ tiếng Việt Unicode utf8mb4.
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";
    private static final String PASSWORD = "Lolmemay12345@";

    /**
     * Mở và trả về kết nối đến CSDL MySQL.
     * @return Connection đối tượng kết nối
     * @throws SQLException nếu kết nối thất bại
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

