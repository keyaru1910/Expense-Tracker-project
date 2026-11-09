-- ====================================================================
-- TỆP DỮ LIỆU GIAO DỊCH MẪU 1 NĂM (DEMO EXPENSE TRACKER)
-- Phục vụ trình diễn:
--   1. Màn hình "Lịch sử giao dịch" (Tìm kiếm, bộ lọc, xem chi tiết, tính số dư)
--   2. Màn hình "Thống kê & Phân tích" (Biểu đồ cột đôi 12 tháng so sánh Thu/Chi)
-- ====================================================================

-- 1. Chọn Cơ sở dữ liệu và thiết lập bảng mã UTF-8 tiếng Việt
CREATE DATABASE IF NOT EXISTS expense_tracker
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE expense_tracker;

SET NAMES utf8mb4;

-- 2. Đảm bảo cấu trúc các bảng tồn tại đầy đủ
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100),
    password VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(15,2) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type ENUM('income', 'expense') NOT NULL DEFAULT 'expense'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ====================================================================
-- 3. CẤU HÌNH TÀI KHOẢN MẪU
-- Bạn có thể đổi 'demo' thành username bạn đã tạo để nạp trực tiếp!
-- ====================================================================
SET @DEMO_USER = 'demo';
SET @DEMO_PASS = '123456';
SET @DEMO_EMAIL = 'demo@gmail.com';

-- Tạo tài khoản demo nếu chưa có (Mật khẩu: 123456)
INSERT INTO users (username, password, email)
SELECT @DEMO_USER, @DEMO_PASS, @DEMO_EMAIL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = @DEMO_USER);

-- Lấy ID của người dùng demo
SET @USER_ID = (SELECT id FROM users WHERE username = @DEMO_USER LIMIT 1);

-- Khởi tạo 9 danh mục chuẩn với mã màu nếu chưa có cho tài khoản này
INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Ăn uống', '#FF5722'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Ăn uống');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Mua sắm', '#E91E63'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Mua sắm');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Di chuyển', '#2196F3'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Di chuyển');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Hóa đơn & Tiện ích', '#FF9800'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Hóa đơn & Tiện ích');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Giải trí', '#9C27B0'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Giải trí');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Sức khỏe', '#F44336'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Sức khỏe');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Tiền lương', '#4CAF50'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Tiền lương');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Thưởng & Đầu tư', '#009688'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Thưởng & Đầu tư');

INSERT INTO categories (user_id, name, color)
SELECT @USER_ID, 'Khác', '#607D8B'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE user_id = @USER_ID AND name = 'Khác');

-- Xóa bớt các giao dịch cũ của tài khoản demo (để tránh bị trùng lặp khi chạy lại nhiều lần)
DELETE FROM transactions WHERE username = @DEMO_USER;

-- ====================================================================
-- 4. BỘ DỮ LIỆU GIAO DỊCH MẪU TRỌN VẸN 12 THÁNG NĂM 2025
-- Đầy đủ các danh mục, số tiền thực tế, phân bổ đều khắp các tháng.
-- ====================================================================

INSERT INTO transactions (username, category_name, description, amount, type, date) VALUES
-- === THÁNG 1/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 01/2025', 22000000, 'income', '2025-01-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Thanh toán tiền thuê căn hộ tháng 01', 4500000, 'expense', '2025-01-05 14:30:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền điện thoại và cước 4G tháng 01', 200000, 'expense', '2025-01-07 10:15:00'),
(@DEMO_USER, 'Ăn uống', 'Đi siêu thị WinMart mua thực phẩm tuần 1', 850000, 'expense', '2025-01-10 18:30:00'),
(@DEMO_USER, 'Di chuyển', 'Đổ xăng xe máy và bảo dưỡng thay nhớt định kỳ', 320000, 'expense', '2025-01-12 11:45:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận tiền thưởng Tết Dương Lịch', 5000000, 'income', '2025-01-15 14:00:00'),
(@DEMO_USER, 'Giải trí', 'Đi xem phim rạp và cà phê cuối tuần cùng bạn bè', 350000, 'expense', '2025-01-18 20:00:00'),
(@DEMO_USER, 'Mua sắm', 'Mua sắm áo khoác ấm và giày dép mới chuẩn bị Tết', 1850000, 'expense', '2025-01-22 17:00:00'),
(@DEMO_USER, 'Sức khỏe', 'Mua vitamin C và thực phẩm bổ sung sức khỏe', 450000, 'expense', '2025-01-25 09:30:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Thu nhập làm thêm dự án Freelance thiết kế Web', 4500000, 'income', '2025-01-28 16:00:00'),

