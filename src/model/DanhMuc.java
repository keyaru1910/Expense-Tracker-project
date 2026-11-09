package model;

/**
 * Lớp thực thể DanhMuc (Category Entity)
 * Đại diện cho một danh mục thu/chi kèm mã màu hiển thị.
 */
public class DanhMuc {
    private int id;
    private int userId;
    private String name;
    private String colorHex;

    public DanhMuc() {
    }

    public DanhMuc(String name, String colorHex) {
        this.name = name;
        this.colorHex = colorHex;
    }

    public DanhMuc(int id, int userId, String name, String colorHex) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.colorHex = colorHex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    @Override
    public String toString() {
        return name;
    }
}

