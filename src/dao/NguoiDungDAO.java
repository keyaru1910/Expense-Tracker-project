package dao;

import model.NguoiDung;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp triển khai INguoiDungDAO để thao tác dữ liệu bảng `users` trong MySQL.
 */
public class NguoiDungDAO implements INguoiDungDAO {

    @Override
    public boolean them(NguoiDung doiTuong) {
        return dangKy(doiTuong);
    }

    @Override
    public boolean capNhat(NguoiDung doiTuong) {
        String sql = "UPDATE users SET password = ?, email = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, doiTuong.getPassword());
            pst.setString(2, doiTuong.getEmail());
            pst.setInt(3, doiTuong.getId());
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
        String sql = "SELECT id, username, password, email FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new NguoiDung(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
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
        String sql = "SELECT id, username, password, email FROM users";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                danhSach.add(new NguoiDung(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách người dùng: " + e.getMessage());
        }
        return danhSach;
    }

    @Override
    public NguoiDung dangNhap(String username, String password) {
        String sql = "SELECT id, username, password, email FROM users WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new NguoiDung(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xác thực đăng nhập: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean dangKy(NguoiDung nguoiDung) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nguoiDung.getUsername());
            pst.setString(2, nguoiDung.getPassword());
            pst.setString(3, nguoiDung.getEmail());
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
}