-- === THÁNG 2/2025 (Tháng Tết Nguyên Đán) ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 02/2025', 22000000, 'income', '2025-02-05 09:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận thưởng Tết Âm lịch & Lương tháng 13', 28000000, 'income', '2025-02-05 14:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Thanh toán tiền thuê căn hộ tháng 02', 4500000, 'expense', '2025-02-06 10:00:00'),
(@DEMO_USER, 'Di chuyển', 'Mua vé máy bay khứ hồi về quê đón Tết', 4200000, 'expense', '2025-02-08 16:30:00'),
(@DEMO_USER, 'Mua sắm', 'Mua bánh kẹo, hoa đào và giỏ quà Tết biếu gia đình', 5600000, 'expense', '2025-02-12 15:00:00'),
(@DEMO_USER, 'Ăn uống', 'Mua sắm mâm cỗ tất niên và thực phẩm Tết', 2500000, 'expense', '2025-02-14 11:30:00'),
(@DEMO_USER, 'Khác', 'Mừng tuổi lì xì đầu năm cho bố mẹ và các cháu', 6500000, 'expense', '2025-02-17 10:00:00'),
(@DEMO_USER, 'Ăn uống', 'Đi liên hoan tân niên họp mặt bạn cấp 3 đầu xuân', 1200000, 'expense', '2025-02-20 19:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận tiền lì xì may mắn và lộc xuân', 2500000, 'income', '2025-02-24 14:00:00'),

-- === THÁNG 3/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 03/2025', 22000000, 'income', '2025-03-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 03', 4500000, 'expense', '2025-03-05 11:00:00'),
(@DEMO_USER, 'Khác', 'Mua quà tặng mẹ ngày Quốc tế Phụ nữ 8/3', 1200000, 'expense', '2025-03-08 19:30:00'),
(@DEMO_USER, 'Ăn uống', 'Ăn trưa văn phòng và cà phê giao lưu tuần 2', 680000, 'expense', '2025-03-12 12:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận lãi gửi tiết kiệm ngân hàng kỳ hạn 6 tháng', 2400000, 'income', '2025-03-15 15:00:00'),
(@DEMO_USER, 'Sức khỏe', 'Đăng ký gói tập gym rèn luyện sức khỏe 3 tháng', 1500000, 'expense', '2025-03-18 17:30:00'),
(@DEMO_USER, 'Di chuyển', 'Đổ xăng và rửa xe máy định kỳ', 250000, 'expense', '2025-03-22 09:00:00'),
(@DEMO_USER, 'Giải trí', 'Xem kịch sân khấu và ăn tối cuối tuần', 650000, 'expense', '2025-03-26 20:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Hóa đơn tiền điện nước sinh hoạt tháng 3', 980000, 'expense', '2025-03-29 16:00:00'),

-- === THÁNG 4/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 04/2025', 22000000, 'income', '2025-04-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê căn hộ tháng 04', 4500000, 'expense', '2025-04-05 10:30:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận tiền thưởng hoàn thành dự án Quý 1', 8000000, 'income', '2025-04-10 14:00:00'),
(@DEMO_USER, 'Ăn uống', 'Mua đồ siêu thị dự trữ thực phẩm', 1150000, 'expense', '2025-04-14 18:30:00'),
(@DEMO_USER, 'Mua sắm', 'Mua sách chuyên ngành công nghệ phần mềm', 450000, 'expense', '2025-04-18 11:00:00'),
(@DEMO_USER, 'Di chuyển', 'Đi lại bằng GrabCar trong tuần mưa bão', 420000, 'expense', '2025-04-22 08:30:00'),
(@DEMO_USER, 'Sức khỏe', 'Mua thuốc cảm cúm thời tiết và khẩu trang', 280000, 'expense', '2025-04-26 15:00:00'),
(@DEMO_USER, 'Giải trí', 'Đặt phòng khách sạn du lịch dịp nghỉ lễ 30/4 - 1/5', 4800000, 'expense', '2025-04-29 17:00:00'),
(@DEMO_USER, 'Ăn uống', 'Ăn uống hải sản tươi sống trong kỳ nghỉ lễ', 1850000, 'expense', '2025-04-30 20:00:00'),

