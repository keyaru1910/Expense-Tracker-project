package model;

import javax.swing.*;
import java.awt.*;

public class Category extends JFrame {
    public Category(String username) {
        // Phông chữ Segoe UI hỗ trợ tiếng Việt Unicode
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);

        JLabel title = new JLabel("Quản Lý Danh Mục", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);

        JButton btnTaoDanhMuc = new JButton("+ Tạo danh mục mới");
        JButton btnXemDanhMuc = new JButton("Xem danh sách danh mục");
        JButton btnQuayLai = new JButton("Quay lại");

        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);
        GiaoDienHelper.apDungMauNutChucNang(btnTaoDanhMuc);
        GiaoDienHelper.apDungMauNutChucNang(btnXemDanhMuc);

        title.setFont(fontTieuDe);
        btnTaoDanhMuc.setFont(fontNut);
        btnXemDanhMuc.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        title.setBounds(10, 25, 600, 45);
        int btnX = 140, btnWidth = 340, btnHeight = 46;
        btnTaoDanhMuc.setBounds(btnX, 105, btnWidth, btnHeight);
        btnXemDanhMuc.setBounds(btnX, 170, btnWidth, btnHeight);
        btnQuayLai.setBounds(btnX, 235, btnWidth, btnHeight);

        // Thẻ quản lý danh mục dạng Card căn giữa màn hình
        JPanel pnlTheDanhMuc = new JPanel();
        pnlTheDanhMuc.setLayout(null);
        pnlTheDanhMuc.setPreferredSize(new Dimension(620, 340));
        GiaoDienHelper.apDungMauTheCard(pnlTheDanhMuc);

        pnlTheDanhMuc.add(title);
        pnlTheDanhMuc.add(btnTaoDanhMuc);
        pnlTheDanhMuc.add(btnXemDanhMuc);
        pnlTheDanhMuc.add(btnQuayLai);

        // Sử dụng GridBagLayout để khi phóng to toàn màn hình, menu luôn nằm chính giữa
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheDanhMuc);

        // Sự kiện chuyển đến màn hình tạo danh mục
        btnTaoDanhMuc.addActionListener(a -> {
            new CreateCategory(username);
            dispose();
        });

        // Sự kiện chuyển đến màn hình xem danh mục
        btnXemDanhMuc.addActionListener(a -> {
            new ViewCategory(username);
            dispose();
        });

        // Sự kiện quay lại trang chủ
        btnQuayLai.addActionListener(a -> {
            new Home(username);
            dispose();
        });

        setVisible(true);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Quản Lý Danh Mục");
    }

    public static void main(String[] args) {
        new Category("Atharv");
    }
}
