package util;

import dao.DanhMucDAO;
import dao.IDanhMucDAO;
import dao.INguoiDungDAO;
import dao.NguoiDungDAO;
import model.NguoiDung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích tạo bộ dữ liệu giao dịch mẫu 1 năm (Demo Data Seeder).
 * Cung cấp dữ liệu thực tế trọn vẹn 12 tháng phục vụ:
 *   1. Màn hình Lịch sử giao dịch (LichSuGiaoDichFrame): lọc, tìm kiếm, tính tổng thu chi, số dư.
 *   2. Màn hình Thống kê biểu đồ (ThongKeBieuDoFrame): biểu đồ cột đôi 12 tháng so sánh Thu/Chi.
 *
 * Có thể chạy độc lập qua hàm main() hoặc gọi từ giao diện ứng dụng.
 */
public class TaoDuLieuMauHelper {

    private static final INguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private static final IDanhMucDAO danhMucDAO = new DanhMucDAO();

    /**
     * Lớp nội bộ đại diện cho một bản ghi giao dịch mẫu.
     */
    public static class GiaoDichMau {
        public String categoryName;
        public String description;
        public double amount;
        public String type; // "income" hoặc "expense"
        public String dateTime; // "yyyy-MM-dd HH:mm:ss"

        public GiaoDichMau(String categoryName, String description, double amount, String type, String dateTime) {
            this.categoryName = categoryName;
            this.description = description;
            this.amount = amount;
            this.type = type;
            this.dateTime = dateTime;
        }
    }

