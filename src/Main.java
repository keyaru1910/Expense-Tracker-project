import db.DBConnection;
import model.GiaoDienHelper;
import model.LoginPage;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Thiết lập giao diện mặc định là giao diện Tối và font tiếng Việt Unicode
        GiaoDienHelper.khoiTaoGiaoDienMacDinh();

        // Kiểm tra kết nối cơ sở dữ liệu MySQL
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Kết nối đến cơ sở dữ liệu MySQL thành công!");
            }
        } catch (Exception e) {
            System.out.println("❌ Kết nối cơ sở dữ liệu thất bại:");
            e.printStackTrace();
        }

        // Khởi động màn hình Đăng nhập trên EDT
        SwingUtilities.invokeLater(() -> {
            new LoginPage();
        });
    }
}
