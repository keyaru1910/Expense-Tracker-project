package view;

import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình Trang Chủ / Bảng Điều Khiển (TrangChuFrame).
 * Kế thừa từ BaseFrame, hỗ trợ đổi Theme giao diện (Dark, Light, IntelliJ)
 * và điều hướng nhanh đến các phân hệ chức năng.
 */
public class TrangChuFrame extends BaseFrame {
    private final String username;

    private JPanel pnlTheTrangChu;
    private JLabel title;
    private JLabel lblLogo;
    private JComboBox<String> themeBox;
    private JButton btnDangXuat;
    private JButton btnThemGiaoDich;
    private JButton btnXemGiaoDich;
    private JButton btnThongKe;
    private JButton btnQuanLyDanhMuc;
    private JButton btnTaoDuLieuMau;

    public TrangChuFrame(String username) {
        super("Quản Lý Chi Tiêu - Trang Chủ (" + username + ")", 850, 630);
        this.username = username;
        // Cập nhật lại tiêu đề chào mừng sau khi nhận username
        if (title != null) {
            title.setText("Xin chào, " + username + "!");
        }
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontNut = new Font("Segoe UI", Font.BOLD, 16);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontNho = new Font("Segoe UI", Font.PLAIN, 14);

        // Logo ứng dụng
        lblLogo = new JLabel(GiaoDienHelper.getLogoIcon(56, 56), SwingConstants.CENTER);

        title = new JLabel("Xin chào!", JLabel.CENTER);
        btnDangXuat = new JButton("← Đăng xuất");

        // Hộp chọn giao diện (Theme Selector)
        themeBox = new JComboBox<>(new String[]{
                GiaoDienHelper.THEME_TOI,
                GiaoDienHelper.THEME_SANG,
                GiaoDienHelper.THEME_INTELLIJ
        });
        themeBox.setSelectedItem(GiaoDienHelper.getTenGiaoDienHienTai());
        GiaoDienHelper.cauHinhHopChonGiaoDien(themeBox);

        btnThemGiaoDich = new JButton("+ Thêm giao dịch");
        btnXemGiaoDich = new JButton("Xem lịch sử giao dịch");
        btnThongKe = new JButton("Thống kê & Phân tích");
        btnQuanLyDanhMuc = new JButton("Quản lý danh mục");
        btnTaoDuLieuMau = new JButton("⭐ Nạp dữ liệu mẫu 1 năm (Demo)");

        pnlTheTrangChu = new JPanel();
        pnlTheTrangChu.setLayout(null);
        pnlTheTrangChu.setPreferredSize(new Dimension(720, 440));

        capNhatMauGiaoDien();

        pnlTheTrangChu.add(lblLogo);
        pnlTheTrangChu.add(title);
        pnlTheTrangChu.add(btnDangXuat);
        pnlTheTrangChu.add(themeBox);
        pnlTheTrangChu.add(btnThemGiaoDich);
        pnlTheTrangChu.add(btnXemGiaoDich);
        pnlTheTrangChu.add(btnThongKe);
        pnlTheTrangChu.add(btnQuanLyDanhMuc);
        pnlTheTrangChu.add(btnTaoDuLieuMau);

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
        btnTaoDuLieuMau.setFont(fontNut);

        // Định vị vị trí các thành phần
        themeBox.setBounds(35, 20, 165, 36);
        btnDangXuat.setBounds(535, 20, 150, 36);
        lblLogo.setBounds(332, 14, 56, 56);
        title.setBounds(0, 72, 720, 42);

        int btnX = 180, btnWidth = 360, btnHeight = 44;
        btnThemGiaoDich.setBounds(btnX, 126, btnWidth, btnHeight);
        btnXemGiaoDich.setBounds(btnX, 180, btnWidth, btnHeight);
        btnThongKe.setBounds(btnX, 234, btnWidth, btnHeight);
        btnQuanLyDanhMuc.setBounds(btnX, 288, btnWidth, btnHeight);
        btnTaoDuLieuMau.setBounds(btnX, 342, btnWidth, btnHeight);
    }

    @Override
    protected void dangKySuKien() {
        // Sự kiện đổi Theme
        themeBox.addActionListener(e -> {
            String themeChon = (String) themeBox.getSelectedItem();
            if (themeChon != null && !themeChon.equals(GiaoDienHelper.getTenGiaoDienHienTai())) {
                GiaoDienHelper.apDungGiaoDien(themeChon);
                capNhatMauGiaoDien();
            }
        });

        // Đăng xuất
        btnDangXuat.addActionListener(e -> {
            int luaChon = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất không?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (luaChon == JOptionPane.YES_OPTION) {
                chuyenManHinh(new DangNhapFrame());
            }
        });

        // Điều hướng các chức năng
        btnThemGiaoDich.addActionListener(e -> chuyenManHinh(new ThemGiaoDichFrame(username)));
        btnXemGiaoDich.addActionListener(e -> chuyenManHinh(new LichSuGiaoDichFrame(username)));
        btnThongKe.addActionListener(e -> chuyenManHinh(new ThongKeBieuDoFrame(username)));
        btnQuanLyDanhMuc.addActionListener(e -> chuyenManHinh(new QuanLyDanhMucFrame(username)));

        // Nạp dữ liệu mẫu 1 năm để demo
        btnTaoDuLieuMau.addActionListener(e -> {
            int luaChon = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn nạp bộ dữ liệu giao dịch mẫu trọn vẹn 12 tháng năm 2025\n" +
                    "cho tài khoản '" + username + "' để demo 'Lịch sử giao dịch' và 'Thống kê phân tích' không?",
                    "Xác nhận nạp dữ liệu mẫu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (luaChon == JOptionPane.YES_OPTION) {
                boolean thanhCong = util.TaoDuLieuMauHelper.taoDuLieuMauChoNguoiDung(username, 2025);
                if (thanhCong) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Đã nạp thành công bộ giao dịch mẫu 12 tháng năm 2025!\n" +
                            "Bây giờ bạn có thể mở 'Xem lịch sử giao dịch' hoặc 'Thống kê & Phân tích' để xem kết quả.",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "❌ Nạp dữ liệu mẫu thất bại. Vui lòng kiểm tra kết nối cơ sở dữ liệu MySQL!",
                            "Lỗi kết nối",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void capNhatMauGiaoDien() {
        GiaoDienHelper.apDungMauTheCard(pnlTheTrangChu);
        GiaoDienHelper.apDungMauTieuDe(title);
        GiaoDienHelper.apDungMauNutDangXuat(btnDangXuat);
        GiaoDienHelper.capNhatMauHopChon(themeBox);

        GiaoDienHelper.apDungMauNutChucNang(btnThemGiaoDich);
        GiaoDienHelper.apDungMauNutChucNang(btnXemGiaoDich);
        GiaoDienHelper.apDungMauNutChucNang(btnThongKe);
        GiaoDienHelper.apDungMauNutChucNang(btnQuanLyDanhMuc);
        GiaoDienHelper.apDungMauNutChucNang(btnTaoDuLieuMau);

        pnlTheTrangChu.revalidate();
        pnlTheTrangChu.repaint();
    }
}

