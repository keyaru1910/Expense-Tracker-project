package dao;

import model.GiaoDich;

import java.util.List;

/**
 * Interface IGiaoDichDAO định nghĩa các thao tác quản lý giao dịch thu chi.
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
}

