package model;

import util.DinhDangTienHelper;

/**
 * Lớp trừu tượng GiaoDich (Abstract Class Transaction)
 * Định nghĩa cấu trúc chung và phương thức cho mọi giao dịch tài chính trong hệ thống.
 * Thể hiện tính Trừu tượng (Abstraction) và Đóng gói (Encapsulation) trong OOP.
 */
public abstract class GiaoDich {
    protected int id;
    protected String username;
    protected String categoryName;
    protected String description;
    protected double amount;
    protected String type; // "income" hoặc "expense"
    protected String dateTime; // Định dạng "yyyy-MM-dd HH:mm:ss"

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

    /**
     * Phương thức tiện ích lấy số tiền định dạng chuỗi VNĐ.
     */
    public String getSoTienDinhDang() {
        return DinhDangTienHelper.dinhDangTienVND(this.amount);
    }

    /**
     * Phương thức trừu tượng: Lấy nhãn hiển thị loại giao dịch bằng tiếng Việt.
     * Các lớp con phải tự triển khai cụ thể (Tính Đa hình - Polymorphism).
     */
    public abstract String getLoaiHienThi();

    /**
     * Phương thức trừu tượng: Lấy mã màu hiển thị đại diện cho loại giao dịch.
     */
    public abstract String getMauSacMacDinh();

    /**
     * Kiểm tra xem có phải là khoản thu hay không.
     */
    public boolean laKhoanThu() {
        return "income".equalsIgnoreCase(this.type);
    }

    /**
     * Kiểm tra xem có phải là khoản chi hay không.
     */
    public boolean laKhoanChi() {
        return "expense".equalsIgnoreCase(this.type);
    }
}

