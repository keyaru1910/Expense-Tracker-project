package dao;

import model.ViTien;

import java.util.List;

/**
 * Interface IViTienDAO định nghĩa các thao tác quản lý Đa ví (Multi-Wallet)
 * và luân chuyển dòng tiền giữa các ví.
 */
public interface IViTienDAO extends IBaseDAO<ViTien> {
    /**
     * Lấy danh sách toàn bộ các ví của một người dùng theo userId.
     */
    List<ViTien> layDanhSachTheoUserId(int userId);

    /**
     * Cập nhật tăng hoặc giảm số dư của một ví.
     * @param viId mã ví
     * @param soTienThayDoi số tiền thay đổi (dương là cộng thêm, âm là trừ đi)
     */
    boolean capNhatSoDu(int viId, double soTienThayDoi);

    /**
     * Chuyển tiền từ ví nguồn sang ví đích.
     * Tự động trừ tiền ví nguồn, cộng tiền ví đích và tạo 2 giao dịch tương ứng trong lịch sử.
     * @param viNguonId mã ví chuyển
     * @param viDichId mã ví nhận
     * @param soTien số tiền chuyển
     * @param ghiChu lý do chuyển
     * @param username tên người dùng thực hiện
     */
    boolean chuyenTien(int viNguonId, int viDichId, double soTien, String ghiChu, String username);

    /**
     * Tự động khởi tạo 2 ví mặc định (Ví Tiền mặt và Tài khoản Ngân hàng) cho người dùng mới.
     */
    void khoiTaoViMacDinh(int userId);

    /**
     * Tính tổng số dư của toàn bộ các ví mà người dùng sở hữu (Tổng tài sản).
     */
    double layTongTaiSan(int userId);
}

