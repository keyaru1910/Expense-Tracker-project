package dao;

import model.NguoiDung;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp triển khai INguoiDungDAO để thao tác dữ liệu bảng `users` trong MySQL.
 * Hỗ trợ phân quyền Quản trị viên (ADMIN) và quản trị tài khoản thành viên.
 */
public class NguoiDungDAO implements INguoiDungDAO {

    @Override
    public boolean them(NguoiDung doiTuong) {
        return dangKy(doiTuong);
    }

    @Override
    public boolean capNhat(NguoiDung doiTuong) {
        String sql = "UPDATE users SET password = ?, email = ?, vai_tro = ?, trang_thai = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, doiTuong.getPassword());
            pst.setString(2, doiTuong.getEmail());
            pst.setString(3, doiTuong.getVaiTro());
            pst.setString(4, doiTuong.getTrangThai());
            pst.setInt(5, doiTuong.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật người dùng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean xoa(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa người dùng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public NguoiDung layTheoId(int id) {
        String sql = "SELECT id, username, password, email, vai_tro, trang_thai FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return taoDoiTuongTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy người dùng theo id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<NguoiDung> layTatCa() {
        List<NguoiDung> danhSach = new ArrayList<>();
        String sql = "SELECT id, username, password, email, vai_tro, trang_thai FROM users ORDER BY id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách người dùng: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public NguoiDung dangNhap(String username, String password) {
        String sql = "SELECT id, username, password, email, vai_tro, trang_thai FROM users WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return taoDoiTuongTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xác thực đăng nhập: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean dangKy(NguoiDung nguoiDung) {
        String sql = "INSERT INTO users (username, password, email, vai_tro, trang_thai) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nguoiDung.getUsername());
            pst.setString(2, nguoiDung.getPassword());
            pst.setString(3, nguoiDung.getEmail());
            pst.setString(4, nguoiDung.getVaiTro() != null ? nguoiDung.getVaiTro() : "USER");
            pst.setString(5, nguoiDung.getTrangThai() != null ? nguoiDung.getTrangThai() : "ACTIVE");
            int affected = pst.executeUpdate();
            if (affected > 0) {
                try (ResultSet rsKey = pst.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        nguoiDung.setId(rsKey.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký tài khoản: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean kiemTraTonTai(String username, String email) {
        String sql = "SELECT id FROM users WHERE username = ? OR email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, email);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra trùng lặp người dùng: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int layIdTheoUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ID người dùng: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean khoaMoKhoaTaiKhoan(int id, String trangThaiMoi) {
        String sql = "UPDATE users SET trang_thai = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, trangThaiMoi);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái khóa/mở tài khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean datLaiMatKhau(int id, String matKhauMoi) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, matKhauMoi);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi đặt lại mật khẩu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int layTongSoNguoiDung() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm số người dùng: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int layTongSoGiaoDichHeThong() {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm số giao dịch hệ thống: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double layTongTienGiaoDichHeThong() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng tiền giao dịch hệ thống: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Phương thức phụ trợ ánh xạ từ ResultSet sang NguoiDung an toàn (hỗ trợ tương thích ngược nếu chưa có cột).
     */
    private NguoiDung taoDoiTuongTuResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");

        String vaiTro = "USER";
        String trangThai = "ACTIVE";
        try {
            String vt = rs.getString("vai_tro");
            if (vt != null) vaiTro = vt;
        } catch (SQLException ignored) {}

        try {
            String tt = rs.getString("trang_thai");
            if (tt != null) trangThai = tt;
        } catch (SQLException ignored) {}

        return new NguoiDung(id, username, password, email, vaiTro, trangThai);
    }
}
