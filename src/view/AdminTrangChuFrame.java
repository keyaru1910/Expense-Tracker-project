package view;

import dao.INguoiDungDAO;
import dao.NguoiDungDAO;
import model.NguoiDung;
import util.DinhDangTienHelper;
import util.GiaoDienHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Màn hình Bảng Điều Khiển Quản Trị Viên (AdminTrangChuFrame).
 * Kế thừa từ BaseFrame, dành riêng cho tài khoản có vai trò ADMIN.
 * Cung cấp thống kê toàn hệ thống (KPIs) và quản lý người dùng (Khóa/Mở, Đổi mật khẩu).
 */
public class AdminTrangChuFrame extends BaseFrame {
    private final String adminUsername;
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    private JLabel lblTongNguoiDung;
    private JLabel lblTongGiaoDich;
    private JLabel lblTongTienHeThong;

    private JTable tblNguoiDung;
    private DefaultTableModel modelBang;

    private JButton btnKhoaMoKhoa;
    private JButton btnDatLaiMatKhau;
    private JButton btnXoaNguoiDung;
    private JButton btnVaoTrangCaNhan;
    private JButton btnDangXuat;
    private JComboBox<String> themeBox;

    public AdminTrangChuFrame(String adminUsername) {
        super("Khu Vực Quản Trị Hệ Thống (Admin Panel) - " + adminUsername, 980, 680);
        this.adminUsername = adminUsername;
        taiDuLieu();
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 26);
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 14);

        JLabel title = new JLabel("BẢNG ĐIỀU KHIỂN QUẢN TRỊ VIÊN (ADMIN)", JLabel.CENTER);
        title.setFont(fontTieuDe);
        if (GiaoDienHelper.laGiaoDienToi()) {
            title.setForeground(new Color(251, 191, 36)); // Màu vàng kim
        } else {
            title.setForeground(new Color(217, 119, 6));
        }

        // Hàng trên cùng: Đổi theme và Đăng xuất
        themeBox = new JComboBox<>(new String[]{
                GiaoDienHelper.THEME_TOI,
                GiaoDienHelper.THEME_SANG,
                GiaoDienHelper.THEME_INTELLIJ
        });
        themeBox.setSelectedItem(GiaoDienHelper.getTenGiaoDienHienTai());
        GiaoDienHelper.cauHinhHopChonGiaoDien(themeBox);

        btnDangXuat = new JButton("← Đăng xuất");
        btnDangXuat.setFont(fontNut);
        GiaoDienHelper.apDungMauNutDangXuat(btnDangXuat);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JPanel pnlHeaderTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlHeaderTrai.setOpaque(false);
        pnlHeaderTrai.add(themeBox);

        pnlHeader.add(pnlHeaderTrai, BorderLayout.WEST);
        pnlHeader.add(title, BorderLayout.CENTER);
        pnlHeader.add(btnDangXuat, BorderLayout.EAST);

        // 1. CÁC THẺ KPI THỐNG KÊ TOÀN HỆ THỐNG
        JPanel pnlKPI = new JPanel(new GridLayout(1, 3, 15, 0));
        pnlKPI.setOpaque(false);
        pnlKPI.setPreferredSize(new Dimension(940, 80));

        lblTongNguoiDung = new JLabel("0", JLabel.CENTER);
        lblTongGiaoDich = new JLabel("0", JLabel.CENTER);
        lblTongTienHeThong = new JLabel("0 VNĐ", JLabel.CENTER);

        pnlKPI.add(taoTheThongKe("TỔNG THÀNH VIÊN", lblTongNguoiDung, new Color(59, 130, 246)));
        pnlKPI.add(taoTheThongKe("TỔNG GIAO DỊCH HỆ THỐNG", lblTongGiaoDich, new Color(16, 185, 129)));
        pnlKPI.add(taoTheThongKe("DÒNG TIỀN LUÂN CHUYỂN", lblTongTienHeThong, new Color(245, 158, 11)));

        // 2. BẢNG QUẢN LÝ NGƯỜI DÙNG
        String[] cot = {"ID", "Tên Đăng Nhập", "Email", "Vai Trò", "Trạng Thái"};
        modelBang = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblNguoiDung = new JTable(modelBang);
        tblNguoiDung.setFont(fontNhan);
        tblNguoiDung.setRowHeight(36);
        tblNguoiDung.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblNguoiDung.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        // Renderer căn giữa cho ID, Vai Trò, Trạng Thái
        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        tblNguoiDung.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblNguoiDung.getColumnModel().getColumn(0).setCellRenderer(canGiua);
        tblNguoiDung.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblNguoiDung.getColumnModel().getColumn(3).setCellRenderer(canGiua);
        tblNguoiDung.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblNguoiDung.getColumnModel().getColumn(4).setCellRenderer(canGiua);

        JScrollPane spBang = new JScrollPane(tblNguoiDung);

        // 3. CÁC NÚT THAO TÁC QUẢN TRỊ
        btnKhoaMoKhoa = new JButton("Khóa / Mở Khóa");
        btnDatLaiMatKhau = new JButton("Đặt Lại Mật Khẩu");
        btnXoaNguoiDung = new JButton("Xóa Tài Khoản");
        btnVaoTrangCaNhan = new JButton("Vào Trang Cá Nhân");

        btnKhoaMoKhoa.setFont(fontNut);
        btnDatLaiMatKhau.setFont(fontNut);
        btnXoaNguoiDung.setFont(fontNut);
        btnVaoTrangCaNhan.setFont(fontNut);

        btnKhoaMoKhoa.setBackground(new Color(239, 68, 68));
        btnKhoaMoKhoa.setForeground(Color.WHITE);
        btnDatLaiMatKhau.setBackground(new Color(245, 158, 11));
        btnDatLaiMatKhau.setForeground(Color.WHITE);
        btnXoaNguoiDung.setBackground(new Color(156, 163, 175));
        btnXoaNguoiDung.setForeground(Color.WHITE);
        btnVaoTrangCaNhan.setBackground(new Color(13, 148, 136));
        btnVaoTrangCaNhan.setForeground(Color.WHITE);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        pnlNut.setOpaque(false);
        pnlNut.add(btnKhoaMoKhoa);
        pnlNut.add(btnDatLaiMatKhau);
        pnlNut.add(btnXoaNguoiDung);
        pnlNut.add(btnVaoTrangCaNhan);

        // BỐ CỤC CHÍNH
        JPanel pnlChinh = new JPanel(new BorderLayout(15, 15));
        pnlChinh.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pnlChinh.setOpaque(false);

        JPanel pnlTren = new JPanel(new BorderLayout(12, 12));
        pnlTren.setOpaque(false);
        pnlTren.add(pnlHeader, BorderLayout.NORTH);
        pnlTren.add(pnlKPI, BorderLayout.SOUTH);

        pnlChinh.add(pnlTren, BorderLayout.NORTH);
        pnlChinh.add(spBang, BorderLayout.CENTER);
        pnlChinh.add(pnlNut, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlChinh, BorderLayout.CENTER);
    }

    private JPanel taoTheThongKe(String tieuDe, JLabel lblGiaTri, Color mauNhan) {
        JPanel p = new JPanel(new BorderLayout(5, 4));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mauNhan, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        GiaoDienHelper.apDungMauTheCard(p);

        JLabel lblTieuDe = new JLabel(tieuDe, JLabel.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDe.setForeground(new Color(160, 170, 190));

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGiaTri.setForeground(mauNhan);

        p.add(lblTieuDe, BorderLayout.NORTH);
        p.add(lblGiaTri, BorderLayout.CENTER);
        return p;
    }

    public void taiDuLieu() {
        // Cập nhật KPIs
        int tongUsers = nguoiDungDAO.layTongSoNguoiDung();
        int tongGD = nguoiDungDAO.layTongSoGiaoDichHeThong();
        double tongTien = nguoiDungDAO.layTongTienGiaoDichHeThong();

        lblTongNguoiDung.setText(String.valueOf(tongUsers));
        lblTongGiaoDich.setText(String.valueOf(tongGD));
        lblTongTienHeThong.setText(DinhDangTienHelper.dinhDangVND(tongTien));

        // Cập nhật bảng
        modelBang.setRowCount(0);
        List<NguoiDung> danhSach = nguoiDungDAO.layTatCa();
        for (NguoiDung u : danhSach) {
            String trangThaiHienThi = "ACTIVE".equalsIgnoreCase(u.getTrangThai()) ? "Đang hoạt động" : "ĐÃ BỊ KHÓA";
            modelBang.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getEmail() != null ? u.getEmail() : "",
                    u.getVaiTro(),
                    trangThaiHienThi
            });
        }
    }

    @Override
    protected void dangKySuKien() {
        themeBox.addActionListener(e -> {
            String themeChon = (String) themeBox.getSelectedItem();
            if (themeChon != null && !themeChon.equals(GiaoDienHelper.getTenGiaoDienHienTai())) {
                GiaoDienHelper.apDungGiaoDien(themeChon);
                SwingUtilities.updateComponentTreeUI(this);
            }
        });

        btnDangXuat.addActionListener(e -> {
            int luaChon = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất quyền Admin không?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (luaChon == JOptionPane.YES_OPTION) {
                chuyenManHinh(new DangNhapFrame());
            }
        });

        btnKhoaMoKhoa.addActionListener(e -> xuLyKhoaMoKhoa());
        btnDatLaiMatKhau.addActionListener(e -> xuLyDatLaiMatKhau());
        btnXoaNguoiDung.addActionListener(e -> xuLyXoaNguoiDung());
        btnVaoTrangCaNhan.addActionListener(e -> chuyenManHinh(new TrangChuFrame(adminUsername)));
    }

    private void xuLyKhoaMoKhoa() {
        int row = tblNguoiDung.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelBang.getValueAt(row, 0);
        String username = (String) modelBang.getValueAt(row, 1);
        String trangThaiStr = (String) modelBang.getValueAt(row, 4);

        if (adminUsername.equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Bạn không thể tự khóa tài khoản Admin đang đăng nhập!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean dangHoatDong = trangThaiStr.contains("hoạt động");
        String trangThaiMoi = dangHoatDong ? "LOCKED" : "ACTIVE";
        String hanhDong = dangHoatDong ? "KHÓA" : "MỞ KHÓA";

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn " + hanhDong + " tài khoản [" + username + "]?",
                "Xác nhận " + hanhDong,
                JOptionPane.YES_NO_OPTION);

        if (xacNhan == JOptionPane.YES_OPTION) {
            if (nguoiDungDAO.khoaMoKhoaTaiKhoan(id, trangThaiMoi)) {
                JOptionPane.showMessageDialog(this, "Đã " + hanhDong + " tài khoản [" + username + "] thành công!");
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyDatLaiMatKhau() {
        int row = tblNguoiDung.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần đổi mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelBang.getValueAt(row, 0);
        String username = (String) modelBang.getValueAt(row, 1);

        String matKhauMoi = JOptionPane.showInputDialog(this,
                "Nhập mật khẩu mới cho người dùng [" + username + "]:",
                "Đặt Lại Mật Khẩu",
                JOptionPane.QUESTION_MESSAGE);

        if (matKhauMoi != null && !matKhauMoi.trim().isEmpty()) {
            if (nguoiDungDAO.datLaiMatKhau(id, matKhauMoi.trim())) {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công cho [" + username + "]!");
            } else {
                JOptionPane.showMessageDialog(this, "Thất bại khi đặt lại mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyXoaNguoiDung() {
        int row = tblNguoiDung.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelBang.getValueAt(row, 0);
        String username = (String) modelBang.getValueAt(row, 1);

        if (adminUsername.equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Bạn không thể xóa tài khoản của chính mình!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "CẢNH BÁO: Xóa tài khoản [" + username + "] sẽ xóa toàn bộ danh mục, ví tiền và lịch sử giao dịch liên quan!\n" +
                "Bạn có chắc chắn muốn xóa không?",
                "Xác nhận xóa tài khoản",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (xacNhan == JOptionPane.YES_OPTION) {
            if (nguoiDungDAO.xoa(id)) {
                JOptionPane.showMessageDialog(this, "Đã xóa tài khoản [" + username + "] thành công!");
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