    /**
     * Tạo danh sách các giao dịch mẫu trọn vẹn 12 tháng cho một năm cụ thể.
     * @param nam Năm cần tạo dữ liệu (ví dụ: 2025)
     * @return Danh sách giao dịch mẫu
     */
    public static List<GiaoDichMau> layDanhSachGiaoDichMau1Nam(int nam) {
        List<GiaoDichMau> list = new ArrayList<>();
        String strNam = String.valueOf(nam);

        // ==================== THÁNG 1 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 01/" + strNam, 22000000, "income", strNam + "-01-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Thanh toán tiền thuê căn hộ tháng 01", 4500000, "expense", strNam + "-01-05 14:30:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền điện thoại và cước 4G tháng 01", 200000, "expense", strNam + "-01-07 10:15:00"));
        list.add(new GiaoDichMau("Ăn uống", "Đi siêu thị WinMart mua thực phẩm tuần 1", 850000, "expense", strNam + "-01-10 18:30:00"));
        list.add(new GiaoDichMau("Di chuyển", "Đổ xăng xe máy và bảo dưỡng thay nhớt định kỳ", 320000, "expense", strNam + "-01-12 11:45:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận tiền thưởng Tết Dương Lịch", 5000000, "income", strNam + "-01-15 14:00:00"));
        list.add(new GiaoDichMau("Giải trí", "Đi xem phim rạp và cà phê cuối tuần cùng bạn bè", 350000, "expense", strNam + "-01-18 20:00:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua sắm áo khoác ấm và giày dép mới", 1850000, "expense", strNam + "-01-22 17:00:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Mua vitamin C và thực phẩm bổ sung sức khỏe", 450000, "expense", strNam + "-01-25 09:30:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Thu nhập làm thêm dự án Freelance thiết kế Web", 4500000, "income", strNam + "-01-28 16:00:00"));

        // ==================== THÁNG 2 (Dịp Tết Nguyên Đán) ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 02/" + strNam, 22000000, "income", strNam + "-02-05 09:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận thưởng Tết Âm lịch & Lương tháng 13", 28000000, "income", strNam + "-02-05 14:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Thanh toán tiền thuê căn hộ tháng 02", 4500000, "expense", strNam + "-02-06 10:00:00"));
        list.add(new GiaoDichMau("Di chuyển", "Mua vé máy bay khứ hồi về quê đón Tết", 4200000, "expense", strNam + "-02-08 16:30:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua bánh kẹo, hoa đào và giỏ quà Tết biếu gia đình", 5600000, "expense", strNam + "-02-12 15:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Mua sắm mâm cỗ tất niên và thực phẩm Tết", 2500000, "expense", strNam + "-02-14 11:30:00"));
        list.add(new GiaoDichMau("Khác", "Mừng tuổi lì xì đầu năm cho bố mẹ và các cháu", 6500000, "expense", strNam + "-02-17 10:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Đi liên hoan tân niên họp mặt bạn bè đầu xuân", 1200000, "expense", strNam + "-02-20 19:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận tiền lì xì may mắn và lộc xuân", 2500000, "income", strNam + "-02-24 14:00:00"));

        // ==================== THÁNG 3 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 03/" + strNam, 22000000, "income", strNam + "-03-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 03", 4500000, "expense", strNam + "-03-05 11:00:00"));
        list.add(new GiaoDichMau("Khác", "Mua quà tặng ngày Quốc tế Phụ nữ 8/3", 1200000, "expense", strNam + "-03-08 19:30:00"));
        list.add(new GiaoDichMau("Ăn uống", "Ăn trưa văn phòng và cà phê giao lưu tuần 2", 680000, "expense", strNam + "-03-12 12:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận lãi gửi tiết kiệm ngân hàng kỳ hạn 6 tháng", 2400000, "income", strNam + "-03-15 15:00:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Đăng ký gói tập gym rèn luyện sức khỏe 3 tháng", 1500000, "expense", strNam + "-03-18 17:30:00"));
        list.add(new GiaoDichMau("Di chuyển", "Đổ xăng và rửa xe máy định kỳ", 250000, "expense", strNam + "-03-22 09:00:00"));
        list.add(new GiaoDichMau("Giải trí", "Xem kịch sân khấu và ăn tối cuối tuần", 650000, "expense", strNam + "-03-26 20:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Hóa đơn tiền điện nước sinh hoạt tháng 3", 980000, "expense", strNam + "-03-29 16:00:00"));

        // ==================== THÁNG 4 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 04/" + strNam, 22000000, "income", strNam + "-04-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê căn hộ tháng 04", 4500000, "expense", strNam + "-04-05 10:30:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận tiền thưởng hoàn thành dự án Quý 1", 8000000, "income", strNam + "-04-10 14:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Mua đồ siêu thị dự trữ thực phẩm", 1150000, "expense", strNam + "-04-14 18:30:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua sách chuyên ngành công nghệ phần mềm", 450000, "expense", strNam + "-04-18 11:00:00"));
        list.add(new GiaoDichMau("Di chuyển", "Đi lại bằng GrabCar trong tuần mưa bão", 420000, "expense", strNam + "-04-22 08:30:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Mua thuốc cảm cúm thời tiết và khẩu trang", 280000, "expense", strNam + "-04-26 15:00:00"));
        list.add(new GiaoDichMau("Giải trí", "Đặt phòng khách sạn du lịch dịp nghỉ lễ 30/4 - 1/5", 4800000, "expense", strNam + "-04-29 17:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Ăn uống hải sản tươi sống trong kỳ nghỉ lễ", 1850000, "expense", strNam + "-04-30 20:00:00"));

        // ==================== THÁNG 5 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 05/" + strNam, 22000000, "income", strNam + "-05-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 05", 4500000, "expense", strNam + "-05-05 11:30:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Thu nhập phụ: Tư vấn giải pháp kỹ thuật online", 4500000, "income", strNam + "-05-08 14:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Hóa đơn tiền điện mùa nóng chạy điều hòa", 1650000, "expense", strNam + "-05-12 16:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Ăn trưa buffet liên hoan cùng đồng nghiệp", 750000, "expense", strNam + "-05-15 12:30:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua quạt cây làm mát và phụ kiện bàn làm việc", 920000, "expense", strNam + "-05-19 19:00:00"));
        list.add(new GiaoDichMau("Di chuyển", "Đổ xăng xe máy và sửa chữa săm lốp", 280000, "expense", strNam + "-05-23 09:15:00"));
        list.add(new GiaoDichMau("Giải trí", "Cà phê Acoustic cuối tuần cùng đồng nghiệp", 320000, "expense", strNam + "-05-27 21:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Mua đồ ăn tươi ngon nấu ăn tại nhà", 850000, "expense", strNam + "-05-30 18:00:00"));

        // ==================== THÁNG 6 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 06/" + strNam, 22000000, "income", strNam + "-06-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 06", 4500000, "expense", strNam + "-06-05 10:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận tiền chi trả cổ tức chứng khoán an toàn", 3800000, "income", strNam + "-06-10 15:30:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Hóa đơn tiền điện mùa hè cao điểm", 1820000, "expense", strNam + "-06-14 18:00:00"));
        list.add(new GiaoDichMau("Di chuyển", "Vé xe limousine cao cấp đi công tác Hải Phòng", 450000, "expense", strNam + "-06-17 08:30:00"));
        list.add(new GiaoDichMau("Giải trí", "Du lịch hè Đà Nẵng - Hội An cùng gia đình", 6500000, "expense", strNam + "-06-20 16:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Thưởng thức ẩm thực và đặc sản miền Trung", 1450000, "expense", strNam + "-06-22 19:30:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua đồ bơi, kính râm và mũ đi biển", 850000, "expense", strNam + "-06-25 14:00:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Mua kem chống nắng bảo vệ da và xịt khoáng", 520000, "expense", strNam + "-06-28 10:30:00"));

        // ==================== THÁNG 7 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 07/" + strNam, 22000000, "income", strNam + "-07-05 09:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Thưởng đánh giá năng lực giữa năm (Mid-year Bonus)", 12000000, "income", strNam + "-07-05 14:30:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 07", 4500000, "expense", strNam + "-07-06 10:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Thanh toán cước internet cáp quang gói 6 tháng", 1320000, "expense", strNam + "-07-09 17:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Mua đồ ăn sạch tại siêu thị thực phẩm hữu cơ", 1100000, "expense", strNam + "-07-13 11:30:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Khám răng định kỳ, lấy cao răng tại nha khoa", 850000, "expense", strNam + "-07-17 15:00:00"));
        list.add(new GiaoDichMau("Giải trí", "Mua vé xem hòa nhạc ngoài trời", 750000, "expense", strNam + "-07-21 20:00:00"));
        list.add(new GiaoDichMau("Di chuyển", "Đổ xăng và thay má phanh xe máy an toàn", 350000, "expense", strNam + "-07-25 09:00:00"));
        list.add(new GiaoDichMau("Khác", "Đăng ký khóa học lập trình và thi chứng chỉ Cloud", 2800000, "expense", strNam + "-07-28 16:30:00"));

        // ==================== THÁNG 8 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 08/" + strNam, 22000000, "income", strNam + "-08-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 08", 4500000, "expense", strNam + "-08-05 10:30:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận tiền thù lao viết tài liệu kỹ thuật", 3500000, "income", strNam + "-08-09 15:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Ăn lẩu nướng liên hoan nhóm hoàn thành dự án", 950000, "expense", strNam + "-08-12 18:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Hóa đơn tiền điện nước sinh hoạt tháng 8", 1250000, "expense", strNam + "-08-16 11:00:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Gói khám sức khỏe tổng quát định kỳ tại bệnh viện", 2200000, "expense", strNam + "-08-19 16:30:00"));
        list.add(new GiaoDichMau("Khác", "Mua quà mừng sinh nhật bạn thân", 850000, "expense", strNam + "-08-23 19:30:00"));
        list.add(new GiaoDichMau("Di chuyển", "Chi phí xăng xe đi làm trong tháng", 310000, "expense", strNam + "-08-26 08:30:00"));
        list.add(new GiaoDichMau("Giải trí", "Chơi bowling và xem phim cuối tuần xả stress", 480000, "expense", strNam + "-08-29 20:30:00"));

        // ==================== THÁNG 9 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 09/" + strNam, 22000000, "income", strNam + "-09-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 09", 4500000, "expense", strNam + "-09-05 11:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận tiền lãi trái phiếu an toàn", 2100000, "income", strNam + "-09-07 15:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Mua thực phẩm tươi sống và trà hoa quả", 880000, "expense", strNam + "-09-10 18:30:00"));
        list.add(new GiaoDichMau("Di chuyển", "Đổ xăng xe máy và thay nhớt Motul", 380000, "expense", strNam + "-09-14 09:00:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua bàn phím cơ không dây phục vụ công việc", 1650000, "expense", strNam + "-09-18 16:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền điện nước và phí quản lý chung cư", 1100000, "expense", strNam + "-09-21 14:00:00"));
        list.add(new GiaoDichMau("Giải trí", "Chuyến đi cắm trại dã ngoại Ba Vì cuối tuần", 1350000, "expense", strNam + "-09-25 19:00:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Mua dầu cá Omega 3 và men vi sinh tiêu hóa", 390000, "expense", strNam + "-09-28 10:30:00"));

        // ==================== THÁNG 10 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 10/" + strNam, 22000000, "income", strNam + "-10-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 10", 4500000, "expense", strNam + "-10-05 10:30:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận thưởng hoàn thành dự án Quý 3", 6500000, "income", strNam + "-10-10 14:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Mua đồ siêu thị Co.opmart", 1050000, "expense", strNam + "-10-14 18:00:00"));
        list.add(new GiaoDichMau("Khác", "Mua quà 20/10 gửi tặng mẹ ở quê", 1500000, "expense", strNam + "-10-18 16:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Ăn tối liên hoan ngày Phụ nữ Việt Nam 20/10", 950000, "expense", strNam + "-10-20 19:30:00"));
        list.add(new GiaoDichMau("Di chuyển", "Đổ xăng và đóng tiền vé gửi xe tháng", 300000, "expense", strNam + "-10-23 09:00:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua áo khoác thu đông đón gió mùa mới", 980000, "expense", strNam + "-10-26 15:30:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền điện sinh hoạt và nước tháng 10", 950000, "expense", strNam + "-10-29 11:00:00"));

        // ==================== THÁNG 11 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 11/" + strNam, 22000000, "income", strNam + "-11-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 11", 4500000, "expense", strNam + "-11-05 11:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Thu nhập nhận từ dự án tư vấn giải pháp ngoài giờ", 5200000, "income", strNam + "-11-08 15:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Ăn uống sinh hoạt và cà phê sáng cả tháng", 1250000, "expense", strNam + "-11-12 18:30:00"));
        list.add(new GiaoDichMau("Di chuyển", "Chi phí xăng xe máy và đi lại GrabCar", 480000, "expense", strNam + "-11-15 10:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Hóa đơn tiền điện và phí dịch vụ chung cư", 890000, "expense", strNam + "-11-19 14:30:00"));
        list.add(new GiaoDichMau("Giải trí", "Đi xem kịch sân khấu và uống trà sữa cuối tuần", 450000, "expense", strNam + "-11-23 19:00:00"));
        list.add(new GiaoDichMau("Mua sắm", "Săn sale Black Friday: Mua màn hình máy tính 27 inch", 5800000, "expense", strNam + "-11-27 20:00:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Mua trà thảo mộc đông y bồi bổ sức khỏe", 320000, "expense", strNam + "-11-29 16:00:00"));

        // ==================== THÁNG 12 ====================
        list.add(new GiaoDichMau("Tiền lương", "Nhận lương tháng 12/" + strNam, 22000000, "income", strNam + "-12-05 09:00:00"));
        list.add(new GiaoDichMau("Hóa đơn & Tiện ích", "Tiền thuê nhà tháng 12", 4500000, "expense", strNam + "-12-05 10:30:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Nhận tiền thưởng hoàn thành xuất sắc KPI cả năm", 20000000, "income", strNam + "-12-10 14:00:00"));
        list.add(new GiaoDichMau("Thưởng & Đầu tư", "Tất toán lãi tiền gửi tiết kiệm năm " + strNam, 4600000, "income", strNam + "-12-12 16:30:00"));
        list.add(new GiaoDichMau("Ăn uống", "Tiệc liên hoan tất niên công ty và giao lưu đối tác", 1500000, "expense", strNam + "-12-15 18:00:00"));
        list.add(new GiaoDichMau("Mua sắm", "Mua cây thông Noel và phụ kiện trang trí phòng", 850000, "expense", strNam + "-12-18 20:00:00"));
        list.add(new GiaoDichMau("Giải trí", "Chuyến đi nghỉ dưỡng Sapa mùa đông ngắm mây tuyết", 4800000, "expense", strNam + "-12-22 17:00:00"));
        list.add(new GiaoDichMau("Ăn uống", "Bữa tối Giáng Sinh ấm cúng tại nhà hàng", 1350000, "expense", strNam + "-12-25 19:30:00"));
        list.add(new GiaoDichMau("Di chuyển", "Vé tàu hỏa khứ hồi và xăng xe về thăm gia đình", 850000, "expense", strNam + "-12-28 10:00:00"));
        list.add(new GiaoDichMau("Sức khỏe", "Đóng phí gia hạn bảo hiểm sức khỏe năm tới", 3200000, "expense", strNam + "-12-30 15:00:00"));

        return list;
    }

