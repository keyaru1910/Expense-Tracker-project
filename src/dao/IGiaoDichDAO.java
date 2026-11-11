package dao;

import model.GiaoDich;

import java.util.List;

/**
 * Interface IGiaoDichDAO định nghĩa các thao tác quản lý giao dịch thu chi.
 * Hỗ trợ truy vấn báo cáo theo Ngày, Tháng, Quý, Năm và tích hợp Đa ví.
 */
public interface IGiaoDichDAO extends IBaseDAO<GiaoDich> {
    /**
     * Lấy danh sách giao dịch có hỗ trợ bộ lọc theo loại và từ khóa tìm kiếm.
     * @param username tên đăng nhập
     * @param loaiLoc "Tất cả", "Thu nhập", hoặc "Chi tiêu"
     * @param tuKhoa từ khóa tìm kiếm theo mô tả hoặc danh mục
     * @return danh sách các đối tượng GiaoDich (KhoanThu / KhoanChi)
     */
    List<GiaoDich> layDanhSachGiaoDich(String username, String loaiLoc, String tuKhoa);

    /**
     * Cập nhật thông tin cơ bản của một giao dịch.
     */
    boolean capNhatThongTinCoBan(int id, String description, double amount, String dateTime);

    /**
     * Tính tổng thu nhập của người dùng.
     * @param username tên đăng nhập
     * @return tổng tiền thu nhập
     */
    double tinhTongThuNhap(String username);

    /**
     * Tính tổng chi tiêu của người dùng.
     * @param username tên đăng nhập
     * @return tổng tiền chi tiêu
     */
    double tinhTongChiTieu(String username);

    /**
     * Lấy dữ liệu tổng tiền theo 12 tháng trong năm phục vụ vẽ biểu đồ.
     * @param username tên đăng nhập
     * @param nam năm cần thống kê ("Tất cả" hoặc "2024", "2025", "2026")
     * @param loaiGiaoDich "income" hoặc "expense"
     * @return mảng double gồm 12 phần tử tương ứng với 12 tháng (từ tháng 1 đến tháng 12)
     */
    double[] layThongKe12Thang(String username, String nam, String loaiGiaoDich);

    /**
     * Lấy danh sách giao dịch theo 1 ngày cụ thể (định dạng "yyyy-MM-dd").
     */
    List<GiaoDich> layTheoNgay(String username, String ngay);

    /**
     * Lấy danh sách giao dịch theo khoảng ngày (từ ngày... đến ngày... định dạng "yyyy-MM-dd").
     */
    List<GiaoDich> layTheoKhoangThoiGian(String username, String tuNgay, String denNgay);

    /**
     * Lấy danh sách giao dịch theo Tháng và Năm.
     */
    List<GiaoDich> layTheoThang(String username, int thang, int nam);

    /**
     * Lấy danh sách giao dịch theo Quý (1, 2, 3, 4) và Năm.
     */
    List<GiaoDich> layTheoQuy(String username, int quy, int nam);

    /**
     * Lấy danh sách giao dịch theo trọn vẹn cả Năm.
     */
    List<GiaoDich> layTheoNam(String username, int nam);
}
