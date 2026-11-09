package db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Lớp chuyển tiếp (Adapter) tương thích ngược với cấu trúc cũ.
 * Sử dụng util.DBConnection làm nguồn kết nối chính.
 */
public class DBConnection {
    public static Connection getConnection() throws SQLException {
        return util.DBConnection.getConnection();
    }
}
