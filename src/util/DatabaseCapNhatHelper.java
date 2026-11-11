package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Lớp tiện ích tự động kiểm tra và nâng cấp cấu trúc cơ sở dữ liệu (Auto Database Migration).
 * Tự động thêm các cột vai_tro, trang_thai cho users, tạo bảng vi_tien và cột vi_id cho transactions
 * giúp ứng dụng luôn chạy mượt mà ngay cả khi chưa chạy file SQL thủ công.
 */
public class DatabaseCapNhatHelper {

    /**
     * Tự động kiểm tra và nâng cấp CSDL khi ứng dụng khởi chạy.
     */
    public static void kiemTraVaNangCapDatabase() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            // 1. Kiểm tra và bổ sung cột vai_tro, trang_thai vào bảng users nếu chưa có
            themCotNeuChuaTonTai(con, "users", "vai_tro", "VARCHAR(20) NOT NULL DEFAULT 'USER'");
            themCotNeuChuaTonTai(con, "users", "trang_thai", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'");

            // 2. Tạo tài khoản Admin mặc định nếu chưa có
            khoiTaoTaiKhoanAdminMacDinh(con);

            // 3. Tạo bảng quản lý Đa ví (vi_tien) nếu chưa có
            String sqlTaoBangViTien = "CREATE TABLE IF NOT EXISTS vi_tien (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "ten_vi VARCHAR(100) NOT NULL, " +
                    "loai_vi VARCHAR(50) NOT NULL DEFAULT 'TIEN_MAT', " +
                    "so_du DECIMAL(15,2) NOT NULL DEFAULT 0, " +
                    "ghi_chu VARCHAR(255), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            st.execute(sqlTaoBangViTien);

            // 4. Bổ sung cột vi_id vào bảng transactions nếu chưa có
            themCotNeuChuaTonTai(con, "transactions", "vi_id", "INT NULL");

            System.out.println("✅ Tự động kiểm tra và đồng bộ cấu trúc CSDL thành công!");
        } catch (SQLException e) {
            System.err.println("⚠️ Lưu ý khi đồng bộ cấu trúc CSDL: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra xem một cột đã tồn tại trong bảng hay chưa, nếu chưa thì thêm mới.
     */
    private static void themCotNeuChuaTonTai(Connection con, String tenBang, String tenCot, String kieuDuLieu) {
        String sqlKiemTra = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement pstKiemTra = con.prepareStatement(sqlKiemTra)) {
            pstKiemTra.setString(1, tenBang);
            pstKiemTra.setString(2, tenCot);
            try (ResultSet rs = pstKiemTra.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Cột chưa tồn tại -> Thêm cột
                    String sqlThemCot = "ALTER TABLE " + tenBang + " ADD COLUMN " + tenCot + " " + kieuDuLieu;
                    try (Statement stThem = con.createStatement()) {
                        stThem.execute(sqlThemCot);
                        System.out.println("-> Đã bổ sung cột `" + tenCot + "` vào bảng `" + tenBang + "`.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra cột " + tenCot + " trong bảng " + tenBang + ": " + e.getMessage());
        }
    }

    /**
     * Khởi tạo tài khoản Quản trị viên (Admin) mặc định nếu chưa có.
     * Tài khoản: admin | Mật khẩu: admin123
     */
    private static void khoiTaoTaiKhoanAdminMacDinh(Connection con) {
        String sqlCheckAdmin = "SELECT id, vai_tro, trang_thai FROM users WHERE username = 'admin'";
        try (PreparedStatement pst = con.prepareStatement(sqlCheckAdmin);
             ResultSet rs = pst.executeQuery()) {
            if (!rs.next()) {
                // Tạo mới tài khoản admin
                String sqlInsertAdmin = "INSERT INTO users (username, password, email, vai_tro, trang_thai) " +
                        "VALUES ('admin', 'admin123', 'admin@expensetracker.vn', 'ADMIN', 'ACTIVE')";
                try (Statement st = con.createStatement()) {
                    st.execute(sqlInsertAdmin);
                    System.out.println("-> Đã khởi tạo tài khoản Admin mặc định: admin / admin123");
                }
            } else {
                // Đảm bảo vai_tro là ADMIN
                String sqlUpdateAdmin = "UPDATE users SET vai_tro = 'ADMIN', trang_thai = 'ACTIVE' WHERE username = 'admin'";
                try (Statement st = con.createStatement()) {
                    st.execute(sqlUpdateAdmin);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo tài khoản Admin mặc định: " + e.getMessage());
        }
    }
}

