package dao;

import model.DanhMuc;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp triển khai IDanhMucDAO để thao tác dữ liệu bảng `categories` trong MySQL.
 */
public class DanhMucDAO implements IDanhMucDAO {

    // Danh sách các danh mục mặc định thông dụng với mã màu trực quan
    public static final String[][] DANH_MUC_MAC_DINH = {
            {"Ăn uống", "#FF5722"},           // Cam đậm
            {"Mua sắm", "#E91E63"},           // Hồng
            {"Di chuyển", "#2196F3"},         // Xanh da trời
            {"Hóa đơn & Tiện ích", "#FF9800"}, // Vàng cam
            {"Giải trí", "#9C27B0"},          // Tím
            {"Sức khỏe", "#F44336"},          // Đỏ
            {"Tiền lương", "#4CAF50"},        // Xanh lá
            {"Thưởng & Đầu tư", "#009688"},   // Xanh mòng két
            {"Khác", "#607D8B"}               // Xám ghi
    };

    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    @Override
    public boolean them(DanhMuc dm) {
        String sql = "INSERT INTO categories (user_id, name, color) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, dm.getUserId());
            pst.setString(2, dm.getName());
            pst.setString(3, dm.getColorHex());
            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rsKey = pst.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        dm.setId(rsKey.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm danh mục: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean capNhat(DanhMuc dm) {
        String sql = "UPDATE categories SET name = ?, color = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, dm.getName());
            pst.setString(2, dm.getColorHex());
            pst.setInt(3, dm.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật danh mục: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean xoa(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa danh mục: " + e.getMessage());
            return false;
        }
    }

    @Override
    public DanhMuc layTheoId(int id) {
        String sql = "SELECT id, user_id, name, color FROM categories WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new DanhMuc(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("color")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh mục theo id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<DanhMuc> layTatCa() {
        List<DanhMuc> danhSach = new ArrayList<>();
        String sql = "SELECT id, user_id, name, color FROM categories";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                danhSach.add(new DanhMuc(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("color")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy toàn bộ danh mục: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<DanhMuc> layDanhSachTheoUsername(String username) {
        List<DanhMuc> danhSach = new ArrayList<>();
        String sql = "SELECT id, user_id, name, color FROM categories WHERE user_id = (SELECT id FROM users WHERE username = ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new DanhMuc(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("color")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh mục theo username: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<DanhMuc> layDanhSachTheoUserId(int userId) {
        List<DanhMuc> danhSach = new ArrayList<>();
        String sql = "SELECT id, user_id, name, color FROM categories WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new DanhMuc(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("color")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh mục theo userId: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public void khoiTaoDanhMucNeuChuaCo(String username) {
        int userId = nguoiDungDAO.layIdTheoUsername(username);
        if (userId == -1) return;

        String sqlCheck = "SELECT COUNT(*) FROM categories WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
            pstCheck.setInt(1, userId);
            try (ResultSet rs = pstCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String sqlInsert = "INSERT INTO categories (user_id, name, color) VALUES (?, ?, ?)";
                    try (PreparedStatement pstInsert = con.prepareStatement(sqlInsert)) {
                        for (String[] dm : DANH_MUC_MAC_DINH) {
                            pstInsert.setInt(1, userId);
                            pstInsert.setString(2, dm[0]);
                            pstInsert.setString(3, dm[1]);
                            pstInsert.addBatch();
                        }
                        pstInsert.executeBatch();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo danh mục mặc định: " + e.getMessage());
        }
    }
}

