package model;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ViewExpense extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private String username;
    private HashMap<String, String> categoryColors = new HashMap<>();

    public ViewExpense(String username) {
        this.username = username;
        // Phông chữ Segoe UI hỗ trợ tiếng Việt Unicode
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 26);
        Font fontNoiDung = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontBang = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontNhanTongKet = new Font("Segoe UI", Font.BOLD, 15);

        JLabel title = new JLabel("Lịch Sử Giao Dịch - " + username, JLabel.CENTER);
        title.setForeground(new Color(255, 255, 255));
        title.setBackground(new Color(0, 102, 204));
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.add(title, BorderLayout.CENTER);

        JLabel warningLabel = new JLabel();
        warningLabel.setForeground(new Color(255, 90, 90));

        String[] columnNames = {"Danh mục", "Mô tả", "Số tiền (VNĐ)", "Thời gian", "Loại", "id"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setGridColor(new Color(224, 224, 224));

        JScrollPane scrollPane = new JScrollPane(table);

        // Tải màu danh mục của người dùng
        try (Connection con = DBConnection.getConnection()) {
            String loadHex = "SELECT name, color FROM categories WHERE user_id = (SELECT id FROM users WHERE username = ?)";
            try (PreparedStatement pst = con.prepareStatement(loadHex)) {
                pst.setString(1, username);
                ResultSet rsn = pst.executeQuery();
                while (rsn.next()) {
                    String s1 = rsn.getString("name");
                    String s2 = rsn.getString("color");
                    categoryColors.put(s1.toLowerCase(), s2);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải màu danh mục: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đổi màu cột Danh mục theo màu tương ứng
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

        // Đổi màu cột Loại: Thu nhập (Xanh lá), Chi tiêu (Đỏ)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component co = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String type = value.toString().toLowerCase();
                    try {
                        if (type.contains("thu nhập") || type.contains("income")) {
                            co.setForeground(new Color(50, 225, 50));
                        } else {
                            co.setForeground(Color.RED);
                        }
                    } catch (Exception ex) {
                        co.setForeground(Color.BLACK);
                    }
                }
                return co;
            }
        });

        // Thanh công cụ lọc và tìm kiếm
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel lblLoai = new JLabel("Loại:");
        String[] transTypes = {"Tất cả", "Thu nhập", "Chi tiêu"};
        JComboBox<String> typeFilter = new JComboBox<>(transTypes);
        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        JTextField searchField = new JTextField(15);
        JButton searchBtn = new JButton("Tìm kiếm");

        filterPanel.add(lblLoai);
        filterPanel.add(typeFilter);
        filterPanel.add(lblTimKiem);
        filterPanel.add(searchField);
        filterPanel.add(searchBtn);

        typeFilter.addActionListener(a -> {
            applyFilters(typeFilter.getSelectedItem().toString(), searchField.getText());
        });

        searchBtn.addActionListener(a -> {
            applyFilters(typeFilter.getSelectedItem().toString(), searchField.getText());
        });

        JLabel incLabel = new JLabel("Tổng thu nhập: 0 đ");
        JLabel expLabel = new JLabel("Tổng chi tiêu: 0 đ");
        JLabel balLabel = new JLabel("Số dư hiện tại: 0 đ");

        incLabel.setFont(fontNhanTongKet);
        expLabel.setFont(fontNhanTongKet);
        balLabel.setFont(fontNhanTongKet);
        warningLabel.setFont(fontNhanTongKet);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 12));
        summaryPanel.add(incLabel);
        summaryPanel.add(expLabel);
        summaryPanel.add(balLabel);
        summaryPanel.add(warningLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(1, 20, 10, 20));
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(summaryPanel, BorderLayout.SOUTH);

        JButton btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setBackground(new Color(70, 70, 70));
        btnQuayLai.setForeground(new Color(200, 200, 200));
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setOpaque(true);
        btnQuayLai.addActionListener(a -> {
            new Home(username);
            dispose();
        });

        JButton btnChinhSua = new JButton("Chỉnh sửa");
        btnChinhSua.setBackground(new Color(0, 153, 102));
        btnChinhSua.setForeground(Color.WHITE);
        btnChinhSua.setFocusPainted(false);
        btnChinhSua.setBorderPainted(false);
        btnChinhSua.setOpaque(true);

        // Sự kiện chỉnh sửa giao dịch
        btnChinhSua.addActionListener(a -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một giao dịch để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            int selectedRowId = Integer.parseInt(tableModel.getValueAt(modelRow, 5).toString());
            String oldCategory = table.getValueAt(row, 0).toString();
            String oldDesc = table.getValueAt(row, 1).toString();
            String oldAmount = table.getValueAt(row, 2).toString();
            String oldDateTime = table.getValueAt(row, 3).toString();
            String oldType = table.getValueAt(row, 4).toString();

            String newDesc = JOptionPane.showInputDialog(null, "Chỉnh sửa mô tả:", oldDesc);
            if (newDesc == null) return;

            String newAmountstr = JOptionPane.showInputDialog(null, "Chỉnh sửa số tiền (VNĐ):", oldAmount);
            if (newAmountstr == null) return;

            double parsedAmount;
            try {
                parsedAmount = DinhDangTienHelper.chuyenDoiSoTien(newAmountstr);
                if (parsedAmount <= 0) {
                    JOptionPane.showMessageDialog(null, "Số tiền phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Số tiền không hợp lệ! (Ví dụ: 50k, 500k, 5tr, 5000000)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String newDateTime = JOptionPane.showInputDialog(null, "Chỉnh sửa ngày giờ (Định dạng: yyyy-MM-dd HH:mm:ss):", oldDateTime);
            if (newDateTime == null) return;

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setLenient(false);
                sdf.parse(newDateTime);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "Định dạng ngày giờ không hợp lệ. Vui lòng nhập theo: yyyy-MM-dd HH:mm:ss", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] types = {"Thu nhập", "Chi tiêu"};
            String defaultTypeVi = oldType.contains("Thu nhập") || oldType.equalsIgnoreCase("income") ? "Thu nhập" : "Chi tiêu";
            String newType = (String) JOptionPane.showInputDialog(null, "Chọn loại giao dịch:", "Chỉnh sửa loại", JOptionPane.QUESTION_MESSAGE, null, types, defaultTypeVi);
            if (newType == null) return;

            String[] categoryArray = categoryColors.keySet().toArray(new String[0]);
            String newCategory = (String) JOptionPane.showInputDialog(null, "Chọn danh mục:", "Chỉnh sửa danh mục", JOptionPane.QUESTION_MESSAGE, null, categoryArray, oldCategory.toLowerCase());
            if (newCategory == null) return;

            String typeDb = newType.equals("Thu nhập") ? "income" : "expense";

            try (Connection con = DBConnection.getConnection()) {
                String query = "UPDATE transactions SET category_name = ?, description = ?, amount = ?, date = ?, type = ? WHERE id = ? AND username = ?";
                try (PreparedStatement pst = con.prepareStatement(query)) {
                    pst.setString(1, newCategory);
                    pst.setString(2, newDesc);
                    pst.setDouble(3, parsedAmount);
                    pst.setString(4, newDateTime);
                    pst.setString(5, typeDb);
                    pst.setInt(6, selectedRowId);
                    pst.setString(7, username);
                    int updated = pst.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(null, "Cập nhật giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new ViewExpense(username);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật giao dịch: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnXoa = new JButton("Xóa");
        btnXoa.setBackground(new Color(232, 66, 66));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.setBorderPainted(false);
        btnXoa.setOpaque(true);

        // Sự kiện xóa giao dịch
        btnXoa.addActionListener(a -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một giao dịch để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa giao dịch này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) tableModel.getValueAt(row, 5);
                deleteTransactionbyID(id);
                tableModel.removeRow(row);
                JOptionPane.showMessageDialog(null, "Đã xóa giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel btmPanel = new JPanel();
        btmPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btmPanel.add(btnQuayLai);
        btmPanel.add(btnChinhSua);
        btmPanel.add(btnXoa);

        Container c = getContentPane();
        c.setLayout(new BorderLayout(20, 20));
        c.add(topPanel, BorderLayout.NORTH);
        c.add(centerPanel, BorderLayout.CENTER);
        c.add(btmPanel, BorderLayout.SOUTH);

        title.setFont(fontTieuDe);
        table.setFont(fontBang);
        lblLoai.setFont(fontNoiDung);
        typeFilter.setFont(fontNoiDung);
        lblTimKiem.setFont(fontNoiDung);
        searchField.setFont(fontNoiDung);
        searchBtn.setFont(fontNoiDung);
        btnQuayLai.setFont(fontNoiDung);
        btnChinhSua.setFont(fontNoiDung);
        btnXoa.setFont(fontNoiDung);

        double totalincome = 0;
        double totalexpense = 0;

        // Tải danh sách giao dịch ban đầu
        try (Connection con = DBConnection.getConnection()) {
            String loadSql = "SELECT id, category_name, description, amount, date, type FROM transactions WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(loadSql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("category_name");
                    String displayName = name.substring(0, 1).toUpperCase() + name.substring(1);
                    String desc = rs.getString("description");
                    double amount = rs.getDouble("amount");
                    String date = rs.getString("date");
                    String rawType = rs.getString("type");

                    String typeDisplay = "income".equalsIgnoreCase(rawType) ? "Thu nhập" : "Chi tiêu";

                    if ("income".equalsIgnoreCase(rawType)) {
                        totalincome += amount;
                    } else {
                        totalexpense += amount;
                    }

                    String amountStr = String.format("%.2f", amount);
                    tableModel.addRow(new Object[]{displayName, desc, amountStr, date, typeDisplay, id});
                }

                double balance = totalincome - totalexpense;
                incLabel.setText(String.format("Tổng thu nhập: %,.2f đ", totalincome));
                expLabel.setText(String.format("Tổng chi tiêu: %,.2f đ", totalexpense));
                balLabel.setText(String.format("Số dư hiện tại: %,.2f đ", balance));
                if (balance < 0) {
                    warningLabel.setText("Cảnh báo: Bạn đang chi tiêu vượt mức!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải giao dịch: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setVisible(true);
        setSize(1107, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Lịch Sử Giao Dịch");
    }

    private void deleteTransactionbyID(int id) {
        try (Connection con = DBConnection.getConnection()) {
            String deleteSql = "DELETE FROM transactions WHERE id = ?";
            try (PreparedStatement pst = con.prepareStatement(deleteSql)) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    void applyFilters(String type, String keyword) {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT id, category_name, description, amount, date, type FROM transactions WHERE username = ?";
            if (!type.equals("Tất cả")) {
                sql += " AND type = ?";
            }
            if (!keyword.trim().isEmpty()) {
                sql += " AND (category_name LIKE ? OR description LIKE ?)";
            }
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                int i = 1;
                pst.setString(i++, username);
                if (!type.equals("Tất cả")) {
                    String dbType = type.equals("Thu nhập") ? "income" : "expense";
                    pst.setString(i++, dbType);
                }
                if (!keyword.trim().isEmpty()) {
                    pst.setString(i++, "%" + keyword + "%");
                    pst.setString(i++, "%" + keyword + "%");
                }
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String cat = rs.getString("category_name");
                    String desc = rs.getString("description");
                    double amt = rs.getDouble("amount");
                    String date = rs.getString("date");
                    String t = rs.getString("type");
                    String typeDisplay = "income".equalsIgnoreCase(t) ? "Thu nhập" : "Chi tiêu";

                    tableModel.addRow(new Object[]{
                            cat.substring(0, 1).toUpperCase() + cat.substring(1),
                            desc,
                            String.format("%.2f", amt),
                            date,
                            typeDisplay,
                            id
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi lọc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ViewExpense("Atharv");
    }
}
