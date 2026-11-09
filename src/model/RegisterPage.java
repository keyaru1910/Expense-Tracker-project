package model;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterPage extends JFrame {
    public RegisterPage() {
        // Phông chữ Segoe UI hỗ trợ tiếng Việt Unicode
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 30);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 18);

        JLabel title = new JLabel("Đăng Ký Tài Khoản", JLabel.CENTER);
        if (GiaoDienHelper.laGiaoDienToi()) {
            title.setForeground(new Color(0, 255, 180));
        } else {
            title.setForeground(new Color(13, 148, 136));
        }

        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        JTextField txtTenDangNhap = new JTextField(15);
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField(20);
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        JPasswordField txtMatKhau = new JPasswordField(15);
        JLabel lblXacNhanMatKhau = new JLabel("Xác nhận MK:");
        JPasswordField txtXacNhanMatKhau = new JPasswordField(15);

        JButton btnDangKy = new JButton("Đăng ký");
        btnDangKy.setBackground(new Color(0, 153, 102));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFocusPainted(false);
        btnDangKy.setBorderPainted(false);
        btnDangKy.setOpaque(true);

        JButton btnQuayLai = new JButton("Quay lại");
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);

        title.setFont(fontTieuDe);
        lblTenDangNhap.setFont(fontLabel);
        lblMatKhau.setFont(fontLabel);
        lblXacNhanMatKhau.setFont(fontLabel);
        lblEmail.setFont(fontLabel);
        txtTenDangNhap.setFont(fontLabel);
        txtMatKhau.setFont(fontLabel);
        txtXacNhanMatKhau.setFont(fontLabel);
        txtEmail.setFont(fontLabel);
        btnDangKy.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        int labelX = 80, fieldX = 260, yStart = 95, widthLabel = 160, widthField = 310, height = 36, gap = 50;

        title.setBounds(30, 25, 600, 45);

        lblTenDangNhap.setBounds(labelX, yStart, widthLabel, height);
        txtTenDangNhap.setBounds(fieldX, yStart, widthField, height);

        lblEmail.setBounds(labelX, yStart + gap, widthLabel, height);
        txtEmail.setBounds(fieldX, yStart + gap, widthField, height);

        lblMatKhau.setBounds(labelX, yStart + 2 * gap, widthLabel, height);
        txtMatKhau.setBounds(fieldX, yStart + 2 * gap, widthField, height);

        lblXacNhanMatKhau.setBounds(labelX, yStart + 3 * gap, widthLabel, height);
        txtXacNhanMatKhau.setBounds(fieldX, yStart + 3 * gap, widthField, height);

        btnDangKy.setBounds(230, yStart + 4 * gap + 10, 200, 40);
        btnQuayLai.setBounds(230, yStart + 5 * gap + 10, 200, 40);

        // Thẻ đăng ký dạng Card căn giữa màn hình
        JPanel pnlTheDangKy = new JPanel();
        pnlTheDangKy.setLayout(null);
        pnlTheDangKy.setPreferredSize(new Dimension(660, 485));
        GiaoDienHelper.apDungMauTheCard(pnlTheDangKy);

        pnlTheDangKy.add(title);
        pnlTheDangKy.add(lblTenDangNhap);
        pnlTheDangKy.add(txtTenDangNhap);
        pnlTheDangKy.add(lblEmail);
        pnlTheDangKy.add(txtEmail);
        pnlTheDangKy.add(lblMatKhau);
        pnlTheDangKy.add(txtMatKhau);
        pnlTheDangKy.add(lblXacNhanMatKhau);
        pnlTheDangKy.add(txtXacNhanMatKhau);
        pnlTheDangKy.add(btnDangKy);
        pnlTheDangKy.add(btnQuayLai);

        // Sử dụng GridBagLayout để khi phóng to toàn màn hình, thẻ đăng ký luôn nằm chính giữa
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheDangKy);

        // Xử lý sự kiện đăng ký
        btnDangKy.addActionListener(a -> {
            String username = txtTenDangNhap.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtMatKhau.getPassword());
            String confirmPass = new String(txtXacNhanMatKhau.getPassword());

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ các trường thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirmPass)) {
                JOptionPane.showMessageDialog(null, "Mật khẩu xác nhận không khớp!", "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                txtMatKhau.setText("");
                txtXacNhanMatKhau.setText("");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                String checkSql = "SELECT * FROM users WHERE username=? OR email=?";
                try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                    checkStmt.setString(1, username);
                    checkStmt.setString(2, email);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Tên đăng nhập hoặc Email đã tồn tại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                String insertSql = "INSERT INTO users(username, email, password) VALUES(?, ?, ?)";
                try (PreparedStatement pst = con.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pst.setString(1, username);
                    pst.setString(2, email);
                    pst.setString(3, password);
                    pst.executeUpdate();

                    // Tự động khởi tạo danh mục mặc định cho tài khoản mới
                    try (ResultSet rsGeneratedKeys = pst.getGeneratedKeys()) {
                        if (rsGeneratedKeys.next()) {
                            int newUserId = rsGeneratedKeys.getInt(1);
                            DanhMucHelper.khoiTaoDanhMucChoUserId(con, newUserId);
                        }
                    }

                    JOptionPane.showMessageDialog(null, "Đăng ký thành công! Đang chuyển đến trang Đăng nhập...", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    new LoginPage();
                    dispose();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý nút quay lại
        btnQuayLai.addActionListener(a -> {
            new LoginPage();
            dispose();
        });

        getRootPane().setDefaultButton(btnDangKy);
        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Trang Đăng Ký");
    }

    public static void main(String[] args) {
        new RegisterPage();
    }
}
