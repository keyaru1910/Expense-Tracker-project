package view;

import dao.INguoiDungDAO;
import dao.IViTienDAO;
import dao.NguoiDungDAO;
import dao.ViTienDAO;
import model.ViTien;
import util.DinhDangTienHelper;
import util.GiaoDienHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

/**
 * Màn hình Chuyển Tiền Giữa Các Ví (ChuyenTienViFrame).
 * Kế thừa từ BaseFrame, hỗ trợ người dùng luân chuyển tiền giữa các ví (Ví dụ: Rút tiền ngân hàng về tiền mặt).
 */
public class ChuyenTienViFrame extends BaseFrame {
    private final String username;
    private final Runnable onChuyenTienThanhCong;
    private final IViTienDAO viTienDAO = new ViTienDAO();
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private int userId;

    private JComboBox<ViTien> cbViNguon;
    private JComboBox<ViTien> cbViDich;
    private JTextField txtSoTien;
    private JLabel lblXemTruoc;
    private JTextField txtGhiChu;
    private JButton btnChuyenTien;
    private JButton btnHuy;
    private JPanel pnlNutNhanh;
    private JPanel pnlTheCard;

    public ChuyenTienViFrame(String username, Runnable onChuyenTienThanhCong) {
        super("Chuyển Tiền Giữa Các Ví - " + username, 680, 560);
        this.username = username;
        this.onChuyenTienThanhCong = onChuyenTienThanhCong;
        this.userId = nguoiDungDAO.layIdTheoUsername(username);
        taiDanhSachVi();
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 24);
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 16);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);

        JLabel title = new JLabel("Chuyển Tiền Giữa Các Ví", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);
        title.setFont(fontTieuDe);

        JLabel lblViNguon = new JLabel("Từ ví (Nguồn):");
        cbViNguon = new JComboBox<>();

        JLabel lblViDich = new JLabel("Đến ví (Đích):");
        cbViDich = new JComboBox<>();

        JLabel lblSoTien = new JLabel("Số tiền chuyển (VNĐ):");
        txtSoTien = new JTextField(12);

        lblXemTruoc = new JLabel("Gõ tắt: 50k, 500k, 2tr hoặc bấm nút dưới");
        lblXemTruoc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblXemTruoc.setForeground(GiaoDienHelper.laGiaoDienToi() ? new Color(160, 200, 240) : new Color(14, 116, 144));

        pnlNutNhanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlNutNhanh.setOpaque(false);
        khoiTaoNutBamNhanh();

        JLabel lblGhiChu = new JLabel("Nội dung chuyển:");
        txtGhiChu = new JTextField(20);

        btnChuyenTien = new JButton("Xác nhận chuyển");
        btnChuyenTien.setBackground(new Color(0, 153, 102));
        btnChuyenTien.setForeground(Color.WHITE);
        btnChuyenTien.setFont(fontNut);

        btnHuy = new JButton("Quay lại");
        btnHuy.setBackground(new Color(108, 117, 125));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFont(fontNut);

        pnlTheCard = new JPanel(null);
        pnlTheCard.setPreferredSize(new Dimension(580, 480));
        GiaoDienHelper.apDungMauTheCard(pnlTheCard);

        title.setBounds(0, 15, 580, 35);
        lblViNguon.setBounds(50, 65, 160, 30);
        cbViNguon.setBounds(210, 65, 320, 32);

        lblViDich.setBounds(50, 110, 160, 30);
        cbViDich.setBounds(210, 110, 320, 32);

        lblSoTien.setBounds(50, 155, 160, 30);
        txtSoTien.setBounds(210, 155, 320, 32);

        lblXemTruoc.setBounds(210, 190, 320, 22);
        pnlNutNhanh.setBounds(210, 215, 330, 40);

        lblGhiChu.setBounds(50, 265, 160, 30);
        txtGhiChu.setBounds(210, 265, 320, 32);

        btnChuyenTien.setBounds(110, 335, 180, 42);
        btnHuy.setBounds(310, 335, 160, 42);

        lblViNguon.setFont(fontNhan);
        cbViNguon.setFont(fontNhan);
        lblViDich.setFont(fontNhan);
        cbViDich.setFont(fontNhan);
        lblSoTien.setFont(fontNhan);
        txtSoTien.setFont(fontNhan);
        lblGhiChu.setFont(fontNhan);
        txtGhiChu.setFont(fontNhan);

        pnlTheCard.add(title);
        pnlTheCard.add(lblViNguon);
        pnlTheCard.add(cbViNguon);
        pnlTheCard.add(lblViDich);
        pnlTheCard.add(cbViDich);
        pnlTheCard.add(lblSoTien);
        pnlTheCard.add(txtSoTien);
        pnlTheCard.add(lblXemTruoc);
        pnlTheCard.add(pnlNutNhanh);
        pnlTheCard.add(lblGhiChu);
        pnlTheCard.add(txtGhiChu);
        pnlTheCard.add(btnChuyenTien);
        pnlTheCard.add(btnHuy);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheCard);
    }

    private void khoiTaoNutBamNhanh() {
        String[] nhanNuts = {"+50k", "+100k", "+500k", "+1tr", "+5tr", "Xóa"};
        for (String nhan : nhanNuts) {
            JButton btn = new JButton(nhan);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setMargin(new Insets(2, 6, 2, 6));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> xuLyNutNhanh(nhan));
            pnlNutNhanh.add(btn);
        }
    }

    private void xuLyNutNhanh(String nhan) {
        if ("Xóa".equals(nhan)) {
            txtSoTien.setText("");
            lblXemTruoc.setText("Gõ tắt: 50k, 500k, 2tr hoặc bấm nút dưới");
            return;
        }
        double hienTai = DinhDangTienHelper.chuyenDoiGoTat(txtSoTien.getText().trim());
        double them = 0;
        switch (nhan) {
            case "+50k": them = 50_000; break;
            case "+100k": them = 100_000; break;
            case "+500k": them = 500_000; break;
            case "+1tr": them = 1_000_000; break;
            case "+5tr": them = 5_000_000; break;
        }
        double tong = hienTai + them;
        txtSoTien.setText(String.format("%.0f", tong));
        capNhatXemTruoc();
    }

    private void capNhatXemTruoc() {
        String text = txtSoTien.getText().trim();
        if (text.isEmpty()) {
            lblXemTruoc.setText("Gõ tắt: 50k, 500k, 2tr hoặc bấm nút dưới");
            return;
        }
        double soTien = DinhDangTienHelper.chuyenDoiGoTat(text);
        if (soTien > 0) {
            lblXemTruoc.setText("➜ Số tiền: " + DinhDangTienHelper.dinhDangVND(soTien));
        } else {
            lblXemTruoc.setText("Số tiền không hợp lệ!");
        }
    }

    private void taiDanhSachVi() {
        cbViNguon.removeAllItems();
        cbViDich.removeAllItems();
        List<ViTien> danhSach = viTienDAO.layDanhSachTheoUserId(userId);
        if (danhSach.isEmpty()) {
            viTienDAO.khoiTaoViMacDinh(userId);
            danhSach = viTienDAO.layDanhSachTheoUserId(userId);
        }
        for (ViTien v : danhSach) {
            cbViNguon.addItem(v);
            cbViDich.addItem(v);
        }
        if (cbViDich.getItemCount() > 1) {
            cbViDich.setSelectedIndex(1);
        }
    }

    @Override
    protected void dangKySuKien() {
        txtSoTien.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { capNhatXemTruoc(); }
            public void removeUpdate(DocumentEvent e) { capNhatXemTruoc(); }
            public void changedUpdate(DocumentEvent e) { capNhatXemTruoc(); }
        });

        txtSoTien.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                double tien = DinhDangTienHelper.chuyenDoiGoTat(txtSoTien.getText().trim());
                if (tien > 0) {
                    txtSoTien.setText(String.format("%.0f", tien));
                }
            }
        });

        btnChuyenTien.addActionListener(e -> xuLyChuyenTien());
        btnHuy.addActionListener(e -> dispose());
    }

    private void xuLyChuyenTien() {
        ViTien viNguon = (ViTien) cbViNguon.getSelectedItem();
        ViTien viDich = (ViTien) cbViDich.getSelectedItem();

        if (viNguon == null || viDich == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ví nguồn và ví đích!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (viNguon.getId() == viDich.getId()) {
            JOptionPane.showMessageDialog(this, "Ví nguồn và ví đích phải khác nhau!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double soTien = DinhDangTienHelper.chuyenDoiGoTat(txtSoTien.getText().trim());
        if (soTien <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (viNguon.getSoDu() < soTien) {
            JOptionPane.showMessageDialog(this,
                    "Số dư trong ví [" + viNguon.getTenVi() + "] không đủ!\n" +
                    "Số dư hiện tại: " + viNguon.getSoDuDinhDang() + "\n" +
                    "Số tiền cần chuyển: " + DinhDangTienHelper.dinhDangVND(soTien),
                    "Không đủ số dư",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ghiChu = txtGhiChu.getText().trim();
        boolean thanhCong = viTienDAO.chuyenTien(viNguon.getId(), viDich.getId(), soTien, ghiChu, username);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this,
                    "Chuyển thành công " + DinhDangTienHelper.dinhDangVND(soTien) + "\n" +
                    "Từ: " + viNguon.getTenVi() + " ➔ Đến: " + viDich.getTenVi(),
                    "Chuyển tiền thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            if (onChuyenTienThanhCong != null) {
                onChuyenTienThanhCong.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Chuyển tiền thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

