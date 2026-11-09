package model;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends JFrame {
    public LoginPage() {
        // Phông chữ Segoe UI chuẩn tiếng Việt Unicode
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 30);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 18);

        // Tiêu đề ứng dụng
        JLabel title = new JLabel("Quản Lý Chi Tiêu Thông Minh", JLabel.CENTER);
        if (GiaoDienHelper.laGiaoDienToi()) {
            title.setForeground(new Color(0, 255, 200));
        } else {
            title.setForeground(new Color(13, 148, 136));
        }

        // Nhãn và ô nhập thông tin đăng nhập
        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        JTextField txtTenDangNhap = new JTextField(15);
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        JPasswordField txtMatKhau = new JPasswordField(15);

        // Nút đăng nhập
        JButton btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(new Color(50, 202, 202));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setOpaque(true);

        // Liên kết chuyển sang trang đăng ký
        JLabel lblDangKy = new JLabel("Chưa có tài khoản? Đăng ký ngay");
        lblDangKy.setForeground(new Color(50, 202, 202));
        lblDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thiết lập phông chữ cho các thành phần giao diện
        title.setFont(fontTieuDe);
        lblTenDangNhap.setFont(fontNhan);
        txtTenDangNhap.setFont(fontNhan);
        lblMatKhau.setFont(fontNhan);
        txtMatKhau.setFont(fontNhan);
        btnDangNhap.setFont(fontNut);
        lblDangKy.setFont(fontNhan);

        // Định vị vị trí và kích thước bên trong thẻ đăng nhập (Card)
        title.setBounds(10, 25, 600, 45);
        lblTenDangNhap.setBounds(80, 105, 140, 36);
        txtTenDangNhap.setBounds(230, 105, 310, 36);
        lblMatKhau.setBounds(80, 165, 140, 36);
        txtMatKhau.setBounds(230, 165, 310, 36);
        btnDangNhap.setBounds(210, 235, 200, 42);
        lblDangKy.setBounds(160, 305, 300, 30);
        lblDangKy.setHorizontalAlignment(SwingConstants.CENTER);

        // Thẻ đăng nhập dạng Card căn giữa màn hình
        JPanel pnlTheDangNhap = new JPanel();
        pnlTheDangNhap.setLayout(null);
        pnlTheDangNhap.setPreferredSize(new Dimension(620, 390));
        GiaoDienHelper.apDungMauTheCard(pnlTheDangNhap);

        pnlTheDangNhap.add(title);
        pnlTheDangNhap.add(lblTenDangNhap);
        pnlTheDangNhap.add(txtTenDangNhap);
        pnlTheDangNhap.add(lblMatKhau);
        pnlTheDangNhap.add(txtMatKhau);
        pnlTheDangNhap.add(btnDangNhap);
        pnlTheDangNhap.add(lblDangKy);

        // Sử dụng GridBagLayout để khi phóng to toàn màn hình, thẻ đăng nhập luôn nằm chính giữa
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheDangNhap);

        // Xử lý sự kiện đăng nhập
        btnDangNhap.addActionListener(a -> {
            String username = txtTenDangNhap.getText().trim();
            String password = new String(txtMatKhau.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtTenDangNhap.setText("");
                txtMatKhau.setText("");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                String sql = "SELECT * FROM users WHERE username=? AND password=?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, username);
                    pst.setString(2, password);

                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        // Đảm bảo người dùng có sẵn danh mục mặc định
                        DanhMucHelper.khoiTaoDanhMucNeuChuaCo(username);
                        JOptionPane.showMessageDialog(null, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        new Home(username);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Tên đăng nhập hoặc mật khẩu không chính xác!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý chuyển sang trang đăng ký
        lblDangKy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterPage();
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnDangNhap);
        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Trang Đăng Nhập");
    }

    public static void main(String[] args) {
        GiaoDienHelper.khoiTaoGiaoDienMacDinh();
        new LoginPage();
    }
}
