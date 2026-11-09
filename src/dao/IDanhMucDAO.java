package dao;

import model.DanhMuc;

import java.util.List;

/**
 * Interface IDanhMucDAO định nghĩa các thao tác quản lý danh mục chi tiêu/thu nhập.
 */
public interface IDanhMucDAO extends IBaseDAO<DanhMuc> {
    /**
     * Lấy toàn bộ danh mục của một người dùng theo username.
     * @param username tên đăng nhập
     * @return danh sách DanhMuc
     */
    List<DanhMuc> layDanhSachTheoUsername(String username);

    /**
     * Lấy toàn bộ danh mục của một người dùng theo ID.
     * @param userId mã số người dùng
     * @return danh sách DanhMuc
     */
    List<DanhMuc> layDanhSachTheoUserId(int userId);

    /**
     * Kiểm tra và khởi tạo sẵn danh mục mặc định đẹp mắt nếu người dùng chưa có.
     * @param username tên đăng nhập
     */
    void khoiTaoDanhMucNeuChuaCo(String username);
}

