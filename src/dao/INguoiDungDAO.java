package dao;

import model.NguoiDung;

/**
 * Interface INguoiDungDAO định nghĩa các thao tác quản lý tài khoản người dùng
 * và các chức năng quản trị hệ thống dành cho Admin.
 */
public interface INguoiDungDAO extends IBaseDAO<NguoiDung> {
    /**
     * Xác thực thông tin đăng nhập của người dùng.
     * @param username tên đăng nhập
     * @param password mật khẩu
     * @return đối tượng NguoiDung nếu chính xác, ngược lại null
     */
    NguoiDung dangNhap(String username, String password);

    /**
     * Đăng ký tài khoản người dùng mới.
     * @param nguoiDung thông tin tài khoản
     * @return true nếu đăng ký thành công
     */
    boolean dangKy(NguoiDung nguoiDung);

    /**
     * Kiểm tra xem tên đăng nhập hoặc email đã tồn tại hay chưa.
     * @param username tên đăng nhập
     * @param email email
     * @return true nếu đã tồn tại
     */
    boolean kiemTraTonTai(String username, String email);

    /**
     * Lấy mã số ID của người dùng theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return ID người dùng hoặc -1 nếu không tồn tại
     */
    int layIdTheoUsername(String username);

    /**
     * Khóa hoặc mở khóa tài khoản người dùng (Dành cho Admin).
     * @param id mã người dùng
     * @param trangThaiMoi "ACTIVE" hoặc "LOCKED"
     * @return true nếu thành công
     */
    boolean khoaMoKhoaTaiKhoan(int id, String trangThaiMoi);

    /**
     * Đặt lại mật khẩu mới cho người dùng (Dành cho Admin).
     * @param id mã người dùng
     * @param matKhauMoi mật khẩu mới
     * @return true nếu thành công
     */
    boolean datLaiMatKhau(int id, String matKhauMoi);

    /**
     * Đếm tổng số lượng người dùng trong hệ thống (KPIs Admin).
     */
    int layTongSoNguoiDung();

    /**
     * Đếm tổng số lượng giao dịch đã thực hiện trong toàn bộ hệ thống.
     */
    int layTongSoGiaoDichHeThong();

    /**
     * Tính tổng số tiền giao dịch đã luân chuyển trong toàn bộ hệ thống.
     */
    double layTongTienGiaoDichHeThong();
}
