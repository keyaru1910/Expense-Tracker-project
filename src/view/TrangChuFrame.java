package view;

import dao.INguoiDungDAO;
import dao.NguoiDungDAO;
import model.NguoiDung;
import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình Trang Chủ / Bảng Điều Khiển (TrangChuFrame).
 * Kế thừa từ BaseFrame, hỗ trợ đổi Theme giao diện (Dark, Light, IntelliJ)
 * và điều hướng nhanh đến toàn bộ các phân hệ chức năng: Giao dịch, Đa ví, Báo cáo, Danh mục, Thống kê.
 */
public class TrangChuFrame extends BaseFrame {
    private final String username;
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private boolean laAdmin = false;

    private JPanel pnlTheTrangChu;
    private JLabel title;
    private JLabel lblLogo;
    private JComboBox<String> themeBox;
    private JButton btnDangXuat;

    private JButton btnThemGiaoDich;
    private JButton btnXemGiaoDich;
    private JButton btnQuanLyVi;
    private JButton btnQuanLyDanhMuc;
    private JButton btnThongKe;
    private JButton btnXuatBaoCao;
    private JButton btnTaoDuLieuMau;
    private JButton btnKhuVucAdmin;

    public TrangChuFrame(String username) {
        super("Quản Lý Chi Tiêu - Trang Chủ (" + username + ")", 880, 660);
        this.username = username;

        // Kiểm tra quyền hạn Admin của tài khoản
        int userId = nguoiDungDAO.layIdTheoUsername(username);
        if (userId > 0) {
            NguoiDung u = nguoiDungDAO.layTheoId(userId);
            if (u != null && u.laAdmin()) {
                this.laAdmin = true;
            }
        } else if ("admin".equalsIgnoreCase(username)) {
            this.laAdmin = true;
        }

        if (title != null) {
            String chaoMung = "Xin chào, " + username + (laAdmin ? " (Admin)!" : "!");
            title.setText(chaoMung);
        }

        if (btnKhuVucAdmin != null) {
            btnKhuVucAdmin.setVisible(laAdmin);
        }
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontNut = new Font("Segoe UI", Font.BOLD, 15);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 26);
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

        // 8 Nút chức năng bố cục 2 cột cân xứng
        btnThemGiaoDich = new JButton("+ Thêm giao dịch");
        btnXemGiaoDich = new JButton("Lịch sử giao dịch");
        btnQuanLyVi = new JButton("Quản lý Đa ví tiền");
        btnQuanLyDanhMuc = new JButton("Quản lý danh mục");

        btnThongKe = new JButton("Biểu đồ thống kê");
        btnXuatBaoCao = new JButton("Xuất Báo Cáo Đa Kỳ");
        btnTaoDuLieuMau = new JButton("★ Nạp dữ liệu 1 năm (Demo)");
        btnKhuVucAdmin = new JButton("Bảng Điều Khiển Admin");

        pnlTheTrangChu = new JPanel();
        pnlTheTrangChu.setLayout(null);
        pnlTheTrangChu.setPreferredSize(new Dimension(780, 480));

        capNhatMauGiaoDien();

        pnlTheTrangChu.add(lblLogo);
        pnlTheTrangChu.add(title);
        pnlTheTrangChu.add(btnDangXuat);
        pnlTheTrangChu.add(themeBox);

        pnlTheTrangChu.add(btnThemGiaoDich);
        pnlTheTrangChu.add(btnXemGiaoDich);
        pnlTheTrangChu.add(btnQuanLyVi);
        pnlTheTrangChu.add(btnQuanLyDanhMuc);

