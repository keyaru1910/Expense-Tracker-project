package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Lớp tiện ích hỗ trợ định dạng và chuyển đổi tiền tệ VNĐ linh hoạt.
 * Cho phép người dùng nhập tắt nhanh các ký hiệu: 50k, 500k, 5tr, 2.5m, 1ty mà không phải gõ nhiều số 0.
 * Hỗ trợ nhận diện chuẩn xác dấu phân cách hàng nghìn (50,000 hoặc 50.000 VNĐ).
 */
public class DinhDangTienHelper {

    /**
     * Chuyển đổi chuỗi nhập tiền thành giá trị số thực (double).
     * Tự động nhận diện các hậu tố viết tắt:
     * - k, nghìn: nhân 1.000
     * - m, tr, triệu: nhân 1.000.000
     * - b, ty, tỷ: nhân 1.000.000.000
     * Nhận diện chuẩn xác dấu phân cách hàng nghìn của Việt Nam (50,000 hoặc 50.000 -> 50000).
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
        boolean coHauTo = false;

        if (s.endsWith("k") || s.endsWith("nghìn") || s.endsWith("nghin")) {
            heSoNhan = 1_000.0;
            coHauTo = true;
            s = s.replaceAll("(k|nghìn|nghin)$", "").trim();
        } else if (s.endsWith("m") || s.endsWith("tr") || s.endsWith("triệu") || s.endsWith("trieu")) {
            heSoNhan = 1_000_000.0;
            coHauTo = true;
            s = s.replaceAll("(m|tr|triệu|trieu)$", "").trim();
        } else if (s.endsWith("b") || s.endsWith("ty") || s.endsWith("tỷ")) {
            heSoNhan = 1_000_000_000.0;
            coHauTo = true;
            s = s.replaceAll("(b|ty|tỷ)$", "").trim();
        }

        if (coHauTo) {
            // Khi có hậu tố (ví dụ: 2.5tr, 2,5tr, 0.5ty), dấu phẩy hoặc chấm đóng vai trò số thập phân
            s = s.replace(",", ".");
            // Nếu người dùng gõ phân cách hàng nghìn kèm hậu tố (ví dụ: 2.500k -> 2500k)
            int viTriCham = s.lastIndexOf('.');
            if (viTriCham != -1 && (s.length() - viTriCham - 1) == 3) {
                // Sau dấu chấm có đúng 3 chữ số -> phân cách hàng nghìn (ví dụ: 2.500k)
                s = s.replace(".", "");
            }
        } else {
            // Khi KHÔNG có hậu tố (ví dụ: 50,000 hoặc 50.000 hoặc 1,000,000 hoặc 50000)
            int soDauCham = s.length() - s.replace(".", "").length();
            int soDauPhay = s.length() - s.replace(",", "").length();

            if (soDauCham > 1 || soDauPhay > 1) {
                // Có nhiều hơn 1 dấu phân cách -> chắc chắn là phân cách hàng nghìn
                s = s.replace(".", "").replace(",", "");
            } else if (soDauCham == 1 && soDauPhay == 1) {
                // Có cả 2 loại dấu (ví dụ: 1,000.00 hoặc 1.000,00)
                int idxCham = s.indexOf('.');
                int idxPhay = s.indexOf(',');
                if (idxCham > idxPhay) {
                    s = s.replace(",", "");
                } else {
                    s = s.replace(".", "").replace(",", ".");
                }
            } else if (soDauPhay == 1) {
                // Có đúng 1 dấu phẩy: kiểm tra xem sau dấu phẩy có đúng 3 chữ số không
                int idx = s.indexOf(',');
                int soChuSoSauDauPhay = s.length() - idx - 1;
                if (soChuSoSauDauPhay == 3) {
                    // Ví dụ: 50,000 -> phân cách hàng nghìn chuẩn (bỏ dấu phẩy)
                    s = s.replace(",", "");
                } else {
                    // Ví dụ: 50,5 -> phần thập phân
                    s = s.replace(",", ".");
                }
            } else if (soDauCham == 1) {
                // Có đúng 1 dấu chấm: kiểm tra xem sau dấu chấm có đúng 3 chữ số không
                int idx = s.indexOf('.');
                int soChuSoSauDauCham = s.length() - idx - 1;
                if (soChuSoSauDauCham == 3) {
                    // Ví dụ: 50.000 -> phân cách hàng nghìn kiểu Việt Nam (bỏ dấu chấm)
                    s = s.replace(".", "");
                }
            }
        }

        double giaTri = Double.parseDouble(s);
        return giaTri * heSoNhan;
    }

    /**
     * Chuyển đổi an toàn từ chuỗi gõ tắt (50k, 5tr...), trả về 0.0 nếu chuỗi không hợp lệ.
     */
    public static double chuyenDoiGoTat(String chuoiTien) {
        try {
            return chuyenDoiSoTien(chuoiTien);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Định dạng số thực sang chuỗi tiền VNĐ có dấu phân cách hàng nghìn (ví dụ: 5,000,000 VNĐ).
     */
    public static String dinhDangTienVND(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(soTien) + " VNĐ";
    }

    /**
     * Phương thức rút gọn định dạng số tiền VNĐ.
     */
    public static String dinhDangVND(double soTien) {
        return dinhDangTienVND(soTien);
    }

    /**
     * Định dạng chỉ phần số nguyên có dấu phẩy phân cách (ví dụ: 5,000,000).
     */
    public static String dinhDangSo(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(soTien);
    }
}
