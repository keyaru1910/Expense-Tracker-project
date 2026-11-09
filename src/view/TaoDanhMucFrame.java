package view;

import dao.DanhMucDAO;
import dao.IDanhMucDAO;
import dao.INguoiDungDAO;
import dao.NguoiDungDAO;
import model.DanhMuc;
import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình Tạo Danh Mục Mới (TaoDanhMucFrame).
 * Cho phép người dùng đặt tên danh mục và tùy chọn màu sắc trực quan qua JColorChooser.
 */
public class TaoDanhMucFrame extends BaseFrame {
    private final String username;
    private final IDanhMucDAO danhMucDAO = new DanhMucDAO();
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    private JTextField txtTenDanhMuc;
    private JPanel pnlMau;
    private JButton btnChonMau;
    private JButton btnTao;
    private JButton btnQuayLai;
    private Color selectedColor = Color.RED;

    public TaoDanhMucFrame(String username) {
        super("Tạo Danh Mục Mới", 800, 500);
        this.username = username;
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);

        JLabel title = new JLabel("Tạo Danh Mục Mới", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);
        title.setFont(fontTieuDe);

        JLabel lblTenDanhMuc = new JLabel("Tên danh mục:");
        txtTenDanhMuc = new JTextField(15);
        JLabel lblMauSac = new JLabel("Màu danh mục:");
        btnChonMau = new JButton("Chọn màu");
        GiaoDienHelper.apDungMauNutChucNang(btnChonMau);

        pnlMau = new JPanel();
        pnlMau.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pnlMau.setPreferredSize(new Dimension(35, 35));
        pnlMau.setBackground(selectedColor);

        btnTao = new JButton("Tạo danh mục");
        btnTao.setBackground(new Color(0, 153, 102));
        btnTao.setForeground(Color.WHITE);
        btnTao.setFocusPainted(false);
        btnTao.setBorderPainted(false);
        btnTao.setOpaque(true);

        btnQuayLai = new JButton("Quay lại");
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

        JPanel pnlThe = new JPanel();
        pnlThe.setLayout(null);
        pnlThe.setPreferredSize(new Dimension(620, 360));
        GiaoDienHelper.apDungMauTheCard(pnlThe);

        pnlThe.add(title);
        pnlThe.add(lblTenDanhMuc);
        pnlThe.add(txtTenDanhMuc);
        pnlThe.add(lblMauSac);
        pnlThe.add(btnChonMau);
        pnlThe.add(pnlMau);
        pnlThe.add(btnTao);
        pnlThe.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlThe);

        getRootPane().setDefaultButton(btnTao);
    }

    @Override
    protected void dangKySuKien() {
        // Mở bảng chọn màu trực quan
        btnChonMau.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Chọn màu đại diện cho danh mục", selectedColor);
            if (chosen != null) {
                selectedColor = chosen;
                pnlMau.setBackground(selectedColor);
            }
        });

        btnTao.addActionListener(e -> xuLyTaoDanhMuc());
        btnQuayLai.addActionListener(e -> chuyenManHinh(new QuanLyDanhMucFrame(username)));
    }

    private void xuLyTaoDanhMuc() {
        String tenDm = txtTenDanhMuc.getText().trim();
        if (tenDm.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên danh mục!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = nguoiDungDAO.layIdTheoUsername(username);
        if (userId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy người dùng hiện tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hexColor = String.format("#%02x%02x%02x", selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
        DanhMuc dm = new DanhMuc(0, userId, tenDm, hexColor);

        boolean ok = danhMucDAO.them(dm);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Tạo danh mục mới thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            txtTenDanhMuc.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tạo danh mục. Có thể tên danh mục đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

