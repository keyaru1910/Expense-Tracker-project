package dao;

import java.util.List;

/**
 * Interface cơ sở IBaseDAO sử dụng Generics <T>.
 * Định nghĩa các phương thức chuẩn cho tầng truy xuất dữ liệu (Data Access Object).
 * Đáp ứng yêu cầu sử dụng Interface trong thiết kế hệ thống OOP.
 *
 * @param <T> Kiểu đối tượng thực thể (Model Entity)
 */
public interface IBaseDAO<T> {
    /**
     * Thêm mới một bản ghi đối tượng vào CSDL.
     * @param doiTuong đối tượng cần thêm
     * @return true nếu thêm thành công, ngược lại false
     */
    boolean them(T doiTuong);

    /**
     * Cập nhật thông tin đối tượng đã có trong CSDL.
     * @param doiTuong đối tượng cần cập nhật
     * @return true nếu cập nhật thành công, ngược lại false
     */
    boolean capNhat(T doiTuong);

    /**
     * Xóa một bản ghi theo mã định danh (id).
     * @param id mã bản ghi cần xóa
     * @return true nếu xóa thành công, ngược lại false
     */
    boolean xoa(int id);

    /**
     * Lấy thông tin đối tượng theo mã định danh (id).
     * @param id mã bản ghi
     * @return đối tượng tương ứng hoặc null nếu không tìm thấy
     */
    T layTheoId(int id);

    /**
     * Lấy danh sách toàn bộ các bản ghi trong bảng.
     * @return danh sách đối tượng
     */
    List<T> layTatCa();
}