        pnlTheTrangChu.add(btnThongKe);
        pnlTheTrangChu.add(btnXuatBaoCao);
        pnlTheTrangChu.add(btnTaoDuLieuMau);
        pnlTheTrangChu.add(btnKhuVucAdmin);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheTrangChu);

        title.setFont(fontTieuDe);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        btnDangXuat.setFont(fontNut);
        themeBox.setFont(fontNho);

        btnThemGiaoDich.setFont(fontNut);
        btnXemGiaoDich.setFont(fontNut);
        btnQuanLyVi.setFont(fontNut);
        btnQuanLyDanhMuc.setFont(fontNut);
        btnThongKe.setFont(fontNut);
        btnXuatBaoCao.setFont(fontNut);
        btnTaoDuLieuMau.setFont(fontNut);
        btnKhuVucAdmin.setFont(fontNut);

        // Định vị vị trí Header
        themeBox.setBounds(35, 18, 165, 36);
        btnDangXuat.setBounds(585, 18, 160, 36);
        lblLogo.setBounds(362, 12, 56, 56);
        title.setBounds(0, 68, 780, 42);

        // Bố cục 2 CỘT cân xứng
        int col1X = 50, col2X = 405, btnWidth = 325, btnHeight = 48;
        int y1 = 125, y2 = 185, y3 = 245, y4 = 305;

        // Cột trái: Quản lý chi tiêu & ví
        btnThemGiaoDich.setBounds(col1X, y1, btnWidth, btnHeight);
        btnXemGiaoDich.setBounds(col1X, y2, btnWidth, btnHeight);
        btnQuanLyVi.setBounds(col1X, y3, btnWidth, btnHeight);
        btnQuanLyDanhMuc.setBounds(col1X, y4, btnWidth, btnHeight);

        // Cột phải: Báo cáo, Thống kê & Công cụ
        btnThongKe.setBounds(col2X, y1, btnWidth, btnHeight);
        btnXuatBaoCao.setBounds(col2X, y2, btnWidth, btnHeight);
        btnTaoDuLieuMau.setBounds(col2X, y3, btnWidth, btnHeight);
        btnKhuVucAdmin.setBounds(col2X, y4, btnWidth, btnHeight);

        // Mặc định ẩn nút Admin nếu không phải admin
        btnKhuVucAdmin.setVisible(false);
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
        btnQuanLyVi.addActionListener(e -> chuyenManHinh(new QuanLyViFrame(username)));
        btnQuanLyDanhMuc.addActionListener(e -> chuyenManHinh(new QuanLyDanhMucFrame(username)));

        btnThongKe.addActionListener(e -> chuyenManHinh(new ThongKeBieuDoFrame(username)));
        btnXuatBaoCao.addActionListener(e -> chuyenManHinh(new XuatBaoCaoFrame(username)));
        btnKhuVucAdmin.addActionListener(e -> chuyenManHinh(new AdminTrangChuFrame(username)));

        // Nạp dữ liệu mẫu 1 năm để demo
        btnTaoDuLieuMau.addActionListener(e -> {
            int luaChon = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn nạp bộ dữ liệu giao dịch mẫu trọn vẹn 12 tháng năm 2025\n" +
                    "cho tài khoản '" + username + "' để demo 'Lịch sử giao dịch', 'Xuất báo cáo' và 'Thống kê phân tích' không?",
                    "Xác nhận nạp dữ liệu mẫu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (luaChon == JOptionPane.YES_OPTION) {
                boolean thanhCong = util.TaoDuLieuMauHelper.taoDuLieuMauChoNguoiDung(username, 2025);
                if (thanhCong) {
                    JOptionPane.showMessageDialog(this,
                            "Đã nạp thành công bộ giao dịch mẫu 12 tháng năm 2025!\n" +
                            "Bây giờ bạn có thể mở 'Lịch sử giao dịch', 'Xuất Báo Cáo' hoặc 'Thống kê & Phân tích' để xem kết quả.",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Nạp dữ liệu mẫu thất bại. Vui lòng kiểm tra kết nối cơ sở dữ liệu MySQL!",
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
        GiaoDienHelper.apDungMauNutChucNang(btnQuanLyVi);
        GiaoDienHelper.apDungMauNutChucNang(btnQuanLyDanhMuc);

        GiaoDienHelper.apDungMauNutChucNang(btnThongKe);
        GiaoDienHelper.apDungMauNutChucNang(btnXuatBaoCao);
        GiaoDienHelper.apDungMauNutChucNang(btnTaoDuLieuMau);

        // Nút Admin phong cách vàng cam nổi bật
        btnKhuVucAdmin.setBackground(new Color(245, 158, 11));
        btnKhuVucAdmin.setForeground(Color.WHITE);

        pnlTheTrangChu.revalidate();
        pnlTheTrangChu.repaint();
    }
}
