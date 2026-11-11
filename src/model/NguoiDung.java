package model;

/**
 * Lớp thực thể NguoiDung (User Entity)
 * Đại diện cho thông tin tài khoản người dùng trong hệ thống.
 * Hỗ trợ phân quyền Quản trị viên (ADMIN) và Trạng thái tài khoản (ACTIVE/LOCKED).
 */
public class NguoiDung {
    private int id;
    private String username;
    private String password;
    private String email;
    private String vaiTro = "USER";     // Mặc định: "USER" hoặc "ADMIN"
    private String trangThai = "ACTIVE"; // Mặc định: "ACTIVE" hoặc "LOCKED"

    // Constructor mặc định
    public NguoiDung() {
    }

    // Constructor dùng khi đăng ký tài khoản mới (chưa có id)
    public NguoiDung(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.vaiTro = "USER";
        this.trangThai = "ACTIVE";
    }

    // Constructor đầy đủ thông tin cơ bản
    public NguoiDung(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.vaiTro = "USER";
        this.trangThai = "ACTIVE";
    }

    // Constructor đầy đủ cả vai trò và trạng thái
    public NguoiDung(int id, String username, String password, String email, String vaiTro, String trangThai) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.vaiTro = (vaiTro != null) ? vaiTro : "USER";
        this.trangThai = (trangThai != null) ? trangThai : "ACTIVE";
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Kiểm tra xem người dùng có phải là Quản trị viên (ADMIN) không.
     */
    public boolean laAdmin() {
        return "ADMIN".equalsIgnoreCase(this.vaiTro);
    }

    /**
     * Kiểm tra xem tài khoản có đang bị khóa (LOCKED) không.
     */
    public boolean daBiKhoa() {
        return "LOCKED".equalsIgnoreCase(this.trangThai);
    }

    @Override
    public String toString() {
        return "NguoiDung{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", vaiTro='" + vaiTro + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