    /**
     * Nạp toàn bộ dữ liệu mẫu cho một người dùng cụ thể.
     * Tự động tạo tài khoản và danh mục nếu chưa có.
     *
     * @param username Tên đăng nhập người dùng cần nạp dữ liệu
     * @param nam Năm cần tạo dữ liệu (ví dụ: 2025)
     * @return true nếu nạp thành công, false nếu có lỗi
     */
    public static boolean taoDuLieuMauChoNguoiDung(String username, int nam) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        username = username.trim();

        // 1. Kiểm tra tài khoản người dùng, nếu chưa có thì tạo mới
        int userId = nguoiDungDAO.layIdTheoUsername(username);
        if (userId == -1) {
            NguoiDung userMoi = new NguoiDung(username, "123456", username + "@gmail.com");
            nguoiDungDAO.dangKy(userMoi);
            userId = nguoiDungDAO.layIdTheoUsername(username);
            System.out.println("-> Đã tự động tạo tài khoản mới: " + username + " (Mật khẩu: 123456)");
        }

        // 2. Tự động khởi tạo 9 danh mục chuẩn nếu chưa có
        danhMucDAO.khoiTaoDanhMucNeuChuaCo(username);

        // 3. Xóa các giao dịch cũ của năm đó để tránh nhân đôi dữ liệu
        String sqlXoa = "DELETE FROM transactions WHERE username = ? AND YEAR(date) = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstXoa = con.prepareStatement(sqlXoa)) {
            pstXoa.setString(1, username);
            pstXoa.setInt(2, nam);
            int rowsDeleted = pstXoa.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("-> Đã làm mới " + rowsDeleted + " giao dịch cũ của năm " + nam);
            }
        } catch (SQLException e) {
            System.err.println("Cảnh báo khi làm mới giao dịch cũ: " + e.getMessage());
        }

        // 4. Chèn danh sách giao dịch mẫu 12 tháng
        List<GiaoDichMau> danhSach = layDanhSachGiaoDichMau1Nam(nam);
        String sqlChen = "INSERT INTO transactions (username, category_name, description, amount, type, date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstChen = con.prepareStatement(sqlChen)) {
            con.setAutoCommit(false); // Dùng Transaction để tối ưu tốc độ và an toàn dữ liệu

            for (GiaoDichMau gd : danhSach) {
                pstChen.setString(1, username);
                pstChen.setString(2, gd.categoryName);
                pstChen.setString(3, gd.description);
                pstChen.setDouble(4, gd.amount);
                pstChen.setString(5, gd.type);
                pstChen.setString(6, gd.dateTime);
                pstChen.addBatch();
            }

            pstChen.executeBatch();
            con.commit();
            con.setAutoCommit(true);
            System.out.println("✅ Đã nạp thành công " + danhSach.size() + " giao dịch mẫu cho năm " + nam + " (Tài khoản: " + username + ")");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi chèn giao dịch mẫu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Phương thức main để chạy độc lập từ IDE (IntelliJ IDEA, Eclipse) hoặc dòng lệnh.
     */
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("   KHỞI TẠO BỘ DỮ LIỆU MẪU 1 NĂM (EXPENSE TRACKER)     ");
        System.out.println("=======================================================");

        // Mặc định nạp cho tài khoản 'demo'
        String taiKhoanMacDinh = "demo";
        int namMau = 2025;

        // Nếu trong hệ thống đã có tài khoản người dùng khác, lấy tài khoản đầu tiên để nạp thêm
        List<NguoiDung> danhSachUser = nguoiDungDAO.layTatCa();
        if (danhSachUser != null && !danhSachUser.isEmpty()) {
            for (NguoiDung u : danhSachUser) {
                System.out.println("Đang nạp dữ liệu cho tài khoản: " + u.getUsername() + "...");
                taoDuLieuMauChoNguoiDung(u.getUsername(), namMau);
            }
        } else {
            System.out.println("Chưa có người dùng nào trong CSDL. Khởi tạo tài khoản '" + taiKhoanMacDinh + "'...");
            taoDuLieuMauChoNguoiDung(taiKhoanMacDinh, namMau);
        }

        // In tổng kết tài chính để người dùng theo dõi
        inTongKetTaiChinh(taiKhoanMacDinh);
        System.out.println("=======================================================");
        System.out.println("Hoàn tất! Bây giờ bạn có thể mở ứng dụng và trải nghiệm.");
    }

    /**
     * In tổng kết tài chính (Tổng thu, tổng chi, số dư) ra Console.
     */
    private static void inTongKetTaiChinh(String username) {
        String sql = "SELECT " +
                "COUNT(*) AS tong_so, " +
                "SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) AS tong_thu, " +
                "SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) AS tong_chi " +
                "FROM transactions WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int tongSo = rs.getInt("tong_so");
                    double tongThu = rs.getDouble("tong_thu");
                    double tongChi = rs.getDouble("tong_chi");
                    double soDu = tongThu - tongChi;

                    System.out.println("-------------------------------------------------------");
                    System.out.println("THỐNG KÊ TÀI KHOẢN: " + username);
                    System.out.println("- Tổng số lượng giao dịch : " + tongSo);
                    System.out.println("- Tổng Thu Nhập           : " + DinhDangTienHelper.dinhDangTienVND(tongThu));
                    System.out.println("- Tổng Chi Tiêu           : " + DinhDangTienHelper.dinhDangTienVND(tongChi));
                    System.out.println("- Số Dư Hiện Tại          : " + DinhDangTienHelper.dinhDangTienVND(soDu));
                    System.out.println("-------------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê: " + e.getMessage());
        }
    }
}
