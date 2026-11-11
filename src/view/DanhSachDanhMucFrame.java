package view;

import dao.DanhMucDAO;
import dao.IDanhMucDAO;
import model.DanhMuc;
import util.GiaoDienHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Danh Sách Danh Mục (DanhSachDanhMucFrame).
 * Cho phép xem bảng, chỉnh sửa tên/mã màu trực tiếp và xóa danh mục.
 */
public class DanhSachDanhMucFrame extends BaseFrame {
    private final String username;
    private final IDanhMucDAO danhMucDAO = new DanhMucDAO();
    private final List<Integer> categoryIds = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnQuayLai;

    public DanhSachDanhMucFrame(String username) {
        super("Danh Sách Danh Mục - " + username, 850, 560);
        this.username = username;
        taiDanhSachDanhMuc();
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 24);
        Font fontBang = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 15);

        JLabel title = new JLabel("Danh Sách Danh Mục - " + username, JLabel.CENTER);
        title.setBackground(new Color(0, 102, 204));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setFont(fontTieuDe);
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        String[] columnNames = {"Tên danh mục", "Mã màu", "Lưu", "Xóa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 1; // Cho phép sửa trực tiếp tên và màu
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(fontBang);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(224, 224, 224));

        // Đổi màu tên danh mục theo màu tương ứng
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Object colorVal = table.getValueAt(row, 1);
                if (colorVal != null) {
                    try {
                        c.setForeground(Color.decode(colorVal.toString()));
                    } catch (Exception e) {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        // Căn giữa cột Lưu và Xóa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);

        btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setFont(fontNut);
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.add(title, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        bottomPanel.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new BorderLayout(20, 20));
        c.add(topPanel, BorderLayout.NORTH);
        c.add(scrollPane, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void dangKySuKien() {
        btnQuayLai.addActionListener(e -> chuyenManHinh(new QuanLyDanhMucFrame(username)));

        // Xử lý click cột Lưu (cột 2) và cột Xóa (cột 3)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0 || row >= categoryIds.size()) return;

                int categoryId = categoryIds.get(row);
                String currentName = table.getValueAt(row, 0).toString().trim();
                String currentColor = table.getValueAt(row, 1).toString().trim();

                // Click cột Lưu
                if (col == 2) {
                    if (currentName.isEmpty() || currentColor.isEmpty()) {
                        JOptionPane.showMessageDialog(DanhSachDanhMucFrame.this,
                                "Tên và mã màu không được để trống!",
                                "Cảnh báo",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    DanhMuc dm = new DanhMuc(categoryId, 0, currentName, currentColor);
                    boolean ok = danhMucDAO.capNhat(dm);
                    if (ok) {
                        JOptionPane.showMessageDialog(DanhSachDanhMucFrame.this,
                                "Cập nhật danh mục thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                        taiDanhSachDanhMuc();
                    } else {
                        JOptionPane.showMessageDialog(DanhSachDanhMucFrame.this,
                                "Cập nhật danh mục thất bại!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Click cột Xóa
                if (col == 3) {
                    int confirm = JOptionPane.showConfirmDialog(DanhSachDanhMucFrame.this,
                            "Bạn có chắc muốn xóa danh mục '" + currentName + "' không?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean ok = danhMucDAO.xoa(categoryId);
                        if (ok) {
                            JOptionPane.showMessageDialog(DanhSachDanhMucFrame.this,
                                    "Đã xóa danh mục thành công!",
                                    "Thông báo",
                                    JOptionPane.INFORMATION_MESSAGE);
                            taiDanhSachDanhMuc();
                        } else {
                            JOptionPane.showMessageDialog(DanhSachDanhMucFrame.this,
                                    "Xóa danh mục thất bại!",
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void taiDanhSachDanhMuc() {
        tableModel.setRowCount(0);
        categoryIds.clear();

        List<DanhMuc> ds = danhMucDAO.layDanhSachTheoUsername(username);
        for (DanhMuc dm : ds) {
            tableModel.addRow(new Object[]{dm.getName(), dm.getColorHex(), "Lưu", "Xóa"});
            categoryIds.add(dm.getId());
        }
    }
}

