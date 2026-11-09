package model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Lớp tiện ích hỗ trợ định dạng và chuyển đổi tiền tệ VNĐ linh hoạt.
 * Cho phép người dùng nhập tắt nhanh các ký hiệu: 50k, 500k, 5tr, 2.5m, 1ty mà không phải gõ nhiều số 0.
 */
public class DinhDangTienHelper {

    /**
     * Chuyển đổi chuỗi nhập tiền thành giá trị số thực (double).
     * Tự động nhận diện các hậu tố viết tắt:
     * - k, nghìn: nhân 1.000
     * - m, tr, triệu: nhân 1.000.000
     * - b, ty, tỷ: nhân 1.000.000.000
     * Hỗ trợ dấu phân cách hàng nghìn (5,000,000 hoặc 5.000.000) và số thập phân (2.5tr, 1,5m).
     */
    public static double chuyenDoiSoTien(String chuoiTien) throws NumberFormatException {
        if (chuoiTien == null) {
            throw new NumberFormatException("Số tiền không được để trống");
        }
        String s = chuoiTien.trim().toLowerCase();
        // Loại bỏ các chữ đơn vị tiền tệ nếu người dùng nhập kèm
        s = s.replace("vnđ", "").replace("vnd", "").replace("đ", "").trim();

        if (s.isEmpty()) {
            throw new NumberFormatException("Vui lòng nhập số tiền");
        }

        double heSoNhan = 1.0;
        if (s.endsWith("k") || s.endsWith("nghìn") || s.endsWith("nghin")) {
            heSoNhan = 1_000.0;
            s = s.replaceAll("(k|nghìn|nghin)$", "").trim();
        } else if (s.endsWith("m") || s.endsWith("tr") || s.endsWith("triệu") || s.endsWith("trieu")) {
            heSoNhan = 1_000_000.0;
            s = s.replaceAll("(m|tr|triệu|trieu)$", "").trim();
        } else if (s.endsWith("b") || s.endsWith("ty") || s.endsWith("tỷ")) {
            heSoNhan = 1_000_000_000.0;
            s = s.replaceAll("(b|ty|tỷ)$", "").trim();
        }

        // Đếm số lượng dấu chấm và phẩy để nhận diện phân cách hàng nghìn hay thập phân
        int soDauCham = s.length() - s.replace(".", "").length();
        int soDauPhay = s.length() - s.replace(",", "").length();

        if (soDauCham > 1) {
            // 5.000.000 -> dấu chấm là phân cách hàng nghìn
            s = s.replace(".", "");
        } else if (soDauPhay > 1) {
            // 5,000,000 -> dấu phẩy là phân cách hàng nghìn
            s = s.replace(",", "");
        } else if (soDauPhay == 1 && soDauCham == 0) {
            // 2,5 -> dấu phẩy là phần thập phân
            s = s.replace(",", ".");
        }

        double giaTri = Double.parseDouble(s);
        return giaTri * heSoNhan;
    }

    /**
     * Định dạng số thực sang chuỗi tiền VNĐ có dấu phân cách hàng nghìn (ví dụ: 5,000,000 VNĐ)
     */
    public static String dinhDangTienVND(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(soTien) + " VNĐ";
    }

    /**
     * Định dạng chỉ phần số nguyên có dấu phẩy phân cách (ví dụ: 5,000,000)
     */
    public static String dinhDangSo(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(soTien);
    }
}

