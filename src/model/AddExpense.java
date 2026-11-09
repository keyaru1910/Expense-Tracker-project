package model;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddExpense extends JFrame {
    public AddExpense(String username) {
        // Phông chữ hỗ trợ tiếng Việt
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 30);
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 18);

        JLabel title = new JLabel("Thêm Giao Dịch Mới", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);

        JLabel lblDanhMuc = new JLabel("Danh mục:");
        JComboBox<CategoryItem> cbDanhMuc = new JComboBox<>();

        JLabel lblLoai = new JLabel("Loại giao dịch:");
        JComboBox<String> cbLoai = new JComboBox<>(new String[]{"Chi tiêu", "Thu nhập"});

        JLabel lblSoTien = new JLabel("Số tiền (VNĐ):");
        JTextField txtSoTien = new JTextField(10);
        JLabel lblDonVi = new JLabel("VNĐ");
        lblDonVi.setForeground(new Color(0, 255, 180));
        lblDonVi.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Nhãn xem trước số tiền quy đổi trực quan
        JLabel lblXemTruoc = new JLabel("Gõ tắt nhanh: 50k, 500k, 5tr, 2.5m hoặc bấm nút dưới");
        lblXemTruoc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblXemTruoc.setForeground(GiaoDienHelper.laGiaoDienToi() ? new Color(160, 200, 240) : new Color(14, 116, 144));

        // Thanh nút bấm nhanh tiện ích (không cần gõ nhiều số 0)
        JPanel pnlNutNhanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlNutNhanh.setOpaque(false);

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

        // Tự động cập nhật nhãn xem trước số tiền khi gõ
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

        txtSoTien.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { capNhatXemTruoc.run(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { capNhatXemTruoc.run(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { capNhatXemTruoc.run(); }
        });

        // Tự động định dạng phân cách hàng nghìn khi rời khỏi ô nhập tiền
        txtSoTien.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
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

        JLabel lblMoTa = new JLabel("Mô tả:");
        JTextField txtMoTa = new JTextField(15);

        JButton btnLuu = new JButton("Lưu giao dịch");
        btnLuu.setBackground(new Color(0, 153, 102));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setOpaque(true);

        JButton btnQuayLai = new JButton("Quay lại");
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

        // Thẻ thêm giao dịch dạng Card căn giữa màn hình
        JPanel pnlTheGiaoDich = new JPanel();
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

        // Sử dụng GridBagLayout để khi phóng to toàn màn hình, thẻ giao dịch luôn nằm chính giữa
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheGiaoDich);

        // Hiển thị màu danh mục trong danh sách lựa chọn
        cbDanhMuc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoryItem) {
                    CategoryItem item = (CategoryItem) value;
                    try {
                        comp.setForeground(Color.decode(item.colorHex));
                    } catch (Exception e) {
                        comp.setForeground(Color.BLACK);
                    }
                }
                return comp;
            }
        });

        // Đổi màu hiển thị: Thu nhập (Xanh), Chi tiêu (Đỏ)
        cbLoai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                try {
                    if ("Thu nhập".equals(value) || "Income".equals(value)) {
                        comp.setForeground(new Color(50, 225, 50));
                    } else {
                        comp.setForeground(Color.RED);
                    }
                } catch (Exception e) {
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        // Đảm bảo người dùng có sẵn danh mục mặc định nếu chưa có
        DanhMucHelper.khoiTaoDanhMucNeuChuaCo(username);

        // Tải danh sách danh mục của người dùng
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT name, color FROM categories WHERE user_id = (SELECT id FROM users WHERE username = ?)";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String color = rs.getString("color");
                    cbDanhMuc.addItem(new CategoryItem(name, color));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh mục: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sự kiện lưu giao dịch
        btnLuu.addActionListener(a -> {
            CategoryItem selected = (CategoryItem) cbDanhMuc.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(null, "Vui lòng tạo danh mục trước khi thêm giao dịch!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String catName = selected.name.toLowerCase();
            String loaiDaChon = cbLoai.getSelectedItem().toString();
            // Lưu vào DB chuẩn 'income' hoặc 'expense'
            String typeDb = "Thu nhập".equals(loaiDaChon) ? "income" : "expense";

            String moTa = txtMoTa.getText().trim();
            String soTienStr = txtSoTien.getText().trim();

            if (moTa.isEmpty() || soTienStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ mô tả và số tiền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = DinhDangTienHelper.chuyenDoiSoTien(soTienStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(null, "Số tiền phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập số tiền hợp lệ! (Ví dụ: 50k, 500k, 5tr, 5000000)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                String sql = "INSERT INTO transactions(username, category_name, description, amount, type) VALUES(?, ?, ?, ?, ?)";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, username);
                    pst.setString(2, catName);
                    pst.setString(3, moTa);
                    pst.setDouble(4, amount);
                    pst.setString(5, typeDb);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Thêm giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    txtSoTien.setText("");
                    txtMoTa.setText("");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi thêm giao dịch: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sự kiện quay lại trang chủ
        btnQuayLai.addActionListener(a -> {
            new Home(username);
            dispose();
        });

        getRootPane().setDefaultButton(btnLuu);
        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Thêm Giao Dịch Mới");
    }

    public static void main(String[] args) {
        new AddExpense("Atharv");
    }
}

class CategoryItem {
    String name;
    String colorHex;

    CategoryItem(String name, String colorHex) {
        this.name = name;
        this.colorHex = colorHex;
    }

    @Override
    public String toString() {
        return name;
    }
}
