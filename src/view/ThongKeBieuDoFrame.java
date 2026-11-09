package view;

import dao.GiaoDichDAO;
import dao.IGiaoDichDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình Thống Kê & Phân Tích Biểu Đồ (ThongKeBieuDoFrame).
 * Sử dụng thư viện JFreeChart vẽ biểu đồ cột đôi so sánh Thu nhập - Chi tiêu 12 tháng,
 * hỗ trợ bộ lọc linh hoạt theo năm.
 */
public class ThongKeBieuDoFrame extends BaseFrame {
    private final String username;
    private final IGiaoDichDAO giaoDichDAO = new GiaoDichDAO();

    private final String[] months = {
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
            "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    };

    private JComboBox<String> yearBox;
    private ChartPanel chartPanel;
    private DefaultCategoryDataset dataset;
    private JButton btnQuayLai;

    public ThongKeBieuDoFrame(String username) {
        super("Thống Kê Chi Tiêu - " + username, 900, 650);
        this.username = username;
        capNhatDuLieuBieuDo((String) yearBox.getSelectedItem());
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 26);
        Font fontLoc = new Font("Segoe UI", Font.BOLD, 15);

        JLabel title = new JLabel("Thống Kê Chi Tiêu - " + username, JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setBackground(new Color(0, 102, 204));
        title.setOpaque(true);
        title.setFont(fontTieuDe);
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.add(title, BorderLayout.CENTER);

        // Bộ lọc năm
        String[] yearList = {"Tất cả", "2024", "2025", "2026", "2027"};
        yearBox = new JComboBox<>(yearList);
        yearBox.setFont(fontLoc);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel lblLocNam = new JLabel("Lọc theo năm:");
        lblLocNam.setFont(fontLoc);
        filterPanel.add(lblLocNam);
        filterPanel.add(yearBox);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.SOUTH);

        // Khởi tạo dataset và biểu đồ JFreeChart
        dataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart(
                "Biểu Đồ So Sánh Thu Nhập & Chi Tiêu Hàng Tháng",
                "Thời gian (Tháng)",
                "Số tiền (VNĐ)",
                dataset
        );

        // Cài đặt phông chữ tiếng Việt Unicode cho biểu đồ
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

        // Tùy chỉnh màu sắc 2 cột: Thu nhập (Xanh lá), Chi tiêu (Đỏ)
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(34, 197, 94)); // Thu nhập
        renderer.setSeriesPaint(1, new Color(239, 68, 68)); // Chi tiêu

        chartPanel = new ChartPanel(barChart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnQuayLai = new JButton("Quay lại");
        btnQuayLai.setFont(fontNut);
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        bottomPanel.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(topContainer, BorderLayout.NORTH);
        c.add(chartPanel, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void dangKySuKien() {
        yearBox.addActionListener(e -> capNhatDuLieuBieuDo((String) yearBox.getSelectedItem()));
        btnQuayLai.addActionListener(e -> chuyenManHinh(new TrangChuFrame(username)));
    }

    private void capNhatDuLieuBieuDo(String selectedYear) {
        dataset.clear();

        // Lấy thống kê từ DAO
        double[] thuNhap12Thang = giaoDichDAO.layThongKe12Thang(username, selectedYear, "income");
        double[] chiTieu12Thang = giaoDichDAO.layThongKe12Thang(username, selectedYear, "expense");

        for (int i = 0; i < 12; i++) {
            dataset.addValue(thuNhap12Thang[i], "Thu nhập", months[i]);
            dataset.addValue(chiTieu12Thang[i], "Chi tiêu", months[i]);
        }
    }
}

