package view;

import dao.GiaoDichDAO;
import dao.IGiaoDichDAO;
import model.BaoCaoTaiChinh;
import model.GiaoDich;
import util.DinhDangTienHelper;
import util.GiaoDienHelper;
import util.XuatBaoCaoHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Xuất Báo Cáo Tài Chính (XuatBaoCaoFrame).
 * Kế thừa từ BaseFrame, cho phép lọc và xuất dữ liệu thu chi theo Ngày, Tháng, Quý, Năm.
 * Hỗ trợ xuất định dạng Microsoft Excel (.csv UTF-8 BOM) và Báo cáo HTML in PDF.
 */
public class XuatBaoCaoFrame extends BaseFrame {
    private final String username;
    private final IGiaoDichDAO giaoDichDAO = new GiaoDichDAO();

    private JRadioButton rbTheoNgay;
    private JRadioButton rbTheoThang;
    private JRadioButton rbTheoQuy;
    private JRadioButton rbTheoNam;

    private JLabel lblNhanNgay;
    private JLabel lblNhanThang;
    private JLabel lblNhanQuy;
    private JLabel lblNhanNam;

    private JComboBox<Integer> cbNgay;
    private JComboBox<Integer> cbThang;
    private JComboBox<String> cbQuy;
    private JComboBox<Integer> cbNam;

    private JLabel lblTongThu;
    private JLabel lblTongChi;
    private JLabel lblTietKiem;

    private JTable tblGiaoDich;
    private DefaultTableModel modelGiaoDich;
    private JTable tblDanhMuc;
    private DefaultTableModel modelDanhMuc;

    private JButton btnXemBaoCao;
    private JButton btnXuatExcel;
    private JButton btnXuatHTML;
    private JButton btnQuayLai;

    private BaoCaoTaiChinh baoCaoHienTai;

    public XuatBaoCaoFrame(String username) {
        super("Xuất Báo Cáo Tài Chính Đa Kỳ - " + username, 1000, 720);
        this.username = username;
        // Mặc định nạp báo cáo theo tháng hiện tại
        xuLyXemBaoCao();
    }

    @Override
    protected void khoiTaoThanhPhan() {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 26);
        Font fontNhan = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontNut = new Font("Segoe UI", Font.BOLD, 15);

