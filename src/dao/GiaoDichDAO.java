package dao;

import model.GiaoDich;
import model.KhoanChi;
import model.KhoanThu;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp triển khai IGiaoDichDAO để thao tác dữ liệu bảng `transactions` trong MySQL.
 * Thể hiện tính Đa hình (Polymorphism): tự động ánh xạ thành KhoanThu hoặc KhoanChi.
 * Tích hợp Đa ví (cộng/trừ số dư ví tự động) và các truy vấn Báo cáo Ngày/Tháng/Quý/Năm.
 */
public class GiaoDichDAO implements IGiaoDichDAO {
    private final IViTienDAO viTienDAO = new ViTienDAO();

    @Override
    public boolean them(GiaoDich gd) {
        String sql = "INSERT INTO transactions (username, category_name, description, amount, type, vi_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, gd.getUsername());
            pst.setString(2, gd.getCategoryName());
            pst.setString(3, gd.getDescription());
            pst.setDouble(4, gd.getAmount());
            pst.setString(5, gd.getType());
            if (gd.getViId() != null && gd.getViId() > 0) {
                pst.setInt(6, gd.getViId());
            } else {
                pst.setNull(6, java.sql.Types.INTEGER);
            }

            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rsKey = pst.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        gd.setId(rsKey.getInt(1));
                    }
                }
                // Tự động cập nhật số dư ví nếu giao dịch có gắn với ví
                if (gd.getViId() != null && gd.getViId() > 0) {
                    double thayDoi = "income".equalsIgnoreCase(gd.getType()) ? gd.getAmount() : -gd.getAmount();
                    viTienDAO.capNhatSoDu(gd.getViId(), thayDoi);
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm giao dịch: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean capNhat(GiaoDich gd) {
        String sql = "UPDATE transactions SET description = ?, amount = ?, date = ?, category_name = ?, type = ?, vi_id = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, gd.getDescription());
            pst.setDouble(2, gd.getAmount());
            pst.setString(3, gd.getDateTime());
            pst.setString(4, gd.getCategoryName());
            pst.setString(5, gd.getType());
            if (gd.getViId() != null && gd.getViId() > 0) {
                pst.setInt(6, gd.getViId());
            } else {
                pst.setNull(6, java.sql.Types.INTEGER);
            }
            pst.setInt(7, gd.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật giao dịch: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean capNhatThongTinCoBan(int id, String description, double amount, String dateTime) {
        String sql = "UPDATE transactions SET description = ?, amount = ?, date = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, description);
            pst.setDouble(2, amount);
            pst.setString(3, dateTime);
            pst.setInt(4, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật cơ bản giao dịch: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean xoa(int id) {
        // Trước khi xóa, lấy thông tin ví và số tiền để hoàn trả lại số dư ví
        GiaoDich gd = layTheoId(id);
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            boolean thanhCong = pst.executeUpdate() > 0;
            if (thanhCong && gd != null && gd.getViId() != null && gd.getViId() > 0) {
                // Hoàn lại tiền vào ví
                double hoanTien = "income".equalsIgnoreCase(gd.getType()) ? -gd.getAmount() : gd.getAmount();
                viTienDAO.capNhatSoDu(gd.getViId(), hoanTien);
            }
            return thanhCong;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa giao dịch: " + e.getMessage());
            return false;
        }
    }

    @Override
    public GiaoDich layTheoId(int id) {
        String sql = "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id WHERE t.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return taoDoiTuongGiaoDich(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy giao dịch theo id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<GiaoDich> layTatCa() {
        List<GiaoDich> danhSach = new ArrayList<>();
        String sql = "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id ORDER BY t.date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                danhSach.add(taoDoiTuongGiaoDich(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách giao dịch: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<GiaoDich> layDanhSachGiaoDich(String username, String loaiLoc, String tuKhoa) {
        List<GiaoDich> danhSach = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id WHERE t.username = ?"
        );

        if ("Thu nhập".equals(loaiLoc)) {
            sql.append(" AND t.type = 'income'");
        } else if ("Chi tiêu".equals(loaiLoc)) {
            sql.append(" AND t.type = 'expense'");
        }

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append(" AND (LOWER(t.description) LIKE ? OR LOWER(t.category_name) LIKE ?)");
        }

        sql.append(" ORDER BY t.date DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {
            pst.setString(1, username);
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String pattern = "%" + tuKhoa.trim().toLowerCase() + "%";
                pst.setString(2, pattern);
                pst.setString(3, pattern);
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoDoiTuongGiaoDich(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lọc giao dịch: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public double tinhTongThuNhap(String username) {
        return tinhTongTheoLoai(username, "income");
    }

    @Override
    public double tinhTongChiTieu(String username) {
        return tinhTongTheoLoai(username, "expense");
    }

    private double tinhTongTheoLoai(String username, String loai) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE username = ? AND type = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, loai);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng (" + loai + "): " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public double[] layThongKe12Thang(String username, String nam, String loaiGiaoDich) {
        double[] ketQua = new double[12];
        boolean coLocNam = (nam != null && !nam.equals("Tất cả"));
        String sql = "SELECT MONTH(date) as thang, SUM(amount) as tong " +
                "FROM transactions WHERE username = ? AND type = ? " +
                (coLocNam ? "AND YEAR(date) = ? " : "") +
                "GROUP BY MONTH(date) ORDER BY thang";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, loaiGiaoDich);
            if (coLocNam) {
                pst.setInt(3, Integer.parseInt(nam));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int thang = rs.getInt("thang");
                    if (thang >= 1 && thang <= 12) {
                        ketQua[thang - 1] = rs.getDouble("tong");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê 12 tháng: " + e.getMessage());
        }
        return ketQua;
    }

    @Override
    public List<GiaoDich> layTheoNgay(String username, String ngay) {
        String sql = "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id " +
                "WHERE t.username = ? AND DATE(t.date) = ? ORDER BY t.date DESC";
        return thucThiTruyVanDanhSach(sql, username, ngay);
    }

    @Override
    public List<GiaoDich> layTheoKhoangThoiGian(String username, String tuNgay, String denNgay) {
        String sql = "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id " +
                "WHERE t.username = ? AND DATE(t.date) >= ? AND DATE(t.date) <= ? ORDER BY t.date DESC";
        List<GiaoDich> danhSach = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, tuNgay);
            pst.setString(3, denNgay);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoDoiTuongGiaoDich(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn theo khoảng thời gian: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<GiaoDich> layTheoThang(String username, int thang, int nam) {
        String sql = "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id " +
                "WHERE t.username = ? AND MONTH(t.date) = ? AND YEAR(t.date) = ? ORDER BY t.date DESC";
        List<GiaoDich> danhSach = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setInt(2, thang);
            pst.setInt(3, nam);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoDoiTuongGiaoDich(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn theo tháng: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<GiaoDich> layTheoQuy(String username, int quy, int nam) {
        int thangBatDau = (quy - 1) * 3 + 1;
        int thangKetThuc = thangBatDau + 2;
        String sql = "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id " +
                "WHERE t.username = ? AND YEAR(t.date) = ? AND MONTH(t.date) >= ? AND MONTH(t.date) <= ? ORDER BY t.date DESC";
        List<GiaoDich> danhSach = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setInt(2, nam);
            pst.setInt(3, thangBatDau);
            pst.setInt(4, thangKetThuc);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoDoiTuongGiaoDich(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn theo quý: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<GiaoDich> layTheoNam(String username, int nam) {
        String sql = "SELECT t.id, t.username, t.category_name, t.description, t.amount, t.type, t.date, t.vi_id, v.ten_vi " +
                "FROM transactions t LEFT JOIN vi_tien v ON t.vi_id = v.id " +
                "WHERE t.username = ? AND YEAR(t.date) = ? ORDER BY t.date DESC";
        List<GiaoDich> danhSach = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setInt(2, nam);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoDoiTuongGiaoDich(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn theo năm: " + e.getMessage());
        }
        return danhSach;
    }

    private List<GiaoDich> thucThiTruyVanDanhSach(String sql, String username, String param) {
        List<GiaoDich> danhSach = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, param);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoDoiTuongGiaoDich(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thực thi truy vấn danh sách: " + e.getMessage());
        }
        return danhSach;
    }

    /**
     * Phương thức ánh xạ từ bản ghi CSDL sang đối tượng KhoanThu hoặc KhoanChi.
     */
    private GiaoDich taoDoiTuongGiaoDich(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String categoryName = rs.getString("category_name");
        String description = rs.getString("description");
        double amount = rs.getDouble("amount");
        String type = rs.getString("type");
        String date = rs.getString("date");

        Integer viId = null;
        try {
            int v = rs.getInt("vi_id");
            if (!rs.wasNull()) {
                viId = v;
            }
        } catch (SQLException ignored) {}

        String tenVi = "Mặc định";
        try {
            String tv = rs.getString("ten_vi");
            if (tv != null && !tv.isEmpty()) {
                tenVi = tv;
            }
        } catch (SQLException ignored) {}

        if ("income".equalsIgnoreCase(type)) {
            return new KhoanThu(id, username, categoryName, description, amount, date, viId, tenVi);
        } else {
            return new KhoanChi(id, username, categoryName, description, amount, date, viId, tenVi);
        }
    }
}
