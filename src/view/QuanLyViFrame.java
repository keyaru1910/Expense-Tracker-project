package view;

import dao.INguoiDungDAO;
import dao.IViTienDAO;
import dao.NguoiDungDAO;
import dao.ViTienDAO;
import model.ViTien;
import util.DinhDangTienHelper;
import util.GiaoDienHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Màn hình Quản Lý Đa Ví (QuanLyViFrame).
 * Kế thừa từ BaseFrame, giúp theo dõi tổng tài sản, quản lý các nguồn tiền
 * và điều hướng chuyển tiền giữa các ví.
 */
public class QuanLyViFrame extends BaseFrame {
    private final String username;
    private final IViTienDAO viTienDAO = new ViTienDAO();
    private final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private int userId;

    private JLabel lblTongTaiSan;
    private JTable tblDanhSachVi;
    private DefaultTableModel modelBang;
    private JButton btnThemVi;
    private JButton btnSuaVi;
    private JButton btnXoaVi;
    private JButton btnChuyenTien;
    private JButton btnQuayLai;
    private JPanel pnlTongQuan;

    public QuanLyViFrame(String username) {
        super("Quản Lý Đa Ví - Smart Expense Tracker (" + username + ")", 900, 620);
        this.username = username;
        this.userId = nguoiDungDAO.layIdTheoUsername(username);
        // Đảm bảo có ví mặc định
        viTienDAO.khoiTaoViMacDinh(userId);
        taiDuLieu();
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 26);
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 15);

        JLabel title = new JLabel("Quản Lý Đa Ví & Nguồn Tiền", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);
        title.setFont(fontTieuDe);

        // Khung tổng quan Tổng tài sản
        pnlTongQuan = new JPanel(new BorderLayout(10, 5));
        pnlTongQuan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 202, 202), 1, true),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        GiaoDienHelper.apDungMauTheCard(pnlTongQuan);

        JLabel lblNhanTong = new JLabel("TỔNG TÀI SẢN (TẤT CẢ CÁC VÍ):");
        lblNhanTong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNhanTong.setForeground(new Color(150, 160, 180));

        lblTongTaiSan = new JLabel("0 VNĐ", JLabel.LEFT);
        lblTongTaiSan.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTongTaiSan.setForeground(new Color(0, 230, 160));

        pnlTongQuan.add(lblNhanTong, BorderLayout.NORTH);
        pnlTongQuan.add(lblTongTaiSan, BorderLayout.CENTER);

        // Bảng hiển thị danh sách ví
        String[] cot = {"ID", "Tên Ví", "Phân Loại", "Số Dư Hiện Tại", "Ghi Chú"};
        modelBang = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDanhSachVi = new JTable(modelBang);
        tblDanhSachVi.setFont(fontNhan);
        tblDanhSachVi.setRowHeight(36);
        tblDanhSachVi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDanhSachVi.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        // Căn giữa cột ID và Phân Loại, căn phải cột Số Dư
        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);

        tblDanhSachVi.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblDanhSachVi.getColumnModel().getColumn(0).setCellRenderer(canGiua);
        tblDanhSachVi.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblDanhSachVi.getColumnModel().getColumn(2).setPreferredWidth(140);
        tblDanhSachVi.getColumnModel().getColumn(2).setCellRenderer(canGiua);
        tblDanhSachVi.getColumnModel().getColumn(3).setPreferredWidth(160);
        tblDanhSachVi.getColumnModel().getColumn(3).setCellRenderer(canPhai);
        tblDanhSachVi.getColumnModel().getColumn(4).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(tblDanhSachVi);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Các nút bấm chức năng
        btnThemVi = new JButton("+ Thêm ví mới");
        btnSuaVi = new JButton("Sửa ví");
        btnXoaVi = new JButton("Xóa ví");
        btnChuyenTien = new JButton("Chuyển tiền giữa các ví");
        btnQuayLai = new JButton("← Quay lại");

        btnThemVi.setFont(fontNut);
        btnSuaVi.setFont(fontNut);
        btnXoaVi.setFont(fontNut);
        btnChuyenTien.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        btnThemVi.setBackground(new Color(16, 185, 129));
        btnThemVi.setForeground(Color.WHITE);
        btnChuyenTien.setBackground(new Color(14, 165, 233));
        btnChuyenTien.setForeground(Color.WHITE);
        btnSuaVi.setBackground(new Color(245, 158, 11));
        btnSuaVi.setForeground(Color.WHITE);
        btnXoaVi.setBackground(new Color(239, 68, 68));
        btnXoaVi.setForeground(Color.WHITE);
        btnQuayLai.setBackground(new Color(107, 114, 128));
        btnQuayLai.setForeground(Color.WHITE);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        pnlNut.setOpaque(false);
        pnlNut.add(btnThemVi);
        pnlNut.add(btnChuyenTien);
        pnlNut.add(btnSuaVi);
        pnlNut.add(btnXoaVi);
        pnlNut.add(btnQuayLai);

        // Bố cục chính
        JPanel pnlChinh = new JPanel(new BorderLayout(15, 15));
        pnlChinh.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        pnlChinh.setOpaque(false);

        JPanel pnlDau = new JPanel(new BorderLayout(10, 10));
        pnlDau.setOpaque(false);
        pnlDau.add(title, BorderLayout.NORTH);
        pnlDau.add(pnlTongQuan, BorderLayout.SOUTH);

        pnlChinh.add(pnlDau, BorderLayout.NORTH);
        pnlChinh.add(scrollPane, BorderLayout.CENTER);
        pnlChinh.add(pnlNut, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlChinh, BorderLayout.CENTER);
    }

    public void taiDuLieu() {
        modelBang.setRowCount(0);
        List<ViTien> danhSach = viTienDAO.layDanhSachTheoUserId(userId);
        double tongTaiSan = 0;
        for (ViTien v : danhSach) {
            tongTaiSan += v.getSoDu();
            modelBang.addRow(new Object[]{
                    v.getId(),
                    v.getTenVi(),
                    v.getTenLoaiViHienThi(),
                    v.getSoDuDinhDang(),
                    v.getGhiChu() != null ? v.getGhiChu() : ""
            });
        }
        lblTongTaiSan.setText(DinhDangTienHelper.dinhDangVND(tongTaiSan));
    }

    @Override
    protected void dangKySuKien() {
        btnThemVi.addActionListener(e -> moHopThoaiThemVi());
        btnSuaVi.addActionListener(e -> moHopThoaiSuaVi());
        btnXoaVi.addActionListener(e -> xuLyXoaVi());
        btnChuyenTien.addActionListener(e -> {
            ChuyenTienViFrame frame = new ChuyenTienViFrame(username, this::taiDuLieu);
            frame.setVisible(true);
        });
        btnQuayLai.addActionListener(e -> chuyenManHinh(new TrangChuFrame(username)));
    }

    private void moHopThoaiThemVi() {
        JTextField txtTen = new JTextField();
        String[] cacLoai = {"TIEN_MAT", "NGAN_HANG", "VI_DIEN_TU", "TIET_KIEM", "THE_TIN_DUNG"};
        String[] tenHienThi = {"Tiền mặt", "Ngân hàng", "Ví điện tử", "Tiết kiệm", "Thẻ tín dụng"};
        JComboBox<String> cbLoai = new JComboBox<>(tenHienThi);
        JTextField txtSoDuDau = new JTextField("0");
        JTextField txtGhiChu = new JTextField();

        Object[] message = {
                "Tên ví:", txtTen,
                "Loại ví:", cbLoai,
                "Số dư ban đầu (VNĐ):", txtSoDuDau,
                "Ghi chú:", txtGhiChu
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Thêm Ví Tiền Mới", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên ví không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idx = cbLoai.getSelectedIndex();
            String loaiVi = (idx >= 0 && idx < cacLoai.length) ? cacLoai[idx] : "TIEN_MAT";
            double soDu = DinhDangTienHelper.chuyenDoiGoTat(txtSoDuDau.getText().trim());
            String ghiChu = txtGhiChu.getText().trim();

            ViTien viMoi = new ViTien(userId, ten, loaiVi, soDu, ghiChu);
            if (viTienDAO.them(viMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm ví mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm ví thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moHopThoaiSuaVi() {
        int row = tblDanhSachVi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ví cần chỉnh sửa trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelBang.getValueAt(row, 0);
        ViTien vi = viTienDAO.layTheoId(id);
        if (vi == null) return;

        JTextField txtTen = new JTextField(vi.getTenVi());
        String[] cacLoai = {"TIEN_MAT", "NGAN_HANG", "VI_DIEN_TU", "TIET_KIEM", "THE_TIN_DUNG"};
        String[] tenHienThi = {"Tiền mặt", "Ngân hàng", "Ví điện tử", "Tiết kiệm", "Thẻ tín dụng"};
        JComboBox<String> cbLoai = new JComboBox<>(tenHienThi);
        for (int i = 0; i < cacLoai.length; i++) {
            if (cacLoai[i].equalsIgnoreCase(vi.getLoaiVi())) {
                cbLoai.setSelectedIndex(i);
                break;
            }
        }
        JTextField txtGhiChu = new JTextField(vi.getGhiChu() != null ? vi.getGhiChu() : "");

        Object[] message = {
                "Tên ví:", txtTen,
                "Loại ví:", cbLoai,
                "Ghi chú:", txtGhiChu
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Chỉnh Sửa Ví", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên ví không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idx = cbLoai.getSelectedIndex();
            vi.setTenVi(ten);
            vi.setLoaiVi(cacLoai[idx]);
            vi.setGhiChu(txtGhiChu.getText().trim());

            if (viTienDAO.capNhat(vi)) {
                JOptionPane.showMessageDialog(this, "Cập nhật ví thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyXoaVi() {
        int row = tblDanhSachVi.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ví cần xóa trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modelBang.getValueAt(row, 0);
        String ten = (String) modelBang.getValueAt(row, 1);

        List<ViTien> danhSach = viTienDAO.layDanhSachTheoUserId(userId);
        if (danhSach.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Bạn phải giữ lại ít nhất 1 ví trong hệ thống!", "Không thể xóa", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa ví [" + ten + "] không?\n" +
                "Lưu ý: Các giao dịch liên kết với ví này sẽ được chuyển thành 'Mặc định'.",
                "Xác nhận xóa ví",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (xacNhan == JOptionPane.YES_OPTION) {
            if (viTienDAO.xoa(id)) {
                JOptionPane.showMessageDialog(this, "Đã xóa ví thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa ví thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