-- === THÁNG 5/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 05/2025', 22000000, 'income', '2025-05-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 05', 4500000, 'expense', '2025-05-05 11:30:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Thu nhập phụ: Tư vấn giải pháp kỹ thuật online', 4500000, 'income', '2025-05-08 14:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Hóa đơn tiền điện mùa nóng chạy điều hòa', 1650000, 'expense', '2025-05-12 16:00:00'),
(@DEMO_USER, 'Ăn uống', 'Ăn trưa buffet liên hoan cùng đồng nghiệp', 750000, 'expense', '2025-05-15 12:30:00'),
(@DEMO_USER, 'Mua sắm', 'Mua quạt cây làm mát và phụ kiện bàn làm việc', 920000, 'expense', '2025-05-19 19:00:00'),
(@DEMO_USER, 'Di chuyển', 'Đổ xăng xe máy và sửa chữa săm lốp', 280000, 'expense', '2025-05-23 09:15:00'),
(@DEMO_USER, 'Giải trí', 'Cà phê Acoustic cuối tuần cùng đồng nghiệp', 320000, 'expense', '2025-05-27 21:00:00'),
(@DEMO_USER, 'Ăn uống', 'Mua đồ ăn tươi ngon nấu ăn tại nhà', 850000, 'expense', '2025-05-30 18:00:00'),

-- === THÁNG 6/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 06/2025', 22000000, 'income', '2025-06-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 06', 4500000, 'expense', '2025-06-05 10:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận tiền chi trả cổ tức chứng khoán an toàn', 3800000, 'income', '2025-06-10 15:30:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Hóa đơn tiền điện mùa hè cao điểm', 1820000, 'expense', '2025-06-14 18:00:00'),
(@DEMO_USER, 'Di chuyển', 'Vé xe limousine cao cấp đi công tác Hải Phòng', 450000, 'expense', '2025-06-17 08:30:00'),
(@DEMO_USER, 'Giải trí', 'Du lịch hè Đà Nẵng - Hội An cùng gia đình', 6500000, 'expense', '2025-06-20 16:00:00'),
(@DEMO_USER, 'Ăn uống', 'Thưởng thức ẩm thực và đặc sản miền Trung', 1450000, 'expense', '2025-06-22 19:30:00'),
(@DEMO_USER, 'Mua sắm', 'Mua đồ bơi, kính râm và mũ đi biển', 850000, 'expense', '2025-06-25 14:00:00'),
(@DEMO_USER, 'Sức khỏe', 'Mua kem chống nắng bảo vệ da và xịt khoáng', 520000, 'expense', '2025-06-28 10:30:00'),

-- === THÁNG 7/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 07/2025', 22000000, 'income', '2025-07-05 09:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Thưởng đánh giá năng lực giữa năm (Mid-year Bonus)', 12000000, 'income', '2025-07-05 14:30:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 07', 4500000, 'expense', '2025-07-06 10:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Thanh toán cước internet cáp quang gói 6 tháng', 1320000, 'expense', '2025-07-09 17:00:00'),
(@DEMO_USER, 'Ăn uống', 'Mua đồ ăn sạch tại siêu thị thực phẩm hữu cơ', 1100000, 'expense', '2025-07-13 11:30:00'),
(@DEMO_USER, 'Sức khỏe', 'Khám răng định kỳ, lấy cao răng tại nha khoa', 850000, 'expense', '2025-07-17 15:00:00'),
(@DEMO_USER, 'Giải trí', 'Mua vé xem hòa nhạc ngoài trời', 750000, 'expense', '2025-07-21 20:00:00'),
(@DEMO_USER, 'Di chuyển', 'Đổ xăng và thay má phanh xe máy an toàn', 350000, 'expense', '2025-07-25 09:00:00'),
(@DEMO_USER, 'Khác', 'Đăng ký khóa học lập trình và thi chứng chỉ Cloud', 2800000, 'expense', '2025-07-28 16:30:00'),

