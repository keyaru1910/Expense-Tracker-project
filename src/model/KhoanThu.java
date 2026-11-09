package model;

/**
 * Lớp thực thể KhoanThu (Income Transaction)
 * Kế thừa từ lớp trừu tượng GiaoDich (Inheritance trong OOP).
 * Đại diện cho các giao dịch thu nhập tiền vào tài khoản.
 */
public class KhoanThu extends GiaoDich {

    public KhoanThu() {
        this.type = "income";
    }

    public KhoanThu(String username, String categoryName, String description, double amount) {
        super(username, categoryName, description, amount, "income");
    }

    public KhoanThu(int id, String username, String categoryName, String description, double amount, String dateTime) {
        super(id, username, categoryName, description, amount, "income", dateTime);
    }

    @Override
    public String getLoaiHienThi() {
        return "Thu nhập";
    }

    @Override
    public String getMauSacMacDinh() {
        return "#22C55E"; // Màu xanh lá cây hiện đại
    }
}

