package model;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateCategory extends JFrame {
    private Color selectedColor = Color.RED;

    public CreateCategory(String username) {
        // Phông chữ hỗ trợ tiếng Việt
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);

        JLabel title = new JLabel("Tạo Danh Mục Mới", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);
        title.setFont(fontTieuDe);

        JLabel lblTenDanhMuc = new JLabel("Tên danh mục:");
        JTextField txtTenDanhMuc = new JTextField(15);
        JLabel lblMauSac = new JLabel("Màu danh mục:");
        JButton btnChonMau = new JButton("Chọn màu");
        GiaoDienHelper.apDungMauNutChucNang(btnChonMau);

        JPanel pnlMau = new JPanel();
        pnlMau.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pnlMau.setPreferredSize(new Dimension(35, 35));
        pnlMau.setBackground(selectedColor);

        JButton btnTao = new JButton("Tạo danh mục");
        btnTao.setBackground(new Color(0, 153, 102));
        btnTao.setForeground(Color.WHITE);
        btnTao.setFocusPainted(false);
        btnTao.setBorderPainted(false);
        btnTao.setOpaque(true);

        JButton btnQuayLai = new JButton("Quay lại");
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);

        lblTenDanhMuc.setFont(fontLabel);
        txtTenDanhMuc.setFont(fontLabel);
        lblMauSac.setFont(fontLabel);
        btnChonMau.setFont(fontLabel);
        btnTao.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        title.setBounds(10, 20, 600, 45);
        lblTenDanhMuc.setBounds(70, 85, 150, 36);
        txtTenDanhMuc.setBounds(230, 85, 310, 36);
        lblMauSac.setBounds(70, 145, 150, 36);
        btnChonMau.setBounds(230, 145, 150, 36);
        pnlMau.setBounds(400, 145, 36, 36);
        btnTao.setBounds(190, 215, 240, 42);
        btnQuayLai.setBounds(190, 275, 240, 42);

        // Thẻ tạo danh mục dạng Card căn giữa màn hình
        JPanel pnlTheTaoDanhMuc = new JPanel();
        pnlTheTaoDanhMuc.setLayout(null);
        pnlTheTaoDanhMuc.setPreferredSize(new Dimension(620, 355));
        GiaoDienHelper.apDungMauTheCard(pnlTheTaoDanhMuc);

        pnlTheTaoDanhMuc.add(title);
        pnlTheTaoDanhMuc.add(lblTenDanhMuc);
        pnlTheTaoDanhMuc.add(txtTenDanhMuc);
        pnlTheTaoDanhMuc.add(lblMauSac);
        pnlTheTaoDanhMuc.add(btnChonMau);
        pnlTheTaoDanhMuc.add(pnlMau);
        pnlTheTaoDanhMuc.add(btnTao);
        pnlTheTaoDanhMuc.add(btnQuayLai);

        // Sử dụng GridBagLayout để khi phóng to toàn màn hình, thẻ luôn ở chính giữa
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheTaoDanhMuc);

        // Sự kiện mở bảng chọn màu
        btnChonMau.addActionListener(a -> {
            Color color = JColorChooser.showDialog(null, "Chọn màu sắc cho danh mục", selectedColor);
            if (color != null) {
                selectedColor = color;
                pnlMau.setBackground(selectedColor);
            }
        });

        // Sự kiện lưu danh mục mới vào cơ sở dữ liệu
        btnTao.addActionListener(a -> {
            String categoryName = txtTenDanhMuc.getText().trim();
            if (categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập tên danh mục!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String hexCode = "#" + Integer.toHexString(selectedColor.getRGB()).substring(2).toUpperCase();

            try (Connection con = DBConnection.getConnection()) {
                int userId = -1;
                String getSql = "SELECT id FROM users WHERE username = ?";
                try (PreparedStatement pst = con.prepareStatement(getSql)) {
                    pst.setString(1, username);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        userId = rs.getInt("id");
                    } else {
                        JOptionPane.showMessageDialog(null, "Không tìm thấy người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String sql = "INSERT INTO categories(user_id, name, color) VALUES(?, ?, ?)";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setInt(1, userId);
                    pst.setString(2, categoryName);
                    pst.setString(3, hexCode);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Tạo danh mục thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    new CreateCategory(username);
                    dispose();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Quay lại màn hình danh mục
        btnQuayLai.addActionListener(a -> {
            new Category(username);
            dispose();
        });

        getRootPane().setDefaultButton(btnTao);
        setVisible(true);
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Tạo Danh Mục Mới");
    }

    public static void main(String[] args) {
        new CreateCategory("Atharv");
    }
}
