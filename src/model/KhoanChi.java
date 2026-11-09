package model;

/**
 * Lớp thực thể KhoanChi (Expense Transaction)
 * Kế thừa từ lớp trừu tượng GiaoDich (Inheritance trong OOP).
 * Đại diện cho các khoản chi tiêu tiền ra khỏi tài khoản.
 */
public class KhoanChi extends GiaoDich {

    public KhoanChi() {
        this.type = "expense";
    }

    public KhoanChi(String username, String categoryName, String description, double amount) {
        super(username, categoryName, description, amount, "expense");
    }

    public KhoanChi(int id, String username, String categoryName, String description, double amount, String dateTime) {
        super(id, username, categoryName, description, amount, "expense", dateTime);
    }

    @Override
    public String getLoaiHienThi() {
        return "Chi tiêu";
    }

    @Override
    public String getMauSacMacDinh() {
        return "#EF4444"; // Màu đỏ tươi hiện đại
    }
}

