package model;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ViewCategory extends JFrame {
    private ArrayList<Integer> categoryIds = new ArrayList<>();

    public ViewCategory(String username) {
        // Phông chữ Segoe UI hỗ trợ tiếng Việt Unicode
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 24);
        Font fontBang = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 15);

        JLabel title = new JLabel("Danh sách danh mục của " + username, JLabel.CENTER);
        title.setBackground(new Color(0, 102, 204));
        title.setForeground(new Color(255, 255, 255));
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề các cột trong bảng
        String[] columnNames = {"Tên danh mục", "Mã màu", "Lưu", "Xóa"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 1;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(224, 224, 224));

        JScrollPane scrollPane = new JScrollPane(table);

        // Hiển thị màu chữ tên danh mục theo màu tương ứng
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String hexColor = (String) table.getValueAt(row, 1);
                try {
                    c.setForeground(Color.decode(hexColor));
                } catch (Exception e) {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        // Căn giữa cột Lưu và Xóa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JButton btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setBackground(new Color(70, 70, 70));
        btnQuayLai.setForeground(new Color(200, 200, 200));
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setOpaque(true);
        btnQuayLai.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnQuayLai.addActionListener(a -> {
            new Category(username);
            dispose();
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.add(title, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new BorderLayout(20, 20));
        c.add(topPanel, BorderLayout.NORTH);
        c.add(scrollPane, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);

        title.setFont(fontTieuDe);
        table.setFont(fontBang);
        btnQuayLai.setFont(fontNut);

        // Lấy danh sách danh mục từ CSDL
        try (Connection con = DBConnection.getConnection()) {
            int userId = -1;
            String getUserid = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(getUserid)) {
                pst.setString(1, username);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id");
                    }
                }
            }

            if (userId != -1) {
                String sql = "SELECT id, name, color FROM categories WHERE user_id = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setInt(1, userId);
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            String name = rs.getString("name");
                            String color = rs.getString("color");
                            categoryIds.add(rs.getInt("id"));
                            tableModel.addRow(new Object[]{name, color, "Lưu", "Xóa"});
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Bắt sự kiện bấm vào nút Lưu hoặc Xóa trong bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || row >= categoryIds.size()) return;
                int categoryId = categoryIds.get(row);

                if (col == 2) { // Cột Lưu
                    String newName = table.getValueAt(row, 0).toString().trim();
                    String newColor = table.getValueAt(row, 1).toString().trim();
                    if (newName.isEmpty() || newColor.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Tên danh mục và màu sắc không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    try (Connection con = DBConnection.getConnection()) {
                        String sql = "UPDATE categories SET name=?, color=? WHERE id=?";
                        try (PreparedStatement pst = con.prepareStatement(sql)) {
                            pst.setString(1, newName);
                            pst.setString(2, newColor);
                            pst.setInt(3, categoryId);
                            pst.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Đã cập nhật danh mục thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Lỗi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }

                } else if (col == 3) { // Cột Xóa
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa danh mục này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection con = DBConnection.getConnection()) {
                            String delSql = "DELETE FROM categories WHERE id=?";
                            try (PreparedStatement pst = con.prepareStatement(delSql)) {
                                pst.setInt(1, categoryId);
                                pst.executeUpdate();
                                ((DefaultTableModel) table.getModel()).removeRow(row);
                                categoryIds.remove(row);
                                JOptionPane.showMessageDialog(null, "Đã xóa danh mục thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception exc) {
                            JOptionPane.showMessageDialog(null, "Lỗi xóa danh mục: " + exc.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Danh Sách Danh Mục");
    }

    public static void main(String[] args) {
        new ViewCategory("Atharv");
    }
}
