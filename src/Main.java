import util.DBConnection;
import util.GiaoDienHelper;
import view.DangNhapFrame;

import javax.swing.*;
import java.sql.Connection;

/**
 * Lớp khởi chạy ứng dụng chính (Main Entry Point).
 * Khởi tạo giao diện FlatLaf, kiểm tra kết nối CSDL MySQL và mở màn hình Đăng nhập.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Thiết lập giao diện mặc định là FlatLaf Dark và font tiếng Việt Unicode
        GiaoDienHelper.khoiTaoGiaoDienMacDinh();

        // 2. Kiểm tra kết nối cơ sở dữ liệu MySQL
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Kết nối đến cơ sở dữ liệu MySQL thành công!");
            }
        } catch (Exception e) {
            System.err.println("❌ Kết nối cơ sở dữ liệu thất bại:");
            e.printStackTrace();
        }

        // 3. Khởi động màn hình Đăng nhập trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new DangNhapFrame().setVisible(true);
        });
    }
}