        JLabel title = new JLabel("Báo Cáo Thu Chi Theo Ngày, Tháng, Quý, Năm", JLabel.CENTER);
        GiaoDienHelper.apDungMauTieuDe(title);
        title.setFont(fontTieuDe);
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 8, 0));

        // 1. THANH ĐIỀU KHIỂN CHỌN KỲ BÁO CÁO (BỐ CỤC 2 HÀNG RÕ RÀNG, KHÔNG BỊ LẤP)
        JPanel pnlChonKy = new JPanel(new GridLayout(2, 1, 0, 6));
        pnlChonKy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Chọn kỳ báo cáo"),
                BorderFactory.createEmptyBorder(4, 15, 8, 15)
        ));
        pnlChonKy.setPreferredSize(new Dimension(940, 115));
        GiaoDienHelper.apDungMauTheCard(pnlChonKy);

        // --- HÀNG 1: CÁC NÚT CHỌN LOẠI KỲ BÁO CÁO ---
        JPanel pnlHang1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 2));
        pnlHang1.setOpaque(false);

        rbTheoNgay = new JRadioButton("Theo Ngày");
        rbTheoThang = new JRadioButton("Theo Tháng", true);
        rbTheoQuy = new JRadioButton("Theo Quý");
        rbTheoNam = new JRadioButton("Theo Năm");

        rbTheoNgay.setFont(fontNhan);
        rbTheoThang.setFont(fontNhan);
        rbTheoQuy.setFont(fontNhan);
        rbTheoNam.setFont(fontNhan);

        ButtonGroup bgKy = new ButtonGroup();
        bgKy.add(rbTheoNgay);
        bgKy.add(rbTheoThang);
        bgKy.add(rbTheoQuy);
        bgKy.add(rbTheoNam);

        pnlHang1.add(rbTheoNgay);
        pnlHang1.add(rbTheoThang);
        pnlHang1.add(rbTheoQuy);
        pnlHang1.add(rbTheoNam);

        // --- HÀNG 2: CÁC Ô CHỌN THỜI GIAN & NÚT XEM BÁO CÁO ---
        JPanel pnlHang2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 2));
        pnlHang2.setOpaque(false);

        LocalDate now = LocalDate.now();

        lblNhanNgay = new JLabel("Ngày:");
        lblNhanNgay.setFont(fontNhan);
        cbNgay = new JComboBox<>();
        for (int i = 1; i <= 31; i++) cbNgay.addItem(i);
        cbNgay.setSelectedItem(now.getDayOfMonth());
        cbNgay.setPreferredSize(new Dimension(65, 30));

        lblNhanThang = new JLabel("Tháng:");
        lblNhanThang.setFont(fontNhan);
        cbThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cbThang.addItem(i);
        cbThang.setSelectedItem(now.getMonthValue());
        cbThang.setPreferredSize(new Dimension(65, 30));

        lblNhanQuy = new JLabel("Quý:");
        lblNhanQuy.setFont(fontNhan);
        cbQuy = new JComboBox<>(new String[]{"Quý 1 (T1-T3)", "Quý 2 (T4-T6)", "Quý 3 (T7-T9)", "Quý 4 (T10-T12)"});
        int currentQuy = (now.getMonthValue() - 1) / 3;
        cbQuy.setSelectedIndex(currentQuy);
        cbQuy.setPreferredSize(new Dimension(145, 30));

        lblNhanNam = new JLabel("Năm:");
        lblNhanNam.setFont(fontNhan);
        cbNam = new JComboBox<>();
        for (int y = 2024; y <= 2030; y++) cbNam.addItem(y);
        cbNam.setSelectedItem(now.getYear());
        cbNam.setPreferredSize(new Dimension(80, 30));

        btnXemBaoCao = new JButton("Xem Báo Cáo");
        btnXemBaoCao.setFont(fontNut);
        btnXemBaoCao.setBackground(new Color(14, 165, 233));
        btnXemBaoCao.setForeground(Color.WHITE);
        btnXemBaoCao.setPreferredSize(new Dimension(160, 32));

        pnlHang2.add(lblNhanNgay);
        pnlHang2.add(cbNgay);
        pnlHang2.add(lblNhanThang);
        pnlHang2.add(cbThang);
        pnlHang2.add(lblNhanQuy);
        pnlHang2.add(cbQuy);
        pnlHang2.add(lblNhanNam);
        pnlHang2.add(cbNam);
        pnlHang2.add(btnXemBaoCao);

        pnlChonKy.add(pnlHang1);
        pnlChonKy.add(pnlHang2);

        capNhatTrangThaiComboBox();

        // 2. CÁC THẺ KPI TỔNG KẾT
        JPanel pnlKPI = new JPanel(new GridLayout(1, 3, 15, 0));
        pnlKPI.setOpaque(false);
        pnlKPI.setPreferredSize(new Dimension(940, 78));

        lblTongThu = new JLabel("0 VNĐ", JLabel.CENTER);
        lblTongChi = new JLabel("0 VNĐ", JLabel.CENTER);
        lblTietKiem = new JLabel("0 VNĐ", JLabel.CENTER);

        pnlKPI.add(taoTheKPI("TỔNG THU NHẬP", lblTongThu, new Color(16, 185, 129)));
        pnlKPI.add(taoTheKPI("TỔNG CHI TIÊU", lblTongChi, new Color(239, 68, 68)));
        pnlKPI.add(taoTheKPI("TIẾT KIỆM RÒNG", lblTietKiem, new Color(59, 130, 246)));

        // 3. TAB HIỂN THỊ DỮ LIỆU
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Tab 1: Danh sách chi tiết
        String[] cotGD = {"STT", "Thời Gian", "Loại", "Danh Mục", "Ví Nguồn", "Số Tiền", "Mô Tả"};
        modelGiaoDich = new DefaultTableModel(cotGD, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGiaoDich = new JTable(modelGiaoDich);
        dinhDangBang(tblGiaoDich);
        JScrollPane spGiaoDich = new JScrollPane(tblGiaoDich);
        tabbedPane.addTab("Chi Tiết Giao Dịch", spGiaoDich);

        // Tab 2: Cơ cấu chi tiêu theo danh mục
        String[] cotDM = {"Danh Mục Chi Tiêu", "Tổng Số Tiền", "Tỷ Trọng (%)"};
        modelDanhMuc = new DefaultTableModel(cotDM, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDanhMuc = new JTable(modelDanhMuc);
        dinhDangBang(tblDanhMuc);
        JScrollPane spDanhMuc = new JScrollPane(tblDanhMuc);
        tabbedPane.addTab("Cơ Cấu Chi Tiêu", spDanhMuc);

        // 4. CÁC NÚT BẤM THAO TÁC XUẤT FILE
        btnXuatExcel = new JButton("Xuất File Excel (.csv)");
        btnXuatHTML = new JButton("Xuất Báo Cáo HTML / In PDF");
        btnQuayLai = new JButton("← Quay lại Trang Chủ");

        btnXuatExcel.setFont(fontNut);
        btnXuatHTML.setFont(fontNut);
        btnQuayLai.setFont(fontNut);

        btnXuatExcel.setBackground(new Color(16, 185, 129));
        btnXuatExcel.setForeground(Color.WHITE);
        btnXuatHTML.setBackground(new Color(13, 148, 136));
        btnXuatHTML.setForeground(Color.WHITE);
        btnQuayLai.setBackground(new Color(107, 114, 128));
        btnQuayLai.setForeground(Color.WHITE);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlNut.setOpaque(false);
        pnlNut.add(btnXuatExcel);
        pnlNut.add(btnXuatHTML);
        pnlNut.add(btnQuayLai);

        // BỐ CỤC CHÍNH
        JPanel pnlChinh = new JPanel(new BorderLayout(12, 12));
        pnlChinh.setBorder(BorderFactory.createEmptyBorder(12, 20, 15, 20));
        pnlChinh.setOpaque(false);

        // Khu vực phía trên (Tiêu đề + Chọn kỳ + KPI)
        JPanel pnlTren = new JPanel(new BorderLayout(0, 10));
        pnlTren.setOpaque(false);
        pnlTren.add(title, BorderLayout.NORTH);
        pnlTren.add(pnlChonKy, BorderLayout.CENTER);
        pnlTren.add(pnlKPI, BorderLayout.SOUTH);

        pnlChinh.add(pnlTren, BorderLayout.NORTH);
        pnlChinh.add(tabbedPane, BorderLayout.CENTER);
        pnlChinh.add(pnlNut, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pnlChinh, BorderLayout.CENTER);
    }

    private JPanel taoTheKPI(String tieuDe, JLabel lblGiaTri, Color mauNhan) {
        JPanel p = new JPanel(new BorderLayout(5, 4));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mauNhan, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        GiaoDienHelper.apDungMauTheCard(p);

        JLabel lblTieuDe = new JLabel(tieuDe, JLabel.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDe.setForeground(new Color(160, 170, 190));

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGiaTri.setForeground(mauNhan);

        p.add(lblTieuDe, BorderLayout.NORTH);
        p.add(lblGiaTri, BorderLayout.CENTER);
        return p;
    }

    private void dinhDangBang(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void capNhatTrangThaiComboBox() {
        boolean chonNgay = rbTheoNgay.isSelected();
        boolean chonThang = rbTheoThang.isSelected();
        boolean chonQuy = rbTheoQuy.isSelected();

        cbNgay.setEnabled(chonNgay);
        lblNhanNgay.setEnabled(chonNgay);

        cbThang.setEnabled(chonNgay || chonThang);
        lblNhanThang.setEnabled(chonNgay || chonThang);

        cbQuy.setEnabled(chonQuy);
        lblNhanQuy.setEnabled(chonQuy);

        cbNam.setEnabled(true);
        lblNhanNam.setEnabled(true);
    }

    @Override
    protected void dangKySuKien() {
        rbTheoNgay.addActionListener(e -> capNhatTrangThaiComboBox());
        rbTheoThang.addActionListener(e -> capNhatTrangThaiComboBox());
        rbTheoQuy.addActionListener(e -> capNhatTrangThaiComboBox());
        rbTheoNam.addActionListener(e -> capNhatTrangThaiComboBox());

        btnXemBaoCao.addActionListener(e -> xuLyXemBaoCao());
        btnXuatExcel.addActionListener(e -> xuLyXuatExcel());
        btnXuatHTML.addActionListener(e -> xuLyXuatHTML());
        btnQuayLai.addActionListener(e -> chuyenManHinh(new TrangChuFrame(username)));
    }

    private void xuLyXemBaoCao() {
        int nam = (int) cbNam.getSelectedItem();
        List<GiaoDich> danhSach = new ArrayList<>();
        String tieuDe = "";

        if (rbTheoNgay.isSelected()) {
            int ngay = (int) cbNgay.getSelectedItem();
            int thang = (int) cbThang.getSelectedItem();
            String ngayStr = String.format("%04d-%02d-%02d", nam, thang, ngay);
            danhSach = giaoDichDAO.layTheoNgay(username, ngayStr);
            tieuDe = "Báo Cáo Thu Chi Ngày " + String.format("%02d/%02d/%04d", ngay, thang, nam);
        } else if (rbTheoThang.isSelected()) {
            int thang = (int) cbThang.getSelectedItem();
            danhSach = giaoDichDAO.layTheoThang(username, thang, nam);
            tieuDe = "Báo Cáo Thu Chi Tháng " + thang + "/" + nam;
        } else if (rbTheoQuy.isSelected()) {
            int quy = cbQuy.getSelectedIndex() + 1;
            danhSach = giaoDichDAO.layTheoQuy(username, quy, nam);
            tieuDe = "Báo Cáo Thu Chi Quý " + quy + " Năm " + nam;
        } else if (rbTheoNam.isSelected()) {
            danhSach = giaoDichDAO.layTheoNam(username, nam);
            tieuDe = "Báo Cáo Thu Chi Cả Năm " + nam;
        }

        baoCaoHienTai = new BaoCaoTaiChinh(tieuDe, username, danhSach);

        // Cập nhật số liệu trên giao diện
        lblTongThu.setText("+" + baoCaoHienTai.getTongThuDinhDang());
        lblTongChi.setText("-" + baoCaoHienTai.getTongChiDinhDang());
        lblTietKiem.setText(baoCaoHienTai.getSoDuThangDuDinhDang());

        // Cập nhật bảng Giao dịch
        modelGiaoDich.setRowCount(0);
        int stt = 1;
        for (GiaoDich gd : baoCaoHienTai.getDanhSachGiaoDich()) {
            String dauTien = "income".equalsIgnoreCase(gd.getType()) ? "+" : "-";
            modelGiaoDich.addRow(new Object[]{
                    stt++,
                    gd.getDateTime(),
                    gd.getLoaiHienThi(),
                    gd.getCategoryName(),
                    gd.getTenVi(),
                    dauTien + gd.getSoTienDinhDang(),
                    gd.getDescription() != null ? gd.getDescription() : ""
            });
        }

        // Cập nhật bảng Danh mục
        modelDanhMuc.setRowCount(0);
        double tongChi = baoCaoHienTai.getTongChi();
        for (Map.Entry<String, Double> entry : baoCaoHienTai.getChiTieuTheoDanhMuc().entrySet()) {
            double phanTram = (tongChi > 0) ? (entry.getValue() / tongChi * 100) : 0;
            modelDanhMuc.addRow(new Object[]{
                    entry.getKey(),
                    DinhDangTienHelper.dinhDangVND(entry.getValue()),
                    String.format("%.1f %%", phanTram)
            });
        }
    }

    private void xuLyXuatExcel() {
        if (baoCaoHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhấn 'Xem Báo Cáo' trước khi xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Báo Cáo Excel (CSV)");
        String tenFileMacDinh = "BaoCao_" + username + "_" + System.currentTimeMillis() + ".csv";
        fileChooser.setSelectedFile(new File(tenFileMacDinh));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            boolean thanhCong = XuatBaoCaoHelper.xuatFileCSV(baoCaoHienTai, fileToSave);
            if (thanhCong) {
                int luaChonMo = JOptionPane.showConfirmDialog(this,
                        "Xuất file Excel (.csv) thành công!\n" +
                        "Đường dẫn: " + fileToSave.getAbsolutePath() + "\n\n" +
                        "Bạn có muốn mở tệp ngay bây giờ không?",
                        "Xuất file thành công",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                if (luaChonMo == JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().open(fileToSave);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Không thể tự động mở file: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyXuatHTML() {
        if (baoCaoHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhấn 'Xem Báo Cáo' trước khi xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Báo Cáo HTML (In / Lưu PDF)");
        String tenFileMacDinh = "BaoCao_" + username + "_" + System.currentTimeMillis() + ".html";
        fileChooser.setSelectedFile(new File(tenFileMacDinh));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".html")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".html");
            }

            boolean thanhCong = XuatBaoCaoHelper.xuatFileHTML(baoCaoHienTai, fileToSave);
            if (thanhCong) {
                int luaChonMo = JOptionPane.showConfirmDialog(this,
                        "Xuất file báo cáo HTML thành công!\n" +
                        "Đường dẫn: " + fileToSave.getAbsolutePath() + "\n\n" +
                        "Bạn có muốn mở trên trình duyệt để xem và in ra PDF ngay bây giờ không?",
                        "Xuất file thành công",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                if (luaChonMo == JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().open(fileToSave);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Không thể tự động mở file: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
