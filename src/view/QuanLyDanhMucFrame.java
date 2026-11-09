package view;

import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình Quản Lý Danh Mục (QuanLyDanhMucFrame).
 * Cung cấp menu lựa chọn Tạo danh mục mới hoặc Xem danh sách danh mục hiện có.
 */
public class QuanLyDanhMucFrame extends BaseFrame {
    private final String username;

    private JButton btnTaoDanhMuc;
    private JButton btnXemDanhMuc;
    private JButton btnQuayLai;

    public QuanLyDanhMucFrame(String username) {
        super("Quản Lý Danh Mục - " + username, 800, 500);
        this.username = username;
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);

        JLabel title = new JLabel("Quản Lý Danh Mục", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);

        btnTaoDanhMuc = new JButton("+ Tạo danh mục mới");
        btnXemDanhMuc = new JButton("Xem danh sách danh mục");
        btnQuayLai = new JButton("Quay lại");

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

        JPanel pnlTheDanhMuc = new JPanel();
        pnlTheDanhMuc.setLayout(null);
        pnlTheDanhMuc.setPreferredSize(new Dimension(620, 340));
        GiaoDienHelper.apDungMauTheCard(pnlTheDanhMuc);

        pnlTheDanhMuc.add(title);
        pnlTheDanhMuc.add(btnTaoDanhMuc);
        pnlTheDanhMuc.add(btnXemDanhMuc);
        pnlTheDanhMuc.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheDanhMuc);
    }

    @Override
    protected void dangKySuKien() {
        btnTaoDanhMuc.addActionListener(e -> chuyenManHinh(new TaoDanhMucFrame(username)));
        btnXemDanhMuc.addActionListener(e -> chuyenManHinh(new DanhSachDanhMucFrame(username)));
        btnQuayLai.addActionListener(e -> chuyenManHinh(new TrangChuFrame(username)));
    }
}