-- === THÁNG 8/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 08/2025', 22000000, 'income', '2025-08-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 08', 4500000, 'expense', '2025-08-05 10:30:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận tiền thù lao viết tài liệu kỹ thuật', 3500000, 'income', '2025-08-09 15:00:00'),
(@DEMO_USER, 'Ăn uống', 'Ăn lẩu nướng liên hoan nhóm hoàn thành dự án', 950000, 'expense', '2025-08-12 18:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Hóa đơn tiền điện nước sinh hoạt tháng 8', 1250000, 'expense', '2025-08-16 11:00:00'),
(@DEMO_USER, 'Sức khỏe', 'Gói khám sức khỏe tổng quát định kỳ tại bệnh viện', 2200000, 'expense', '2025-08-19 16:30:00'),
(@DEMO_USER, 'Khác', 'Mua quà mừng sinh nhật bạn thân', 850000, 'expense', '2025-08-23 19:30:00'),
(@DEMO_USER, 'Di chuyển', 'Chi phí xăng xe đi làm trong tháng', 310000, 'expense', '2025-08-26 08:30:00'),
(@DEMO_USER, 'Giải trí', 'Chơi bowling và xem phim cuối tuần xả stress', 480000, 'expense', '2025-08-29 20:30:00'),

-- === THÁNG 9/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 09/2025', 22000000, 'income', '2025-09-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 09', 4500000, 'expense', '2025-09-05 11:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận tiền lãi trái phiếu an toàn', 2100000, 'income', '2025-09-07 15:00:00'),
(@DEMO_USER, 'Ăn uống', 'Mua thực phẩm tươi sống và trà hoa quả', 880000, 'expense', '2025-09-10 18:30:00'),
(@DEMO_USER, 'Di chuyển', 'Đổ xăng xe máy và thay nhớt Motul', 380000, 'expense', '2025-09-14 09:00:00'),
(@DEMO_USER, 'Mua sắm', 'Mua bàn phím cơ không dây phục vụ công việc', 1650000, 'expense', '2025-09-18 16:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền điện nước và phí quản lý chung cư', 1100000, 'expense', '2025-09-21 14:00:00'),
(@DEMO_USER, 'Giải trí', 'Chuyến đi cắm trại dã ngoại Ba Vì cuối tuần', 1350000, 'expense', '2025-09-25 19:00:00'),
(@DEMO_USER, 'Sức khỏe', 'Mua dầu cá Omega 3 và men vi sinh tiêu hóa', 390000, 'expense', '2025-09-28 10:30:00'),

-- === THÁNG 10/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 10/2025', 22000000, 'income', '2025-10-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 10', 4500000, 'expense', '2025-10-05 10:30:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận thưởng hoàn thành dự án Quý 3', 6500000, 'income', '2025-10-10 14:00:00'),
(@DEMO_USER, 'Ăn uống', 'Mua đồ siêu thị Co.opmart', 1050000, 'expense', '2025-10-14 18:00:00'),
(@DEMO_USER, 'Khác', 'Mua quà 20/10 gửi tặng mẹ ở quê', 1500000, 'expense', '2025-10-18 16:00:00'),
(@DEMO_USER, 'Ăn uống', 'Ăn tối liên hoan ngày Phụ nữ Việt Nam 20/10', 950000, 'expense', '2025-10-20 19:30:00'),
(@DEMO_USER, 'Di chuyển', 'Đổ xăng và đóng tiền vé gửi xe tháng', 300000, 'expense', '2025-10-23 09:00:00'),
(@DEMO_USER, 'Mua sắm', 'Mua áo khoác thu đông đón gió mùa mới', 980000, 'expense', '2025-10-26 15:30:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền điện sinh hoạt và nước tháng 10', 950000, 'expense', '2025-10-29 11:00:00'),

-- === THÁNG 11/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 11/2025', 22000000, 'income', '2025-11-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 11', 4500000, 'expense', '2025-11-05 11:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Thu nhập nhận từ dự án tư vấn giải pháp ngoài giờ', 5200000, 'income', '2025-11-08 15:00:00'),
(@DEMO_USER, 'Ăn uống', 'Ăn uống sinh hoạt và cà phê sáng cả tháng', 1250000, 'expense', '2025-11-12 18:30:00'),
(@DEMO_USER, 'Di chuyển', 'Chi phí xăng xe máy và đi lại GrabCar', 480000, 'expense', '2025-11-15 10:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Hóa đơn tiền điện và phí dịch vụ chung cư', 890000, 'expense', '2025-11-19 14:30:00'),
(@DEMO_USER, 'Giải trí', 'Đi xem kịch sân khấu và uống trà sữa cuối tuần', 450000, 'expense', '2025-11-23 19:00:00'),
(@DEMO_USER, 'Mua sắm', 'Săn sale Black Friday: Mua màn hình máy tính 27 inch', 5800000, 'expense', '2025-11-27 20:00:00'),
(@DEMO_USER, 'Sức khỏe', 'Mua trà thảo mộc đông y bồi bổ sức khỏe', 320000, 'expense', '2025-11-29 16:00:00'),

