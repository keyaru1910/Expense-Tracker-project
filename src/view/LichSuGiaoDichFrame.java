package view;

import dao.DanhMucDAO;
import dao.GiaoDichDAO;
import dao.IDanhMucDAO;
import dao.IGiaoDichDAO;
import model.DanhMuc;
import model.GiaoDich;
import util.DinhDangTienHelper;
import util.GiaoDienHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Lịch Sử Giao Dịch (LichSuGiaoDichFrame).
 * Kế thừa từ BaseFrame, hỗ trợ lọc, tìm kiếm, tính tổng tài chính,
 * cảnh báo số dư tối thiểu, chỉnh sửa và xóa giao dịch.
 */
public class LichSuGiaoDichFrame extends BaseFrame {
    private final String username;
    private final IGiaoDichDAO giaoDichDAO = new GiaoDichDAO();
    private final IDanhMucDAO danhMucDAO = new DanhMucDAO();

    private JLabel title;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> typeFilter;
    private JTextField searchField;
    private JLabel incLabel;
    private JLabel expLabel;
    private JLabel balLabel;
    private JLabel warningLabel;
    private JButton btnChinhSua;
    private JButton btnXoa;
    private JButton btnQuayLai;

    private final Map<String, String> categoryColors = new HashMap<>();

    public LichSuGiaoDichFrame(String username) {
        super("Lịch Sử Giao Dịch - " + (username != null ? username : ""), 1000, 650);
        this.username = username;
        if (title != null) {
            title.setText("Lịch Sử Giao Dịch - " + (username != null ? username : ""));
        }
        napMauDanhMuc();
        taiDuLieuLenBang("Tất cả", "");
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontNhanTongKet = new Font("Segoe UI", Font.BOLD, 15);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 15);

        title = new JLabel("Lịch Sử Giao Dịch", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setBackground(new Color(0, 102, 204));
        title.setOpaque(true);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.add(title, BorderLayout.CENTER);

        // Bảng dữ liệu JTable
        String[] columnNames = {"Danh mục", "Ví nguồn", "Mô tả", "Số tiền (VNĐ)", "Thời gian", "Loại", "id"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(224, 224, 224));

        // Ẩn cột id (cột số 6)
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);

        // Đổi màu cột Danh mục theo mã màu CSDL
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String catName = value.toString().toLowerCase();
                    String hexColor = categoryColors.getOrDefault(catName, "#000000");
                    try {
                        c.setForeground(Color.decode(hexColor));
                    } catch (Exception e) {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        // Đổi màu cột Loại: Thu nhập (xanh), Chi tiêu (đỏ)
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component co = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String type = value.toString().toLowerCase();
                    if (type.contains("thu nhập") || type.contains("income")) {
                        co.setForeground(new Color(34, 197, 94));
                    } else {
                        co.setForeground(new Color(239, 68, 68));
                    }
                }
                return co;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

        // Thanh công cụ lọc và tìm kiếm
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel lblLoai = new JLabel("Loại:");
        typeFilter = new JComboBox<>(new String[]{"Tất cả", "Thu nhập", "Chi tiêu"});
        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Tìm kiếm");

        filterPanel.add(lblLoai);
        filterPanel.add(typeFilter);
        filterPanel.add(lblTimKiem);
        filterPanel.add(searchField);
        filterPanel.add(searchBtn);

        searchBtn.addActionListener(e -> taiDuLieuLenBang((String) typeFilter.getSelectedItem(), searchField.getText()));
        typeFilter.addActionListener(e -> taiDuLieuLenBang((String) typeFilter.getSelectedItem(), searchField.getText()));

        // Hộp tổng kết tài chính và cảnh báo
        incLabel = new JLabel("Tổng thu nhập: 0 VNĐ");
        expLabel = new JLabel("Tổng chi tiêu: 0 VNĐ");
        balLabel = new JLabel("Số dư hiện tại: 0 VNĐ");
        warningLabel = new JLabel();

