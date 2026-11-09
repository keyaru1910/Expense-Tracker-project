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
 * Thể hiện tính Đa hình (Polymorphism): tự động ánh xạ thành KhoanThu hoặc KhoanChi
 * dựa trên giá trị cột `type`.
 */
public class GiaoDichDAO implements IGiaoDichDAO {

    @Override
    public boolean them(GiaoDich gd) {
        String sql = "INSERT INTO transactions (username, category_name, description, amount, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, gd.getUsername());
            pst.setString(2, gd.getCategoryName());
            pst.setString(3, gd.getDescription());
            pst.setDouble(4, gd.getAmount());
            pst.setString(5, gd.getType());
            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rsKey = pst.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        gd.setId(rsKey.getInt(1));
                    }
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
        String sql = "UPDATE transactions SET description = ?, amount = ?, date = ?, category_name = ?, type = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, gd.getDescription());
            pst.setDouble(2, gd.getAmount());
            pst.setString(3, gd.getDateTime());
            pst.setString(4, gd.getCategoryName());
            pst.setString(5, gd.getType());
            pst.setInt(6, gd.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật giao dịch: " + e.getMessage());
            return false;
        }
    }

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
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa giao dịch: " + e.getMessage());
            return false;
        }
    }

    @Override
    public GiaoDich layTheoId(int id) {
        String sql = "SELECT id, username, category_name, description, amount, type, date FROM transactions WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return taoDoiTuongTuResultSet(rs);
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
        String sql = "SELECT id, username, category_name, description, amount, type, date FROM transactions ORDER BY date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy toàn bộ giao dịch: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public List<GiaoDich> layDanhSachGiaoDich(String username, String loaiLoc, String tuKhoa) {
        List<GiaoDich> danhSach = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, username, category_name, description, amount, type, date FROM transactions WHERE username = ?");

        boolean locThuNhap = "Thu nhập".equalsIgnoreCase(loaiLoc) || "income".equalsIgnoreCase(loaiLoc);
        boolean locChiTieu = "Chi tiêu".equalsIgnoreCase(loaiLoc) || "expense".equalsIgnoreCase(loaiLoc);

        if (locThuNhap) {
            sql.append(" AND type = 'income'");
        } else if (locChiTieu) {
            sql.append(" AND type = 'expense'");
        }

        boolean coTuKhoa = tuKhoa != null && !tuKhoa.trim().isEmpty();
        if (coTuKhoa) {
            sql.append(" AND (LOWER(description) LIKE ? OR LOWER(category_name) LIKE ?)");
        }

        sql.append(" ORDER BY date DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            pst.setString(paramIndex++, username);

            if (coTuKhoa) {
                String searchPattern = "%" + tuKhoa.trim().toLowerCase() + "%";
                pst.setString(paramIndex++, searchPattern);
                pst.setString(paramIndex++, searchPattern);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoDoiTuongTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lọc danh sách giao dịch: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public double tinhTongThuNhap(String username) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE username = ? AND type = 'income'";
        return tinhTongTheoLoai(username, sql);
    }

    @Override
    public double tinhTongChiTieu(String username) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE username = ? AND type = 'expense'";
        return tinhTongTheoLoai(username, sql);
    }

    private double tinhTongTheoLoai(String username, String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng tiền: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public double[] layThongKe12Thang(String username, String nam, String loaiGiaoDich) {
        double[] duLieu12Thang = new double[12];
        StringBuilder sql = new StringBuilder("SELECT MONTH(date) AS thang, SUM(amount) AS tong FROM transactions WHERE username = ? AND type = ?");

        boolean locTheoNam = nam != null && !nam.equals("Tất cả") && !nam.trim().isEmpty();
        if (locTheoNam) {
            sql.append(" AND YEAR(date) = ?");
        }

        sql.append(" GROUP BY MONTH(date)");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {
            pst.setString(1, username);
            pst.setString(2, loaiGiaoDich);
            if (locTheoNam) {
                pst.setInt(3, Integer.parseInt(nam.trim()));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int thang = rs.getInt("thang"); // 1 đến 12
                    double tong = rs.getDouble("tong");
                    if (thang >= 1 && thang <= 12) {
                        duLieu12Thang[thang - 1] = tong;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê 12 tháng: " + e.getMessage());
        }
        return duLieu12Thang;
    }

    /**
     * Phương thức nội bộ chuyển đổi ResultSet thành đối tượng GiaoDich cụ thể (KhoanThu hoặc KhoanChi).
     * Áp dụng tính Đa hình (Polymorphism).
     */
    private GiaoDich taoDoiTuongTuResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String catName = rs.getString("category_name");
        String desc = rs.getString("description");
        double amount = rs.getDouble("amount");
        String type = rs.getString("type");
        String date = rs.getString("date");

        if ("income".equalsIgnoreCase(type)) {
            return new KhoanThu(id, username, catName, desc, amount, date);
        } else {
            return new KhoanChi(id, username, catName, desc, amount, date);
        }
    }
}

