package view;

import dao.DanhMucDAO;
import dao.GiaoDichDAO;
import dao.IDanhMucDAO;
import dao.IGiaoDichDAO;
import dao.INguoiDungDAO;
import dao.IViTienDAO;
import dao.NguoiDungDAO;
import dao.ViTienDAO;
import model.DanhMuc;
import model.GiaoDich;
import model.KhoanChi;
import model.KhoanThu;
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
 * Màn hình Thêm Giao Dịch Mới (ThemGiaoDichFrame).
 * Kế thừa từ BaseFrame, tích hợp bàn phím nhanh số tiền, tính năng xem trước thông minh
 * và chọn Ví thanh toán đa nguồn (Multi-Wallet).
 */
public class ThemGiaoDichFrame extends BaseFrame {
    private final String username;
    private final IDanhMucDAO danhMucDAO = new DanhMucDAO();
    private final IGiaoDichDAO giaoDichDAO = new GiaoDichDAO();
    private final IViTienDAO viTienDAO = new ViTienDAO();
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private int userId;

    private JComboBox<DanhMuc> cbDanhMuc;
    private JComboBox<ViTien> cbViTien;
    private JComboBox<String> cbLoai;
    private JTextField txtSoTien;
    private JTextField txtMoTa;
    private JLabel lblXemTruoc;
    private JButton btnLuu;
    private JButton btnQuayLai;
    private JPanel pnlNutNhanh;
    private JPanel pnlTheGiaoDich;

