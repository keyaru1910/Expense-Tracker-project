package model;

import javax.swing.*;
import java.awt.*;

public class Home extends JFrame {
    private final JPanel pnlTheTrangChu;
    private final JLabel title;
    private final JComboBox<String> themeBox;
    private final JButton btnDangXuat;
    private final JButton btnThemGiaoDich;
    private final JButton btnXemGiaoDich;
    private final JButton btnThongKe;
    private final JButton btnQuanLyDanhMuc;

    public Home(String username) {
        // Phông chữ Segoe UI hỗ trợ tiếng Việt Unicode hoàn hảo
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontNho = new Font("Segoe UI", Font.PLAIN, 14);

        title = new JLabel("Xin chào, " + username + "!", JLabel.CENTER);
        btnDangXuat = new JButton("← Đăng xuất");

        // Hộp chọn giao diện (Theme Selector)
        themeBox = new JComboBox<>(new String[]{
                GiaoDienHelper.THEME_TOI,
                GiaoDienHelper.THEME_SANG,
                GiaoDienHelper.THEME_INTELLIJ
        });
        themeBox.setSelectedItem(GiaoDienHelper.getTenGiaoDienHienTai());

        // Cấu hình Renderer tùy chỉnh cho themeBox để không bị chữ trắng trên nền sáng
        GiaoDienHelper.cauHinhHopChonGiaoDien(themeBox);

        btnThemGiaoDich = new JButton("+ Thêm giao dịch");
        btnXemGiaoDich = new JButton("Xem lịch sử giao dịch");
        btnThongKe = new JButton("Thống kê & Phân tích");
        btnQuanLyDanhMuc = new JButton("Quản lý danh mục");

        // Thẻ trang chủ (Card) căn giữa màn hình
        pnlTheTrangChu = new JPanel();
        pnlTheTrangChu.setLayout(null);
        pnlTheTrangChu.setPreferredSize(new Dimension(720, 400));

        // Áp dụng màu sắc ban đầu theo theme hiện tại
        capNhatMauGiaoDien();

        pnlTheTrangChu.add(title);
        pnlTheTrangChu.add(btnDangXuat);
        pnlTheTrangChu.add(themeBox);
        pnlTheTrangChu.add(btnThemGiaoDich);
        pnlTheTrangChu.add(btnXemGiaoDich);
        pnlTheTrangChu.add(btnThongKe);
        pnlTheTrangChu.add(btnQuanLyDanhMuc);

        // Sử dụng GridBagLayout để khi phóng to toàn màn hình, toàn bộ thẻ menu luôn ở chính giữa
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheTrangChu);

        title.setFont(fontTieuDe);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        btnDangXuat.setFont(fontNut);
        themeBox.setFont(fontNho);
        btnThemGiaoDich.setFont(fontNut);
        btnXemGiaoDich.setFont(fontNut);
        btnThongKe.setFont(fontNut);
        btnQuanLyDanhMuc.setFont(fontNut);

        // Thanh công cụ phía trên: bên trái là chọn giao diện, bên phải là nút đăng xuất
        themeBox.setBounds(35, 20, 165, 36);
        btnDangXuat.setBounds(535, 20, 150, 36);

        // Dòng chào mừng căn giữa hoàn hảo theo trục dọc của thẻ và các nút chức năng
        title.setBounds(0, 68, 720, 45);

        // Các nút điều hướng chức năng căn giữa thẻ
        int btnX = 180, btnWidth = 360, btnHeight = 46;
        btnThemGiaoDich.setBounds(btnX, 135, btnWidth, btnHeight);
        btnXemGiaoDich.setBounds(btnX, 195, btnWidth, btnHeight);
        btnThongKe.setBounds(btnX, 255, btnWidth, btnHeight);
        btnQuanLyDanhMuc.setBounds(btnX, 315, btnWidth, btnHeight);

        // Xử lý đăng xuất
        btnDangXuat.addActionListener(a -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất không?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginPage();
                dispose();
            }
        });

        // Xử lý sự kiện chuyển đổi giao diện sáng/tối/IntelliJ
        themeBox.addActionListener(a -> {
            String val = (String) themeBox.getSelectedItem();
            if (val != null && !val.equals(GiaoDienHelper.getTenGiaoDienHienTai())) {
                GiaoDienHelper.apDungGiaoDien(val);
                capNhatMauGiaoDien();
            }
        });

        // Mở các chức năng
        btnThemGiaoDich.addActionListener(a -> {
            new AddExpense(username);
            dispose();
        });

        btnXemGiaoDich.addActionListener(a -> {
            new ViewExpense(username);
            dispose();
        });

        btnThongKe.addActionListener(a -> {
            new Analytics(username);
            dispose();
        });

        btnQuanLyDanhMuc.addActionListener(a -> {
            new Category(username);
            dispose();
        });

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Trang Chủ");
    }

    /**
     * Cập nhật màu sắc giao diện động cho thẻ trang chủ và các thành phần bên trong.
     * Khi chuyển sang Giao diện Sáng: thẻ nền trắng, chữ tiêu đề xanh đậm rõ nét,
     * các nút chức năng nền sáng chữ tối tương phản cao, nút đăng xuất nền đỏ nhạt thanh lịch.
     */
    public void capNhatMauGiaoDien() {
        // Cập nhật màu nền và viền của Thẻ trang chủ ở giữa màn hình
        GiaoDienHelper.apDungMauTheCard(pnlTheTrangChu);

        // Cập nhật màu chữ tiêu đề chào mừng
        GiaoDienHelper.apDungMauTieuDe(title);

        // Cập nhật màu hộp chọn theme
        GiaoDienHelper.capNhatMauHopChon(themeBox);

        // Cập nhật nút Đăng xuất
        GiaoDienHelper.apDungMauNutDangXuat(btnDangXuat);

        // Cập nhật các nút chức năng chính
        GiaoDienHelper.apDungMauNutChucNang(btnThemGiaoDich);
        GiaoDienHelper.apDungMauNutChucNang(btnXemGiaoDich);
        GiaoDienHelper.apDungMauNutChucNang(btnThongKe);
        GiaoDienHelper.apDungMauNutChucNang(btnQuanLyDanhMuc);

        pnlTheTrangChu.revalidate();
        pnlTheTrangChu.repaint();
    }

    /**
     * Phương thức tĩnh hỗ trợ đổi giao diện toàn ứng dụng (giữ lại để tương thích ngược)
     */
    public static void setAppTheme(String themeName) {
        GiaoDienHelper.apDungGiaoDien(themeName);
    }

    public static void main(String[] args) {
        GiaoDienHelper.khoiTaoGiaoDienMacDinh();
        SwingUtilities.invokeLater(() -> new Home("keyaru"));
    }
}
