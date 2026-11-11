package model;

import util.DinhDangTienHelper;

/**
 * Lớp trừu tượng GiaoDich (Abstract Class Transaction)
 * Định nghĩa cấu trúc chung và phương thức cho mọi giao dịch tài chính trong hệ thống.
 * Thể hiện tính Trừu tượng (Abstraction) và Đóng gói (Encapsulation) trong OOP.
 * Bổ sung hỗ trợ Đa ví (viId, tenVi).
 */
public abstract class GiaoDich {
    protected int id;
    protected String username;
    protected String categoryName;
    protected String description;
    protected double amount;
    protected String type; // "income" hoặc "expense"
    protected String dateTime; // Định dạng "yyyy-MM-dd HH:mm:ss"
    protected Integer viId; // Mã ví thanh toán
    protected String tenVi; // Tên ví thanh toán

    public GiaoDich() {
    }

    public GiaoDich(String username, String categoryName, String description, double amount, String type) {
        this.username = username;
        this.categoryName = categoryName;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    public GiaoDich(int id, String username, String categoryName, String description, double amount, String type, String dateTime) {
        this.id = id;
        this.username = username;
        this.categoryName = categoryName;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.dateTime = dateTime;
    }

    public GiaoDich(int id, String username, String categoryName, String description, double amount, String type, String dateTime, Integer viId, String tenVi) {
        this.id = id;
        this.username = username;
        this.categoryName = categoryName;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.dateTime = dateTime;
        this.viId = viId;
        this.tenVi = tenVi;
    }

    // Các phương thức getter và setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getViId() {
        return viId;
    }

    public void setViId(Integer viId) {
        this.viId = viId;
    }

    public String getTenVi() {
        return tenVi != null ? tenVi : "Mặc định";
    }

    public void setTenVi(String tenVi) {
        this.tenVi = tenVi;
    }

    /**
     * Phương thức tiện ích lấy số tiền định dạng chuỗi VNĐ.
     */
    public String getSoTienDinhDang() {
        return DinhDangTienHelper.dinhDangVND(amount);
    }

    /**
     * Phương thức trừu tượng trả về loại giao dịch hiển thị bằng tiếng Việt.
     * Thể hiện tính Đa hình (Polymorphism) qua các lớp con KhoanThu và KhoanChi.
     */
    public abstract String getLoaiHienThi();

    /**
     * Phương thức trừu tượng trả về mã màu hex mặc định đại diện cho loại giao dịch.
     * KhoanThu thường màu xanh lá cây (#10B981), KhoanChi thường màu đỏ cam (#EF4444).
     */
    public abstract String getMauSacMacDinh();

    @Override
    public String toString() {
        return "GiaoDich{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", viId=" + viId +
                ", tenVi='" + tenVi + '\'' +
                '}';
    }
}
