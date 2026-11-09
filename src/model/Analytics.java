package model;

import db.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Analytics extends JFrame {
    private String[] months = {
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
            "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    };

    public Analytics(String username) {
        // Phông chữ Segoe UI hỗ trợ tiếng Việt Unicode
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 26);
        Font fontLoc = new Font("Segoe UI", Font.BOLD, 15);

        JLabel title = new JLabel("Thống Kê Chi Tiêu - " + username, JLabel.CENTER);
        title.setForeground(new Color(255, 255, 255));
        title.setBackground(new Color(0, 102, 204));
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(title, BorderLayout.CENTER);

        // Bảng lọc theo năm
        String[] year = {"Tất cả", "2023", "2024", "2025", "2026"};
        JComboBox<String> yearBox = new JComboBox<>(year);
        yearBox.setFont(fontLoc);
        String selectedYear = yearBox.getSelectedItem().toString();

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel lblLocNam = new JLabel("Lọc theo năm:", JLabel.CENTER);
        lblLocNam.setFont(fontLoc);
        filterPanel.add(lblLocNam);
        filterPanel.add(yearBox);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.SOUTH);

        // Thiết lập biểu đồ cột (Bar Chart)
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        updatebarChart(username, selectedYear, dataset);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Biểu Đồ Thu Nhập & Chi Tiêu Hàng Tháng",
                "Tháng",
                "Số tiền (VNĐ)",
                dataset
        );

        // Đặt phông chữ tiếng Việt cho biểu đồ JFreeChart
        Font chartFontTitle = new Font("Segoe UI", Font.BOLD, 18);
        Font chartFontAxis = new Font("Segoe UI", Font.PLAIN, 13);
        barChart.getTitle().setFont(chartFontTitle);
        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        plot.getDomainAxis().setLabelFont(chartFontAxis);
        plot.getDomainAxis().setTickLabelFont(chartFontAxis);
        plot.getRangeAxis().setLabelFont(chartFontAxis);
        plot.getRangeAxis().setTickLabelFont(chartFontAxis);
        if (barChart.getLegend() != null) {
            barChart.getLegend().setItemFont(chartFontAxis);
        }

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // Thu nhập: Xanh lá, Chi tiêu: Đỏ
        renderer.setSeriesPaint(0, new Color(0, 204, 102));
        renderer.setSeriesPaint(1, new Color(255, 51, 51));

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(750, 420));
        chartPanel.setMouseWheelEnabled(true);

        yearBox.addActionListener(a -> {
            String selYear = yearBox.getSelectedItem().toString();
            updatebarChart(username, selYear, dataset);
        });

        JButton btnQuayLai = new JButton("Quay lại");
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);
        btnQuayLai.addActionListener(a -> {
            new Home(username);
            dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnQuayLai);

        title.setFont(fontTieuDe);
        btnQuayLai.setFont(fontNut);

        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));
        c.add(topContainer, BorderLayout.NORTH);
        c.add(chartPanel, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Thống Kê & Phân Tích");
    }

    private void updatebarChart(String username, String selectedYear, DefaultCategoryDataset dataset) {
        dataset.clear();

        for (String type : new String[]{"Thu nhập", "Chi tiêu"}) {
            for (String monthName : months) {
                dataset.setValue(0.0, type, monthName);
            }
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = """
                SELECT MONTH(date) AS months, type, SUM(amount) AS total
                FROM transactions
                WHERE username = ? AND (? = 'Tất cả' OR YEAR(date) = ?)
                GROUP BY MONTH(date), type
                ORDER BY MONTH(date)
            """;
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, selectedYear);
                pst.setInt(3, selectedYear.equals("Tất cả") ? 0 : Integer.parseInt(selectedYear));
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    int month = rs.getInt("months");
                    if (month >= 1 && month <= 12) {
                        String monthName = months[month - 1];
                        String rawType = rs.getString("type");
                        String typeLabel = "income".equalsIgnoreCase(rawType) ? "Thu nhập" : "Chi tiêu";
                        double total = rs.getDouble("total");
                        dataset.setValue(total, typeLabel, monthName);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu biểu đồ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Analytics("Atharv");
    }
}
