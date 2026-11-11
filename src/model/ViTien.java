package model;

import util.DinhDangTienHelper;

/**
 * Lớp thực thể ViTien (Wallet Entity)
 * Đại diện cho nguồn tiền / ví tài chính của người dùng (Tiền mặt, Ngân hàng, Ví điện tử...).
 */
public class ViTien {
    private int id;
    private int userId;
    private String tenVi;
    private String loaiVi; // "TIEN_MAT", "NGAN_HANG", "VI_DIEN_TU", "TIET_KIEM", "THE_TIN_DUNG"
    private double soDu;
    private String ghiChu;

    public ViTien() {
    }

    public ViTien(int userId, String tenVi, String loaiVi, double soDu, String ghiChu) {
        this.userId = userId;
        this.tenVi = tenVi;
        this.loaiVi = loaiVi;
        this.soDu = soDu;
        this.ghiChu = ghiChu;
    }

    public ViTien(int id, int userId, String tenVi, String loaiVi, double soDu, String ghiChu) {
        this.id = id;
        this.userId = userId;
        this.tenVi = tenVi;
        this.loaiVi = loaiVi;
        this.soDu = soDu;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTenVi() {
        return tenVi;
    }

    public void setTenVi(String tenVi) {
        this.tenVi = tenVi;
    }

    public String getLoaiVi() {
        return loaiVi;
    }

    public void setLoaiVi(String loaiVi) {
        this.loaiVi = loaiVi;
    }

    public double getSoDu() {
        return soDu;
    }

    public void setSoDu(double soDu) {
        this.soDu = soDu;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy tên hiển thị tiếng Việt của loại ví.
     */
    public String getTenLoaiViHienThi() {
        if (loaiVi == null) return "Khác";
        switch (loaiVi) {
            case "TIEN_MAT":
                return "Tiền mặt";
            case "NGAN_HANG":
                return "Ngân hàng";
            case "VI_DIEN_TU":
                return "Ví điện tử";
            case "TIET_KIEM":
                return "Tiết kiệm";
            case "THE_TIN_DUNG":
                return "Thẻ tín dụng";
            default:
                return loaiVi;
        }
    }

    /**
     * Lấy số dư đã định dạng chuỗi VNĐ.
     */
    public String getSoDuDinhDang() {
        return DinhDangTienHelper.dinhDangVND(soDu);
    }

    @Override
    public String toString() {
        return tenVi + " (" + getSoDuDinhDang() + ")";
    }
}

