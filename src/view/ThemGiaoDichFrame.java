package view;

import dao.DanhMucDAO;
import dao.GiaoDichDAO;
import dao.IDanhMucDAO;
import dao.IGiaoDichDAO;
import model.DanhMuc;
import model.GiaoDich;
import model.KhoanChi;
import model.KhoanThu;
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
 * Kế thừa từ BaseFrame, tích hợp bàn phím nhanh số tiền và tính năng xem trước thông minh.
 */
public class ThemGiaoDichFrame extends BaseFrame {
    private final String username;
    private final IDanhMucDAO danhMucDAO = new DanhMucDAO();
    private final IGiaoDichDAO giaoDichDAO = new GiaoDichDAO();

    private JComboBox<DanhMuc> cbDanhMuc;
    private JComboBox<String> cbLoai;
    private JTextField txtSoTien;
    private JTextField txtMoTa;
    private JLabel lblXemTruoc;
    private JButton btnLuu;
    private JButton btnQuayLai;
    private JPanel pnlNutNhanh;
    private JPanel pnlTheGiaoDich;

    public ThemGiaoDichFrame(String username) {
        super("Quản Lý Chi Tiêu - Thêm Giao Dịch Mới", 850, 600);
        this.username = username;
        taiDanhSachDanhMuc();
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 30);
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 18);

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
        lblLoai.setFont(fontNhan);
        cbLoai.setFont(fontNhan);
        lblSoTien.setFont(fontNhan);
        txtSoTien.setFont(fontNhan);
        lblMoTa.setFont(fontNhan);
        txtMoTa.setFont(fontNhan);
        btnLuu.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        title.setBounds(30, 20, 620, 45);
        int lblX = 60, inputX = 210, widthLbl = 140, widthInput = 340, heightInput = 36;

        lblDanhMuc.setBounds(lblX, 75, widthLbl, heightInput);
        cbDanhMuc.setBounds(inputX, 75, widthInput + 50, heightInput);

        lblLoai.setBounds(lblX, 125, widthLbl, heightInput);
        cbLoai.setBounds(inputX, 125, widthInput + 50, heightInput);

        lblSoTien.setBounds(lblX, 175, widthLbl, heightInput);
        txtSoTien.setBounds(inputX, 175, widthInput - 10, heightInput);
        lblDonVi.setBounds(inputX + widthInput, 175, 55, heightInput);

        lblXemTruoc.setBounds(inputX, 215, widthInput + 50, 22);
        pnlNutNhanh.setBounds(inputX, 240, 410, 32);

        lblMoTa.setBounds(lblX, 282, widthLbl, heightInput);
        txtMoTa.setBounds(inputX, 282, widthInput + 50, heightInput);

        btnLuu.setBounds(220, 340, 240, 42);
        btnQuayLai.setBounds(220, 395, 240, 42);

        pnlTheGiaoDich = new JPanel();
        pnlTheGiaoDich.setLayout(null);
        pnlTheGiaoDich.setPreferredSize(new Dimension(680, 465));
        GiaoDienHelper.apDungMauTheCard(pnlTheGiaoDich);

        pnlTheGiaoDich.add(title);
        pnlTheGiaoDich.add(lblDanhMuc);
        pnlTheGiaoDich.add(cbDanhMuc);
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
        boolean laToi = GiaoDienHelper.laGiaoDienToi();
        String[] danhSachNut = {"+000", "+50k", "+100k", "+500k", "+1tr", "+5tr", "Xóa"};
        for (String nut : danhSachNut) {
            JButton btnNhanh = new JButton(nut);
            btnNhanh.setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (laToi) {
                btnNhanh.setForeground(nut.equals("Xóa") ? new Color(255, 120, 120) : new Color(220, 240, 255));
                btnNhanh.setBackground(new Color(50, 52, 58));
                btnNhanh.setBorderPainted(false);
            } else {
                btnNhanh.setForeground(nut.equals("Xóa") ? new Color(220, 38, 38) : new Color(30, 41, 59));
                btnNhanh.setBackground(new Color(241, 245, 249));
                btnNhanh.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
            }
            btnNhanh.setFocusPainted(false);
            btnNhanh.setOpaque(true);
            btnNhanh.setMargin(new Insets(3, 8, 3, 8));
            btnNhanh.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnNhanh.addActionListener(e -> {
                String chuoiHienTai = txtSoTien.getText().trim();
                if (nut.equals("Xóa")) {
                    txtSoTien.setText("");
                    txtSoTien.requestFocus();
                    return;
                }
                if (nut.equals("+000")) {
                    if (chuoiHienTai.isEmpty() || chuoiHienTai.equals("0")) {
                        txtSoTien.setText("1,000");
                    } else {
                        try {
                            double giaTri = DinhDangTienHelper.chuyenDoiSoTien(chuoiHienTai);
                            txtSoTien.setText(DinhDangTienHelper.dinhDangSo(giaTri * 1000));
                        } catch (Exception ex) {
                            txtSoTien.setText(chuoiHienTai + "000");
                        }
                    }
                } else {
                    double soTienCong = 0;
                    switch (nut) {
                        case "+50k": soTienCong = 50_000; break;
                        case "+100k": soTienCong = 100_000; break;
                        case "+500k": soTienCong = 500_000; break;
                        case "+1tr": soTienCong = 1_000_000; break;
                        case "+5tr": soTienCong = 5_000_000; break;
                    }
                    double goc = 0;
                    try {
                        if (!chuoiHienTai.isEmpty()) {
                            goc = DinhDangTienHelper.chuyenDoiSoTien(chuoiHienTai);
                        }
                    } catch (Exception ignored) {}
                    txtSoTien.setText(DinhDangTienHelper.dinhDangSo(goc + soTienCong));
                }
                txtSoTien.requestFocus();
            });
            pnlNutNhanh.add(btnNhanh);
        }
    }

    @Override
    protected void dangKySuKien() {
        // Cập nhật nhãn xem trước thời gian thực
        Runnable capNhatXemTruoc = () -> {
            String text = txtSoTien.getText().trim();
            if (text.isEmpty()) {
                lblXemTruoc.setText("Gõ tắt nhanh: 50k, 500k, 5tr, 2.5m hoặc bấm nút dưới");
                lblXemTruoc.setForeground(GiaoDienHelper.laGiaoDienToi() ? new Color(160, 200, 240) : new Color(14, 116, 144));
            } else {
                try {
                    double giaTri = DinhDangTienHelper.chuyenDoiSoTien(text);
                    if (giaTri > 0) {
                        lblXemTruoc.setText("≈ " + DinhDangTienHelper.dinhDangTienVND(giaTri));
                        lblXemTruoc.setForeground(new Color(50, 255, 160));
                    } else {
                        lblXemTruoc.setText("Số tiền phải lớn hơn 0");
                        lblXemTruoc.setForeground(Color.ORANGE);
                    }
                } catch (Exception ex) {
                    lblXemTruoc.setText("Số tiền chưa hợp lệ");
                    lblXemTruoc.setForeground(new Color(255, 120, 120));
                }
            }
        };

        txtSoTien.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { capNhatXemTruoc.run(); }
            @Override public void removeUpdate(DocumentEvent e) { capNhatXemTruoc.run(); }
            @Override public void changedUpdate(DocumentEvent e) { capNhatXemTruoc.run(); }
        });

        // Tự động định dạng phân cách khi rời ô nhập
        txtSoTien.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String text = txtSoTien.getText().trim();
                if (!text.isEmpty()) {
                    try {
                        double giaTri = DinhDangTienHelper.chuyenDoiSoTien(text);
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

    private void xuLyLuuGiaoDich() {
        DanhMuc selected = (DanhMuc) cbDanhMuc.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng tạo danh mục trước khi thêm giao dịch!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

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

        // Lưu thông qua DAO
        boolean thanhCong = giaoDichDAO.them(giaoDichMoi);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this,
                    "Thêm giao dịch (" + giaoDichMoi.getLoaiHienThi() + ") thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            txtSoTien.setText("");
            txtMoTa.setText("");
            txtSoTien.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi thêm giao dịch vào CSDL!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

