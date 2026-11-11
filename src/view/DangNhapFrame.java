package view;

import dao.INguoiDungDAO;
import dao.NguoiDungDAO;
import model.NguoiDung;
import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Màn hình Đăng nhập (DangNhapFrame).
 * Kế thừa từ BaseFrame, sử dụng INguoiDungDAO để xác thực tài khoản.
 */
public class DangNhapFrame extends BaseFrame {
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JLabel lblDangKy;
    private JLabel title;
    private JLabel lblLogo;
    private JPanel pnlTheDangNhap;

    public DangNhapFrame() {
        super("Quản Lý Chi Tiêu Thông Minh - Đăng Nhập", 820, 570);
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 18);

        // Logo ứng dụng
        lblLogo = new JLabel(GiaoDienHelper.getLogoIcon(70, 70), SwingConstants.CENTER);

        title = new JLabel("Quản Lý Chi Tiêu Thông Minh", JLabel.CENTER);
        if (GiaoDienHelper.laGiaoDienToi()) {
            title.setForeground(new Color(0, 255, 200));
        } else {
            title.setForeground(new Color(13, 148, 136));
        }

        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        txtTenDangNhap = new JTextField(15);

        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        txtMatKhau = new JPasswordField(15);

        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(new Color(50, 202, 202));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setOpaque(true);

        lblDangKy = new JLabel("Chưa có tài khoản? Đăng ký ngay");
        lblDangKy.setForeground(new Color(50, 202, 202));
        lblDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        title.setFont(fontTieuDe);
        lblTenDangNhap.setFont(fontNhan);
        txtTenDangNhap.setFont(fontNhan);
        lblMatKhau.setFont(fontNhan);
        txtMatKhau.setFont(fontNhan);
        btnDangNhap.setFont(fontNut);
        lblDangKy.setFont(fontNhan);

        lblLogo.setBounds(275, 20, 70, 70);
        title.setBounds(10, 98, 600, 40);
        lblTenDangNhap.setBounds(80, 155, 140, 36);
        txtTenDangNhap.setBounds(230, 155, 310, 36);
        lblMatKhau.setBounds(80, 210, 140, 36);
        txtMatKhau.setBounds(230, 210, 310, 36);
        btnDangNhap.setBounds(210, 275, 200, 42);
        lblDangKy.setBounds(160, 338, 300, 30);
        lblDangKy.setHorizontalAlignment(SwingConstants.CENTER);

        // Thẻ Card căn giữa
        pnlTheDangNhap = new JPanel();
        pnlTheDangNhap.setLayout(null);
        pnlTheDangNhap.setPreferredSize(new Dimension(620, 420));
        GiaoDienHelper.apDungMauTheCard(pnlTheDangNhap);

        pnlTheDangNhap.add(lblLogo);
        pnlTheDangNhap.add(title);
        pnlTheDangNhap.add(lblTenDangNhap);
        pnlTheDangNhap.add(txtTenDangNhap);
        pnlTheDangNhap.add(lblMatKhau);
        pnlTheDangNhap.add(txtMatKhau);
        pnlTheDangNhap.add(btnDangNhap);
        pnlTheDangNhap.add(lblDangKy);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheDangNhap);

        getRootPane().setDefaultButton(btnDangNhap);
    }

    @Override
    protected void dangKySuKien() {
        // Sự kiện click nút đăng nhập
        btnDangNhap.addActionListener(e -> xuLyDangNhap());

        // Chuyển sang màn hình đăng ký
        lblDangKy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chuyenManHinh(new DangKyFrame());
            }
        });
    }

    private void xuLyDangNhap() {
        String username = txtTenDangNhap.getText().trim();
        String password = new String(txtMatKhau.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi tầng DAO để xác thực tài khoản
        NguoiDung nguoiDung = nguoiDungDAO.dangNhap(username, password);
        if (nguoiDung != null) {
            // Kiểm tra trạng thái khóa tài khoản
            if (nguoiDung.daBiKhoa()) {
                JOptionPane.showMessageDialog(this,
                        "Tài khoản '" + username + "' hiện đang BỊ KHÓA bởi Quản trị viên!\nVui lòng liên hệ Admin để được hỗ trợ mở khóa.",
                        "Tài khoản bị khóa",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Phân quyền điều hướng: Admin vào Admin Panel, User vào Trang chủ
            if (nguoiDung.laAdmin()) {
                JOptionPane.showMessageDialog(this,
                        "Đăng nhập thành công với quyền QUẢN TRỊ VIÊN (ADMIN)!\nChào mừng " + username + ".",
                        "Đăng nhập Admin",
                        JOptionPane.INFORMATION_MESSAGE);
                chuyenManHinh(new AdminTrangChuFrame(username));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Đăng nhập thành công! Chào mừng " + username + ".",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                chuyenManHinh(new TrangChuFrame(username));
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không chính xác!",
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);
            txtMatKhau.setText("");
            txtMatKhau.requestFocus();
        }
    }
}

