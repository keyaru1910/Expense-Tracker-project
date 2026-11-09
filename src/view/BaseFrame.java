package view;

import util.GiaoDienHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Lớp trừu tượng BaseFrame (Khung giao diện cơ sở).
 * Kế thừa từ javax.swing.JFrame để định hình cấu trúc chung cho toàn bộ màn hình trong ứng dụng.
 * Áp dụng tính Kế thừa (Inheritance) và khuôn mẫu phương thức (Template Method Pattern).
 */
public abstract class BaseFrame extends JFrame {

    public BaseFrame(String tieuDe, int chieuRong, int chieuCao) {
        super(tieuDe);
        khoiTaoKhungCoSo(chieuRong, chieuCao);
    }

    /**
     * Cài đặt thuộc tính chung của cửa sổ ứng dụng.
     */
    private void khoiTaoKhungCoSo(int chieuRong, int chieuCao) {
        setSize(chieuRong, chieuCao);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Cài đặt biểu tượng (Icon) logo cho thanh tiêu đề cửa sổ và Taskbar Windows
        caiDatIconCuaSo();

        // Khởi tạo các thành phần giao diện và sự kiện do lớp con định nghĩa
        khoiTaoThanhPhan();
        dangKySuKien();
    }

    /**
     * Cài đặt biểu tượng (Logo Icon) hiển thị trên thanh tiêu đề cửa sổ và Taskbar Windows.
     */
    private void caiDatIconCuaSo() {
        Image logo = GiaoDienHelper.getLogoUngDung();
        if (logo != null) {
            setIconImage(logo);
        }
    }

    /**
     * Phương thức trừu tượng: Khởi tạo các thành phần giao diện (UI Components).
     */
    protected abstract void khoiTaoThanhPhan();

    /**
     * Phương thức trừu tượng: Đăng ký các sự kiện tương tác của người dùng (Event Listeners).
     */
    protected abstract void dangKySuKien();

    /**
     * Chuyển hướng sang một cửa sổ mới và đóng cửa sổ hiện tại một cách an toàn.
     */
    public void chuyenManHinh(JFrame manHinhMoi) {
        manHinhMoi.setVisible(true);
        this.dispose();
    }
}