        incLabel.setFont(fontNhanTongKet);
        incLabel.setForeground(new Color(34, 197, 94));
        expLabel.setFont(fontNhanTongKet);
        expLabel.setForeground(new Color(239, 68, 68));
        balLabel.setFont(fontNhanTongKet);
        warningLabel.setFont(fontNhanTongKet);
        warningLabel.setForeground(new Color(255, 90, 90));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 12));
        summaryPanel.add(incLabel);
        summaryPanel.add(expLabel);
        summaryPanel.add(balLabel);
        summaryPanel.add(warningLabel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(summaryPanel, BorderLayout.SOUTH);

        // Các nút thao tác phía dưới
        btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setFont(fontNut);
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);

        btnChinhSua = new JButton("Chỉnh sửa");
        btnChinhSua.setFont(fontNut);
        btnChinhSua.setBackground(new Color(0, 153, 102));
        btnChinhSua.setForeground(Color.WHITE);
        btnChinhSua.setFocusPainted(false);
        btnChinhSua.setBorderPainted(false);
        btnChinhSua.setOpaque(true);

        btnXoa = new JButton("Xóa giao dịch");
        btnXoa.setFont(fontNut);
        btnXoa.setBackground(new Color(220, 38, 38));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.setBorderPainted(false);
        btnXoa.setOpaque(true);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        bottomPanel.add(btnChinhSua);
        bottomPanel.add(btnXoa);
        bottomPanel.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(topPanel, BorderLayout.NORTH);
        c.add(centerPanel, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void dangKySuKien() {
        btnQuayLai.addActionListener(e -> chuyenManHinh(new TrangChuFrame(username)));
        btnChinhSua.addActionListener(e -> xuLyChinhSua());
        btnXoa.addActionListener(e -> xuLyXoa());
    }

    private void napMauDanhMuc() {
        categoryColors.clear();
        List<DanhMuc> ds = danhMucDAO.layDanhSachTheoUsername(username);
        for (DanhMuc dm : ds) {
            categoryColors.put(dm.getName().toLowerCase(), dm.getColorHex());
        }
    }

    private void taiDuLieuLenBang(String loai, String tuKhoa) {
        tableModel.setRowCount(0);
        List<GiaoDich> ds = giaoDichDAO.layDanhSachGiaoDich(username, loai, tuKhoa);

        for (GiaoDich gd : ds) {
            tableModel.addRow(new Object[]{
                    gd.getCategoryName(),
                    gd.getTenVi(),
                    gd.getDescription(),
                    DinhDangTienHelper.dinhDangSo(gd.getAmount()),
                    gd.getDateTime(),
                    gd.getLoaiHienThi(),
                    gd.getId()
            });
        }

        capNhatTongKetTaiChinh();
    }

    private void capNhatTongKetTaiChinh() {
        double tongThu = giaoDichDAO.tinhTongThuNhap(username);
        double tongChi = giaoDichDAO.tinhTongChiTieu(username);
        double soDu = tongThu - tongChi;

        incLabel.setText("Tổng thu: " + DinhDangTienHelper.dinhDangTienVND(tongThu));
        expLabel.setText("Tổng chi: " + DinhDangTienHelper.dinhDangTienVND(tongChi));
        balLabel.setText("Số dư: " + DinhDangTienHelper.dinhDangTienVND(soDu));

        // Cảnh báo nếu số dư xuống thấp hoặc âm
        if (soDu < 0) {
            warningLabel.setText("CẢNH BÁO: Bội chi ngân sách!");
        } else if (soDu < 200_000 && tongChi > 0) {
            warningLabel.setText("CẢNH BÁO: Số dư dưới mức an toàn (< 200k)!");
        } else {
            warningLabel.setText("");
        }
    }

    private void xuLyChinhSua() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một giao dịch để chỉnh sửa!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int selectedId = Integer.parseInt(tableModel.getValueAt(modelRow, 6).toString());
        String oldDesc = table.getValueAt(row, 2).toString();
        String oldAmount = table.getValueAt(row, 3).toString();
        String oldDateTime = table.getValueAt(row, 4).toString();

        String newDesc = JOptionPane.showInputDialog(this, "Chỉnh sửa mô tả:", oldDesc);
        if (newDesc == null || newDesc.trim().isEmpty()) return;

        String newAmountStr = JOptionPane.showInputDialog(this, "Chỉnh sửa số tiền (VNĐ):", oldAmount);
        if (newAmountStr == null || newAmountStr.trim().isEmpty()) return;

        double parsedAmount;
        try {
            parsedAmount = DinhDangTienHelper.chuyenDoiSoTien(newAmountStr);
            if (parsedAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ! (Ví dụ: 50k, 500k, 5tr)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newDateTime = JOptionPane.showInputDialog(this, "Chỉnh sửa ngày giờ (yyyy-MM-dd HH:mm:ss):", oldDateTime);
        if (newDateTime == null || newDateTime.trim().isEmpty()) return;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            sdf.parse(newDateTime.trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày giờ sai định dạng! (Yêu cầu: yyyy-MM-dd HH:mm:ss)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GiaoDichDAO dao = (GiaoDichDAO) giaoDichDAO;
        boolean ok = dao.capNhatThongTinCoBan(selectedId, newDesc.trim(), parsedAmount, newDateTime.trim());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cập nhật giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuLenBang((String) typeFilter.getSelectedItem(), searchField.getText());
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật giao dịch thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoa() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một giao dịch để xóa!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int selectedId = Integer.parseInt(tableModel.getValueAt(modelRow, 6).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa giao dịch này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = giaoDichDAO.xoa(selectedId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuLenBang((String) typeFilter.getSelectedItem(), searchField.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Xóa giao dịch thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

