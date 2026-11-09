package model;

/**
 * Lớp thực thể NguoiDung (User Entity)
 * Đại diện cho thông tin tài khoản người dùng trong hệ thống.
 */
public class NguoiDung {
    private int id;
    private String username;
    private String password;
    private String email;

    // Constructor mặc định
    public NguoiDung() {
    }

    // Constructor dùng khi đăng ký tài khoản mới (chưa có id)
    public NguoiDung(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Constructor đầy đủ
    public NguoiDung(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
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

    @Override
    public String toString() {
        return "NguoiDung{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

