package model;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp tiện ích hỗ trợ quản lý và khởi tạo danh mục mặc định cho người dùng.
 * Đảm bảo người dùng luôn có sẵn danh mục đẹp mắt khi sử dụng ứng dụng.
 */
public class DanhMucHelper {

    // Danh sách các danh mục mặc định thông dụng với mã màu trực quan
    public static final String[][] DANH_MUC_MAC_DINH = {
            {"Ăn uống", "#FF5722"},          // Cam đậm
            {"Mua sắm", "#E91E63"},          // Hồng
            {"Di chuyển", "#2196F3"},        // Xanh da trời
            {"Hóa đơn & Tiện ích", "#FF9800"},// Vàng cam
            {"Giải trí", "#9C27B0"},         // Tím
            {"Sức khỏe", "#F44336"},         // Đỏ
            {"Tiền lương", "#4CAF50"},       // Xanh lá
            {"Thưởng & Đầu tư", "#009688"},  // Xanh mòng két
            {"Khác", "#607D8B"}              // Xám ghi
    };

    /**
     * Kiểm tra và khởi tạo danh mục mặc định cho người dùng theo tên đăng nhập (username)
     * nếu người dùng hiện tại chưa có danh mục nào.
     */
    public static void khoiTaoDanhMucNeuChuaCo(String username) {
        try (Connection con = DBConnection.getConnection()) {
            int userId = -1;
            String sqlLayId = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement pstLayId = con.prepareStatement(sqlLayId)) {
                pstLayId.setString(1, username);
                try (ResultSet rs = pstLayId.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id");
                    }
                }
            }

            if (userId != -1) {
                khoiTaoDanhMucChoUserId(con, userId);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra và tạo danh mục mặc định: " + e.getMessage());
        }
    }

    /**
     * Khởi tạo danh sách danh mục mặc định cho người dùng theo userId nếu chưa có
     */
    public static void khoiTaoDanhMucChoUserId(Connection con, int userId) throws SQLException {
        String sqlKiemTra = "SELECT COUNT(*) FROM categories WHERE user_id = ?";
        try (PreparedStatement pstKiemTra = con.prepareStatement(sqlKiemTra)) {
            pstKiemTra.setInt(1, userId);
            try (ResultSet rs = pstKiemTra.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String sqlThem = "INSERT INTO categories (user_id, name, color) VALUES (?, ?, ?)";
                    try (PreparedStatement pstThem = con.prepareStatement(sqlThem)) {
                        for (String[] danhMuc : DANH_MUC_MAC_DINH) {
                            pstThem.setInt(1, userId);
                            pstThem.setString(2, danhMuc[0]);
                            pstThem.setString(3, danhMuc[1]);
                            pstThem.addBatch();
                        }
                        pstThem.executeBatch();
                    }
                }
            }
        }
    }
}

