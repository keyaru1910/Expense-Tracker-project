package model;

import util.DinhDangTienHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp thực thể BaoCaoTaiChinh (Financial Report Model)
 * Lưu trữ dữ liệu tổng hợp thu chi, tỷ lệ cơ cấu danh mục và danh sách chi tiết theo kỳ báo cáo.
 */
public class BaoCaoTaiChinh {
    private String tieuDe;
    private String nguoiDung;
    private String ngayXuatBaoCao;
    private double tongThu;
    private double tongChi;
    private double soDuThangDu; // = tongThu - tongChi
    private int soLuongGiaoDich;
    private Map<String, Double> chiTieuTheoDanhMuc = new LinkedHashMap<>();
    private List<GiaoDich> danhSachGiaoDich = new ArrayList<>();

    public BaoCaoTaiChinh() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.ngayXuatBaoCao = dtf.format(LocalDateTime.now());
    }

    public BaoCaoTaiChinh(String tieuDe, String nguoiDung, List<GiaoDich> danhSach) {
        this();
        this.tieuDe = tieuDe;
        this.nguoiDung = nguoiDung;
        this.danhSachGiaoDich = (danhSach != null) ? danhSach : new ArrayList<>();
        tongHopDuLieu();
    }

    /**
     * Tự động duyệt danh sách giao dịch để tổng hợp số tiền thu, chi và cơ cấu danh mục.
     */
    public void tongHopDuLieu() {
        this.tongThu = 0;
        this.tongChi = 0;
        this.chiTieuTheoDanhMuc.clear();
        this.soLuongGiaoDich = danhSachGiaoDich.size();

        for (GiaoDich gd : danhSachGiaoDich) {
            double soTien = gd.getAmount();
            if ("income".equalsIgnoreCase(gd.getType())) {
                this.tongThu += soTien;
            } else {
                this.tongChi += soTien;
                String dm = gd.getCategoryName();
                if (dm == null || dm.trim().isEmpty()) {
                    dm = "Khác";
                }
                // Chuẩn hóa chữ hoa đầu dòng
                dm = Character.toUpperCase(dm.charAt(0)) + dm.substring(1);
                chiTieuTheoDanhMuc.put(dm, chiTieuTheoDanhMuc.getOrDefault(dm, 0.0) + soTien);
            }
        }
        this.soDuThangDu = this.tongThu - this.tongChi;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(String nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public String getNgayXuatBaoCao() {
        return ngayXuatBaoCao;
    }

    public double getTongThu() {
        return tongThu;
    }

    public double getTongChi() {
        return tongChi;
    }

    public double getSoDuThangDu() {
        return soDuThangDu;
    }

    public int getSoLuongGiaoDich() {
        return soLuongGiaoDich;
    }

    public Map<String, Double> getChiTieuTheoDanhMuc() {
        return chiTieuTheoDanhMuc;
    }

    public List<GiaoDich> getDanhSachGiaoDich() {
        return danhSachGiaoDich;
    }

    public void setDanhSachGiaoDich(List<GiaoDich> danhSachGiaoDich) {
        this.danhSachGiaoDich = danhSachGiaoDich;
        tongHopDuLieu();
    }

    public String getTongThuDinhDang() {
        return DinhDangTienHelper.dinhDangVND(tongThu);
    }

    public String getTongChiDinhDang() {
        return DinhDangTienHelper.dinhDangVND(tongChi);
    }

    public String getSoDuThangDuDinhDang() {
        return DinhDangTienHelper.dinhDangVND(soDuThangDu);
    }

    /**
     * Tính tỷ lệ tiết kiệm (%): (Thu - Chi) / Thu * 100
     */
    public double getTyLeTietKiem() {
        if (tongThu <= 0) return 0.0;
        return (soDuThangDu / tongThu) * 100;
    }
}