    public ThemGiaoDichFrame(String username) {
        super("Quản Lý Chi Tiêu - Thêm Giao Dịch Mới", 850, 650);
        this.username = username;
        this.userId = nguoiDungDAO.layIdTheoUsername(username);
        taiDanhSachDanhMuc();
        taiDanhSachVi();
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 16);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 17);

        JLabel title = new JLabel("Thêm Giao Dịch Mới", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);

        JLabel lblDanhMuc = new JLabel("Danh mục:");
        cbDanhMuc = new JComboBox<>();

        // Renderer hiển thị chấm màu đại diện cho danh mục
        cbDanhMuc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DanhMuc) {
                    DanhMuc dm = (DanhMuc) value;
                    lbl.setText(dm.getName());
                    try {
                        lbl.setForeground(Color.decode(dm.getColorHex()));
                    } catch (Exception e) {
                        lbl.setForeground(Color.BLACK);
                    }
                }
                return lbl;
            }
        });

        JLabel lblViTien = new JLabel("Ví thanh toán:");
        cbViTien = new JComboBox<>();

        JLabel lblLoai = new JLabel("Loại giao dịch:");
        cbLoai = new JComboBox<>(new String[]{"Chi tiêu", "Thu nhập"});

        JLabel lblSoTien = new JLabel("Số tiền (VNĐ):");
        txtSoTien = new JTextField(10);
        JLabel lblDonVi = new JLabel("VNĐ");
        lblDonVi.setForeground(new Color(0, 255, 180));
        lblDonVi.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblXemTruoc = new JLabel("Gõ tắt nhanh: 50k, 500k, 5tr, 2.5m hoặc bấm nút dưới");
        lblXemTruoc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblXemTruoc.setForeground(GiaoDienHelper.laGiaoDienToi() ? new Color(160, 200, 240) : new Color(14, 116, 144));

        // Hàng nút bấm số tiền nhanh
        pnlNutNhanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlNutNhanh.setOpaque(false);
        khoiTaoNutBamNhanh();

        JLabel lblMoTa = new JLabel("Mô tả:");
        txtMoTa = new JTextField(15);

        btnLuu = new JButton("Lưu giao dịch");
        btnLuu.setBackground(new Color(0, 153, 102));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setOpaque(true);

        btnQuayLai = new JButton("Quay lại");
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);

        title.setFont(fontTieuDe);
        lblDanhMuc.setFont(fontNhan);
        cbDanhMuc.setFont(fontNhan);
        lblViTien.setFont(fontNhan);
        cbViTien.setFont(fontNhan);
        lblLoai.setFont(fontNhan);
        cbLoai.setFont(fontNhan);
        lblSoTien.setFont(fontNhan);
        txtSoTien.setFont(fontNhan);
        lblMoTa.setFont(fontNhan);
        txtMoTa.setFont(fontNhan);
        btnLuu.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        title.setBounds(30, 15, 620, 40);
        int lblX = 50, inputX = 200, widthLbl = 140, widthInput = 350, heightInput = 34;

        lblDanhMuc.setBounds(lblX, 65, widthLbl, heightInput);
        cbDanhMuc.setBounds(inputX, 65, widthInput + 50, heightInput);

        lblViTien.setBounds(lblX, 110, widthLbl, heightInput);
        cbViTien.setBounds(inputX, 110, widthInput + 50, heightInput);

        lblLoai.setBounds(lblX, 155, widthLbl, heightInput);
        cbLoai.setBounds(inputX, 155, widthInput + 50, heightInput);

        lblSoTien.setBounds(lblX, 200, widthLbl, heightInput);
        txtSoTien.setBounds(inputX, 200, widthInput - 10, heightInput);
        lblDonVi.setBounds(inputX + widthInput, 200, 55, heightInput);

        lblXemTruoc.setBounds(inputX, 238, widthInput + 50, 22);
        pnlNutNhanh.setBounds(inputX, 262, 420, 32);

        lblMoTa.setBounds(lblX, 302, widthLbl, heightInput);
        txtMoTa.setBounds(inputX, 302, widthInput + 50, heightInput);

        btnLuu.setBounds(220, 355, 240, 42);
        btnQuayLai.setBounds(220, 408, 240, 42);

        pnlTheGiaoDich = new JPanel();
        pnlTheGiaoDich.setLayout(null);
        pnlTheGiaoDich.setPreferredSize(new Dimension(680, 475));
        GiaoDienHelper.apDungMauTheCard(pnlTheGiaoDich);

        pnlTheGiaoDich.add(title);
        pnlTheGiaoDich.add(lblDanhMuc);
        pnlTheGiaoDich.add(cbDanhMuc);
        pnlTheGiaoDich.add(lblViTien);
        pnlTheGiaoDich.add(cbViTien);
        pnlTheGiaoDich.add(lblLoai);
        pnlTheGiaoDich.add(cbLoai);
        pnlTheGiaoDich.add(lblSoTien);
        pnlTheGiaoDich.add(txtSoTien);
        pnlTheGiaoDich.add(lblDonVi);
        pnlTheGiaoDich.add(lblXemTruoc);
        pnlTheGiaoDich.add(pnlNutNhanh);
        pnlTheGiaoDich.add(lblMoTa);
        pnlTheGiaoDich.add(txtMoTa);
        pnlTheGiaoDich.add(btnLuu);
        pnlTheGiaoDich.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheGiaoDich);

        getRootPane().setDefaultButton(btnLuu);
    }

    private void khoiTaoNutBamNhanh() {
        String[] nhanNuts = {"+000", "+50k", "+100k", "+500k", "+1tr", "+5tr", "Xóa"};
        for (String nhan : nhanNuts) {
            JButton btn = new JButton(nhan);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setMargin(new Insets(2, 6, 2, 6));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> xuLyBamNutNhanh(nhan));
            pnlNutNhanh.add(btn);
        }
    }

    private void xuLyBamNutNhanh(String nhan) {
        String vanBanHienTai = txtSoTien.getText().trim();
        if ("Xóa".equals(nhan)) {
            txtSoTien.setText("");
            lblXemTruoc.setText("Gõ tắt nhanh: 50k, 500k, 5tr, 2.5m hoặc bấm nút dưới");
            txtSoTien.requestFocus();
            return;
        }

        if ("+000".equals(nhan)) {
            if (vanBanHienTai.isEmpty()) {
                txtSoTien.setText("1000");
            } else {
                try {
                    double giaTri = DinhDangTienHelper.chuyenDoiSoTien(vanBanHienTai);
                    long giaTriMoi = (long) (giaTri * 1000);
                    txtSoTien.setText(String.valueOf(giaTriMoi));
                } catch (Exception ex) {
                    txtSoTien.setText(vanBanHienTai + "000");
                }
            }
            capNhatNhanXemTruoc();
            txtSoTien.requestFocus();
            return;
        }

        double soTienThem = 0;
        switch (nhan) {
            case "+50k": soTienThem = 50_000; break;
            case "+100k": soTienThem = 100_000; break;
            case "+500k": soTienThem = 500_000; break;
            case "+1tr": soTienThem = 1_000_000; break;
            case "+5tr": soTienThem = 5_000_000; break;
        }

        double giaTriHienTai = 0;
        if (!vanBanHienTai.isEmpty()) {
            try {
                giaTriHienTai = DinhDangTienHelper.chuyenDoiSoTien(vanBanHienTai);
            } catch (Exception ignored) {}
        }

        long tongMoi = (long) (giaTriHienTai + soTienThem);
        txtSoTien.setText(String.valueOf(tongMoi));
        capNhatNhanXemTruoc();
        txtSoTien.requestFocus();
    }

    private void capNhatNhanXemTruoc() {
        String vanBan = txtSoTien.getText().trim();
        if (vanBan.isEmpty()) {
            lblXemTruoc.setText("Gõ tắt nhanh: 50k, 500k, 5tr, 2.5m hoặc bấm nút dưới");
            lblXemTruoc.setForeground(GiaoDienHelper.laGiaoDienToi() ? new Color(160, 200, 240) : new Color(14, 116, 144));
            return;
        }

        try {
            double soTien = DinhDangTienHelper.chuyenDoiSoTien(vanBan);
            if (soTien > 0) {
                String chuoiTien = DinhDangTienHelper.dinhDangVND(soTien);
                lblXemTruoc.setText("➜ Số tiền: " + chuoiTien);
                lblXemTruoc.setForeground(new Color(0, 230, 160));
            } else {
                lblXemTruoc.setText("Số tiền phải lớn hơn 0!");
                lblXemTruoc.setForeground(new Color(255, 120, 120));
            }
        } catch (NumberFormatException e) {
            lblXemTruoc.setText("Số tiền không hợp lệ (Ví dụ đúng: 50k, 500k, 5tr)");
            lblXemTruoc.setForeground(new Color(255, 120, 120));
        }
    }

    @Override
    protected void dangKySuKien() {
        txtSoTien.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { capNhatNhanXemTruoc(); }
            @Override
            public void removeUpdate(DocumentEvent e) { capNhatNhanXemTruoc(); }
            @Override
            public void changedUpdate(DocumentEvent e) { capNhatNhanXemTruoc(); }
        });

        txtSoTien.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String vanBan = txtSoTien.getText().trim();
                if (!vanBan.isEmpty()) {
                    try {
                        double giaTri = DinhDangTienHelper.chuyenDoiSoTien(vanBan);
                        if (giaTri > 0) {
                            txtSoTien.setText(DinhDangTienHelper.dinhDangSo(giaTri));
                        }
                    } catch (Exception ignored) {}
                }
            }
        });

        btnLuu.addActionListener(e -> xuLyLuuGiaoDich());
        btnQuayLai.addActionListener(e -> chuyenManHinh(new TrangChuFrame(username)));
    }

    private void taiDanhSachDanhMuc() {
        danhMucDAO.khoiTaoDanhMucNeuChuaCo(username);
        List<DanhMuc> ds = danhMucDAO.layDanhSachTheoUsername(username);
        cbDanhMuc.removeAllItems();
        for (DanhMuc dm : ds) {
            cbDanhMuc.addItem(dm);
        }
    }

    private void taiDanhSachVi() {
        if (userId <= 0) {
            userId = nguoiDungDAO.layIdTheoUsername(username);
        }
        viTienDAO.khoiTaoViMacDinh(userId);
        List<ViTien> dsVi = viTienDAO.layDanhSachTheoUserId(userId);
        cbViTien.removeAllItems();
        for (ViTien v : dsVi) {
            cbViTien.addItem(v);
        }
    }

    private void xuLyLuuGiaoDich() {
        DanhMuc selected = (DanhMuc) cbDanhMuc.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng tạo danh mục trước khi thêm giao dịch!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ViTien viChon = (ViTien) cbViTien.getSelectedItem();
        String catName = selected.getName().toLowerCase();
        String loaiDaChon = (String) cbLoai.getSelectedItem();
        String moTa = txtMoTa.getText().trim();
        String soTienStr = txtSoTien.getText().trim();

        if (moTa.isEmpty() || soTienStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ mô tả và số tiền!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = DinhDangTienHelper.chuyenDoiSoTien(soTienStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Số tiền phải lớn hơn 0!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số tiền hợp lệ! (Ví dụ: 50k, 500k, 5tr)",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tạo đối tượng đa hình (KhoanThu hoặc KhoanChi)
        GiaoDich giaoDichMoi;
        if ("Thu nhập".equals(loaiDaChon)) {
            giaoDichMoi = new KhoanThu(username, catName, moTa, amount);
        } else {
            giaoDichMoi = new KhoanChi(username, catName, moTa, amount);
        }

        // Gán ví thanh toán
        if (viChon != null) {
            giaoDichMoi.setViId(viChon.getId());
            giaoDichMoi.setTenVi(viChon.getTenVi());
        }

        // Lưu thông qua DAO (DAO sẽ tự động cập nhật số dư ví)
        boolean thanhCong = giaoDichDAO.them(giaoDichMoi);
        if (thanhCong) {
            String thongBaoVi = (viChon != null) ? "\nVí: " + viChon.getTenVi() : "";
            JOptionPane.showMessageDialog(this,
                    "Thêm giao dịch (" + giaoDichMoi.getLoaiHienThi() + ") thành công!" + thongBaoVi,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            txtSoTien.setText("");
            txtMoTa.setText("");
            txtSoTien.requestFocus();
            // Nạp lại danh sách ví để cập nhật số dư mới
            taiDanhSachVi();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi thêm giao dịch vào CSDL!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
