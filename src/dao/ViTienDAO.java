package dao;

import model.ViTien;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp triển khai IViTienDAO để thao tác dữ liệu bảng `vi_tien` trong MySQL.
 */
public class ViTienDAO implements IViTienDAO {

    @Override
    public boolean them(ViTien vi) {
        String sql = "INSERT INTO vi_tien (user_id, ten_vi, loai_vi, so_du, ghi_chu) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, vi.getUserId());
            pst.setString(2, vi.getTenVi());
            pst.setString(3, vi.getLoaiVi() != null ? vi.getLoaiVi() : "TIEN_MAT");
            pst.setDouble(4, vi.getSoDu());
            pst.setString(5, vi.getGhiChu());
            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rsKey = pst.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        vi.setId(rsKey.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm ví tiền: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean capNhat(ViTien vi) {
        String sql = "UPDATE vi_tien SET ten_vi = ?, loai_vi = ?, so_du = ?, ghi_chu = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, vi.getTenVi());
            pst.setString(2, vi.getLoaiVi());
            pst.setDouble(3, vi.getSoDu());
            pst.setString(4, vi.getGhiChu());
            pst.setInt(5, vi.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật ví: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean xoa(int id) {
        String sql = "DELETE FROM vi_tien WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa ví: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ViTien layTheoId(int id) {
        String sql = "SELECT id, user_id, ten_vi, loai_vi, so_du, ghi_chu FROM vi_tien WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new ViTien(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("ten_vi"),
                            rs.getString("loai_vi"),
                            rs.getDouble("so_du"),
                            rs.getString("ghi_chu")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ví theo id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ViTien> layTatCa() {
        List<ViTien> danhSach = new ArrayList<>();
        String sql = "SELECT id, user_id, ten_vi, loai_vi, so_du, ghi_chu FROM vi_tien ORDER BY id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                danhSach.add(new ViTien(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("ten_vi"),
                        rs.getString("loai_vi"),
                        rs.getDouble("so_du"),
                        rs.getString("ghi_chu")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tất cả ví: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<ViTien> layDanhSachTheoUserId(int userId) {
        List<ViTien> danhSach = new ArrayList<>();
        String sql = "SELECT id, user_id, ten_vi, loai_vi, so_du, ghi_chu FROM vi_tien WHERE user_id = ? ORDER BY id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new ViTien(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("ten_vi"),
                            rs.getString("loai_vi"),
                            rs.getDouble("so_du"),
                            rs.getString("ghi_chu")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách ví theo userId: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public boolean capNhatSoDu(int viId, double soTienThayDoi) {
        String sql = "UPDATE vi_tien SET so_du = so_du + ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, soTienThayDoi);
            pst.setInt(2, viId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật số dư ví: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean chuyenTien(int viNguonId, int viDichId, double soTien, String ghiChu, String username) {
        if (viNguonId == viDichId || soTien <= 0) {
            return false;
        }

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction đảm bảo tính toàn vẹn Atomicity (ACID)

            // 1. Kiểm tra số dư ví nguồn
            String sqlCheck = "SELECT ten_vi, so_du FROM vi_tien WHERE id = ? FOR UPDATE";
            String tenViNguon = "";
            double soDuNguon = 0;
            try (PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
                pstCheck.setInt(1, viNguonId);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    if (rs.next()) {
                        tenViNguon = rs.getString("ten_vi");
                        soDuNguon = rs.getDouble("so_du");
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            if (soDuNguon < soTien) {
                con.rollback();
                return false; // Không đủ số dư để chuyển
            }

            // 2. Lấy tên ví đích
            String tenViDich = "";
            try (PreparedStatement pstDich = con.prepareStatement(sqlCheck)) {
                pstDich.setInt(1, viDichId);
                try (ResultSet rs = pstDich.executeQuery()) {
                    if (rs.next()) {
                        tenViDich = rs.getString("ten_vi");
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            // 3. Trừ tiền ví nguồn
            String sqlTruTien = "UPDATE vi_tien SET so_du = so_du - ? WHERE id = ?";
            try (PreparedStatement pstTru = con.prepareStatement(sqlTruTien)) {
                pstTru.setDouble(1, soTien);
                pstTru.setInt(2, viNguonId);
                pstTru.executeUpdate();
            }

            // 4. Cộng tiền ví đích
            String sqlCongTien = "UPDATE vi_tien SET so_du = so_du + ? WHERE id = ?";
            try (PreparedStatement pstCong = con.prepareStatement(sqlCongTien)) {
                pstCong.setDouble(1, soTien);
                pstCong.setInt(2, viDichId);
                pstCong.executeUpdate();
            }

            // 5. Ghi lịch sử giao dịch: Khoản chi cho ví nguồn và Khoản thu cho ví đích
            String moTaNguon = "Chuyển tiền sang [" + tenViDich + "]" + (ghiChu.isEmpty() ? "" : ": " + ghiChu);
            String moTaDich = "Nhận tiền từ [" + tenViNguon + "]" + (ghiChu.isEmpty() ? "" : ": " + ghiChu);

            String sqlGiaoDich = "INSERT INTO transactions (username, category_name, description, amount, type, vi_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstGD = con.prepareStatement(sqlGiaoDich)) {
                // Giao dịch trừ tiền (expense)
                pstGD.setString(1, username);
                pstGD.setString(2, "Chuyển khoản");
                pstGD.setString(3, moTaNguon);
                pstGD.setDouble(4, soTien);
                pstGD.setString(5, "expense");
                pstGD.setInt(6, viNguonId);
                pstGD.executeUpdate();

                // Giao dịch cộng tiền (income)
                pstGD.setString(1, username);
                pstGD.setString(2, "Chuyển khoản");
                pstGD.setString(3, moTaDich);
                pstGD.setDouble(4, soTien);
                pstGD.setString(5, "income");
                pstGD.setInt(6, viDichId);
                pstGD.executeUpdate();
            }

            con.commit(); // Hoàn tất thành công
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi Transaction chuyển tiền: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void khoiTaoViMacDinh(int userId) {
        // Kiểm tra xem người dùng đã có ví nào chưa
        List<ViTien> danhSach = layDanhSachTheoUserId(userId);
        if (danhSach.isEmpty()) {
            // Tự động tạo 2 ví thiết yếu: Tiền mặt và Tài khoản ngân hàng
            them(new ViTien(userId, "Ví Tiền mặt", "TIEN_MAT", 0.0, "Tiền mặt hàng ngày"));
            them(new ViTien(userId, "Tài khoản Ngân hàng", "NGAN_HANG", 0.0, "Tài khoản ngân hàng chính"));
            System.out.println("-> Đã khởi tạo 2 ví mặc định cho userId: " + userId);
        }
    }

    @Override
    public double layTongTaiSan(int userId) {
        String sql = "SELECT COALESCE(SUM(so_du), 0) FROM vi_tien WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng tài sản: " + e.getMessage());
        }
        return 0.0;
    }
}

