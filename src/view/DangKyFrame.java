package view;

import dao.DanhMucDAO;
import dao.IDanhMucDAO;
import dao.INguoiDungDAO;
import dao.NguoiDungDAO;
import model.NguoiDung;
import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình Đăng ký tài khoản (DangKyFrame).
 * Kế thừa từ BaseFrame, sử dụng INguoiDungDAO và IDanhMucDAO.
 */
public class DangKyFrame extends BaseFrame {
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private final IDanhMucDAO danhMucDAO = new DanhMucDAO();
    private final dao.IViTienDAO viTienDAO = new dao.ViTienDAO();

    private JTextField txtTenDangNhap;
    private JTextField txtEmail;
    private JPasswordField txtMatKhau;
    private JPasswordField txtXacNhanMatKhau;
    private JButton btnDangKy;
    private JButton btnQuayLai;
    private JLabel title;
    private JLabel lblLogo;
    private JPanel pnlTheDangKy;

    public DangKyFrame() {
        super("Quản Lý Chi Tiêu Thông Minh - Đăng Ký Tài Khoản", 850, 640);
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 28);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 18);

        // Logo ứng dụng
        lblLogo = new JLabel(GiaoDienHelper.getLogoIcon(56, 56), SwingConstants.CENTER);

        title = new JLabel("Đăng Ký Tài Khoản", JLabel.CENTER);
        if (GiaoDienHelper.laGiaoDienToi()) {
            title.setForeground(new Color(0, 255, 180));
        } else {
            title.setForeground(new Color(13, 148, 136));
        }

        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        txtTenDangNhap = new JTextField(15);

        JLabel lblEmail = new JLabel("Email:");
        txtEmail = new JTextField(20);

        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        txtMatKhau = new JPasswordField(15);

        JLabel lblXacNhanMatKhau = new JLabel("Xác nhận MK:");
        txtXacNhanMatKhau = new JPasswordField(15);

        btnDangKy = new JButton("Đăng ký");
        btnDangKy.setBackground(new Color(0, 153, 102));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFocusPainted(false);
        btnDangKy.setBorderPainted(false);
        btnDangKy.setOpaque(true);

        btnQuayLai = new JButton("Quay lại");
        GiaoDienHelper.apDungMauNutPhu(btnQuayLai);

        title.setFont(fontTieuDe);
        lblTenDangNhap.setFont(fontLabel);
        txtTenDangNhap.setFont(fontLabel);
        lblEmail.setFont(fontLabel);
        txtEmail.setFont(fontLabel);
        lblMatKhau.setFont(fontLabel);
        txtMatKhau.setFont(fontLabel);
        lblXacNhanMatKhau.setFont(fontLabel);
        txtXacNhanMatKhau.setFont(fontLabel);
        btnDangKy.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        // Gợi ý định dạng (Tooltip) khi người dùng di chuột vào ô nhập liệu
        txtTenDangNhap.setToolTipText("Từ 3 đến 25 ký tự (chữ cái, chữ số, gạch dưới _)");
        txtEmail.setToolTipText("Bắt buộc phải có đuôi @gmail.com (ví dụ: hoten@gmail.com)");
        txtMatKhau.setToolTipText("Tối thiểu 6 ký tự, gồm cả chữ, số và ký tự đặc biệt (!@#$%...)");
        txtXacNhanMatKhau.setToolTipText("Nhập lại chính xác mật khẩu đã đặt");

        int labelX = 80, fieldX = 260, yStart = 125, widthLabel = 160, widthField = 310, height = 36, gap = 48;

        lblLogo.setBounds(302, 14, 56, 56);
        title.setBounds(30, 74, 600, 38);
        lblTenDangNhap.setBounds(labelX, yStart, widthLabel, height);
        txtTenDangNhap.setBounds(fieldX, yStart, widthField, height);
        lblEmail.setBounds(labelX, yStart + gap, widthLabel, height);
        txtEmail.setBounds(fieldX, yStart + gap, widthField, height);
        lblMatKhau.setBounds(labelX, yStart + 2 * gap, widthLabel, height);
        txtMatKhau.setBounds(fieldX, yStart + 2 * gap, widthField, height);
        lblXacNhanMatKhau.setBounds(labelX, yStart + 3 * gap, widthLabel, height);
        txtXacNhanMatKhau.setBounds(fieldX, yStart + 3 * gap, widthField, height);
        btnDangKy.setBounds(230, yStart + 4 * gap + 10, 200, 40);
        btnQuayLai.setBounds(230, yStart + 5 * gap + 10, 200, 40);

        pnlTheDangKy = new JPanel();
        pnlTheDangKy.setLayout(null);
        pnlTheDangKy.setPreferredSize(new Dimension(660, 525));
        GiaoDienHelper.apDungMauTheCard(pnlTheDangKy);

        pnlTheDangKy.add(lblLogo);
        pnlTheDangKy.add(title);
        pnlTheDangKy.add(lblTenDangNhap);
        pnlTheDangKy.add(txtTenDangNhap);
        pnlTheDangKy.add(lblEmail);
        pnlTheDangKy.add(txtEmail);
        pnlTheDangKy.add(lblMatKhau);
        pnlTheDangKy.add(txtMatKhau);
        pnlTheDangKy.add(lblXacNhanMatKhau);
        pnlTheDangKy.add(txtXacNhanMatKhau);
        pnlTheDangKy.add(btnDangKy);
        pnlTheDangKy.add(btnQuayLai);

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(pnlTheDangKy);

        getRootPane().setDefaultButton(btnDangKy);
    }

    @Override
    protected void dangKySuKien() {
        btnDangKy.addActionListener(e -> xuLyDangKy());
        btnQuayLai.addActionListener(e -> chuyenManHinh(new DangNhapFrame()));
    }

    private void xuLyDangKy() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String email = txtEmail.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword());

        // Kiểm tra tính hợp lệ của tất cả các trường dữ liệu theo quy định
        if (!kiemTraDuLieuDauVao(tenDangNhap, email, matKhau, xacNhanMatKhau)) {
            return;
        }

        // Kiểm tra xem tên đăng nhập hoặc email đã tồn tại trong CSDL chưa
        if (nguoiDungDAO.kiemTraTonTai(tenDangNhap, email)) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc email đã được sử dụng. Vui lòng chọn tên hoặc email khác!",
                    "Lỗi trùng lặp dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo đối tượng NguoiDung và lưu qua DAO
        NguoiDung nguoiDungMoi = new NguoiDung(tenDangNhap, matKhau, email);
        boolean thanhCong = nguoiDungDAO.dangKy(nguoiDungMoi);

        if (thanhCong) {
            // Tự động khởi tạo bộ danh mục mẫu cho người dùng mới
            danhMucDAO.khoiTaoDanhMucNeuChuaCo(tenDangNhap);

            // Tự động khởi tạo 2 ví mặc định (Ví Tiền mặt và Tài khoản Ngân hàng)
            int userId = nguoiDungMoi.getId();
            if (userId <= 0) {
                userId = nguoiDungDAO.layIdTheoUsername(tenDangNhap);
            }
            if (userId > 0) {
                viTienDAO.khoiTaoViMacDinh(userId);
            }

            JOptionPane.showMessageDialog(this,
                    "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            chuyenManHinh(new DangNhapFrame());
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thất bại do lỗi hệ thống CSDL!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xác thực dữ liệu đầu vào cho các trường thông tin đăng ký:
     * 1. Không để trống trường nào.
     * 2. Tên đăng nhập: 3-25 ký tự, chỉ gồm chữ cái, số và gạch dưới.
     * 3. Email: Định dạng hợp lệ và bắt buộc phải có đuôi @gmail.com.
     * 4. Mật khẩu: Tối thiểu 6 ký tự, phải có chữ cái, chữ số và ký tự đặc biệt.
     * 5. Xác nhận mật khẩu: Phải trùng khớp với mật khẩu.
     *
     * @return true nếu dữ liệu hợp lệ, ngược lại false
     */
    private boolean kiemTraDuLieuDauVao(String tenDangNhap, String email, String matKhau, String xacNhanMatKhau) {
        // 1. Kiểm tra các trường rỗng
        if (tenDangNhap.isEmpty() || email.isEmpty() || matKhau.isEmpty() || xacNhanMatKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ tất cả các trường thông tin!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2. Kiểm tra định dạng Tên đăng nhập
        if (!tenDangNhap.matches("^[a-zA-Z0-9_]{3,25}$")) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập không hợp lệ!\n" +
                    "- Độ dài từ 3 đến 25 ký tự.\n" +
                    "- Chỉ cho phép chữ cái (a-z, A-Z), chữ số (0-9) và dấu gạch dưới (_).\n" +
                    "- Không được chứa khoảng trắng hoặc ký tự đặc biệt khác.",
                    "Lỗi tên đăng nhập",
                    JOptionPane.WARNING_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }

        // 3. Kiểm tra định dạng Email (yêu cầu chuẩn @gmail.com)
        String regexEmailGmail = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        if (!email.toLowerCase().matches(regexEmailGmail)) {
            JOptionPane.showMessageDialog(this,
                    "Email không hợp lệ!\n" +
                    "- Email bắt buộc phải có đuôi '@gmail.com'\n" +
                    "- Ví dụ: user@gmail.com, nguyenvana@gmail.com",
                    "Lỗi định dạng Email",
                    JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }

        // 4. Kiểm tra độ dài Mật khẩu tối thiểu
        if (matKhau.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu quá ngắn! Mật khẩu phải có độ dài tối thiểu 6 ký tự.",
                    "Lỗi mật khẩu",
                    JOptionPane.WARNING_MESSAGE);
            txtMatKhau.requestFocus();
            return false;
        }

        // Kiểm tra thành phần mật khẩu: có chữ, có số, có ký tự đặc biệt
        boolean coChuCai = matKhau.matches(".*[a-zA-Z].*");
        boolean coChuSo = matKhau.matches(".*[0-9].*");
        boolean coKyTuDacBiet = matKhau.matches(".*[^a-zA-Z0-9].*");

        if (!coChuCai || !coChuSo || !coKyTuDacBiet) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu chưa đủ độ an toàn!\n" +
                    "Mật khẩu bắt buộc phải có đủ 3 yếu tố:\n" +
                    "✓ Có ký tự chữ cái (a-z, A-Z)\n" +
                    "✓ Có ký tự số (0-9)\n" +
                    "✓ Có ký tự đặc biệt (ví dụ: @, #, $, %, !, *, &, ...)",
                    "Lỗi độ phức tạp mật khẩu",
                    JOptionPane.WARNING_MESSAGE);
            txtMatKhau.requestFocus();
            return false;
        }

        // 5. Kiểm tra Xác nhận mật khẩu trùng khớp
        if (!matKhau.equals(xacNhanMatKhau)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu xác nhận không trùng khớp! Vui lòng nhập lại.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtXacNhanMatKhau.requestFocus();
            return false;
        }

        return true;
    }
}