-- === THÁNG 12/2025 ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 12/2025', 22000000, 'income', '2025-12-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê nhà tháng 12', 4500000, 'expense', '2025-12-05 10:30:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Nhận tiền thưởng hoàn thành xuất sắc KPI cả năm', 20000000, 'income', '2025-12-10 14:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Tất toán lãi tiền gửi tiết kiệm năm 2025', 4600000, 'income', '2025-12-12 16:30:00'),
(@DEMO_USER, 'Ăn uống', 'Tiệc liên hoan tất niên công ty và giao lưu đối tác', 1500000, 'expense', '2025-12-15 18:00:00'),
(@DEMO_USER, 'Mua sắm', 'Mua cây thông Noel và phụ kiện trang trí phòng', 850000, 'expense', '2025-12-18 20:00:00'),
(@DEMO_USER, 'Giải trí', 'Chuyến đi nghỉ dưỡng Sapa mùa đông ngắm mây tuyết', 4800000, 'expense', '2025-12-22 17:00:00'),
(@DEMO_USER, 'Ăn uống', 'Bữa tối Giáng Sinh ấm cúng tại nhà hàng', 1350000, 'expense', '2025-12-25 19:30:00'),
(@DEMO_USER, 'Di chuyển', 'Vé tàu hỏa khứ hồi và xăng xe về thăm gia đình', 850000, 'expense', '2025-12-28 10:00:00'),
(@DEMO_USER, 'Sức khỏe', 'Đóng phí gia hạn bảo hiểm sức khỏe năm 2026', 3200000, 'expense', '2025-12-30 15:00:00'),

-- === DỮ LIỆU ĐẦU NĂM 2026 (Demo năm hiện tại) ===
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 01/2026 (Mức lương mới tăng bậc)', 25000000, 'income', '2026-01-05 09:00:00'),
(@DEMO_USER, 'Hóa đơn & Tiện ích', 'Tiền thuê căn hộ tháng 01/2026', 4500000, 'expense', '2026-01-05 11:00:00'),
(@DEMO_USER, 'Ăn uống', 'Mua đồ siêu thị đầu năm mới 2026', 950000, 'expense', '2026-01-10 18:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Thưởng Tết Dương Lịch 2026', 6000000, 'income', '2026-01-15 14:00:00'),
(@DEMO_USER, 'Giải trí', 'Xem phim rạp cuối tuần xả hơi', 380000, 'expense', '2026-01-20 19:30:00'),
(@DEMO_USER, 'Tiền lương', 'Nhận lương tháng 02/2026', 25000000, 'income', '2026-02-05 09:00:00'),
(@DEMO_USER, 'Thưởng & Đầu tư', 'Thưởng Tết Nguyên Đán Bính Ngọ 2026', 32000000, 'income', '2026-02-08 14:00:00'),
(@DEMO_USER, 'Mua sắm', 'Mua sắm Tết Bính Ngọ 2026 cho cả gia đình', 6800000, 'expense', '2026-02-10 10:00:00'),
(@DEMO_USER, 'Di chuyển', 'Mua vé máy bay về quê đón Tết', 4500000, 'expense', '2026-02-12 16:00:00'),
(@DEMO_USER, 'Khác', 'Mừng tuổi đầu xuân năm mới cho người thân', 7000000, 'expense', '2026-02-15 11:00:00');

-- ====================================================================
-- KIỂM TRA KẾT QUẢ NẠP DỮ LIỆU
-- ====================================================================
SELECT 
    username,
    COUNT(*) AS 'Tổng số giao dịch',
    SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) AS 'Tổng Thu Nhập (VNĐ)',
    SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) AS 'Tổng Chi Tiêu (VNĐ)',
    (SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) - SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END)) AS 'Số Dư (VNĐ)'
FROM transactions 
WHERE username = @DEMO_USER
GROUP BY username;

