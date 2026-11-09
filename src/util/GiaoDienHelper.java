package util;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Lớp hỗ trợ quản lý và đồng bộ giao diện (Theme) cho toàn bộ ứng dụng Expense Tracker.
 * Hỗ trợ giao diện Tối (mặc định), giao diện Sáng và giao diện IntelliJ.
 */
public class GiaoDienHelper {
    public static final String THEME_TOI = "Giao diện Tối";
    public static final String THEME_SANG = "Giao diện Sáng";
    public static final String THEME_INTELLIJ = "Giao diện IntelliJ";

    // Giao diện mặc định của hệ thống là Giao diện Tối
    private static String tenGiaoDienHienTai = THEME_TOI;

    /**
     * Khởi tạo giao diện mặc định ban đầu là Giao diện Tối (FlatMacDarkLaf).
     */
    public static void khoiTaoGiaoDienMacDinh() {
        apDungGiaoDien(THEME_TOI);
    }

    /**
     * Lấy tên giao diện hiện tại đang kích hoạt.
     */
    public static String getTenGiaoDienHienTai() {
        return tenGiaoDienHienTai;
    }

    /**
     * Kiểm tra xem giao diện hiện tại có phải là giao diện tối không.
     */
    public static boolean laGiaoDienToi() {
        return FlatLaf.isLafDark();
    }

    /**
     * Chuyển đổi và áp dụng giao diện theo tên được chọn.
     */
    public static void apDungGiaoDien(String tenGiaoDien) {
        tenGiaoDienHienTai = tenGiaoDien;
        try {
            switch (tenGiaoDien) {
                case THEME_TOI:
                    UIManager.setLookAndFeel(new FlatMacDarkLaf());
                    break;
                case THEME_SANG:
                    UIManager.setLookAndFeel(new FlatMacLightLaf());
                    break;
                case THEME_INTELLIJ:
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    break;
                default:
                    UIManager.setLookAndFeel(new FlatMacDarkLaf());
                    tenGiaoDienHienTai = THEME_TOI;
                    break;
            }

            // Cấu hình phông chữ mặc định Segoe UI hỗ trợ tiếng Việt Unicode sắc nét
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 15));

            // Cập nhật lại UI cho tất cả cửa sổ Frame đang mở trong ứng dụng
            for (Frame frame : JFrame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }
        } catch (Exception ex) {
            System.err.println("Lỗi khi áp dụng giao diện: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Áp dụng màu sắc cho Thẻ (Card container) ở giữa màn hình.
     */
    public static void apDungMauTheCard(JPanel pnlCard) {
        if (pnlCard == null) return;

        if (laGiaoDienToi()) {
            pnlCard.setBackground(new Color(32, 33, 36));
            pnlCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 64, 67), 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
        } else {
            pnlCard.setBackground(new Color(255, 255, 255));
            pnlCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(218, 222, 229), 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
        }
    }

    /**
     * Áp dụng màu cho tiêu đề chào mừng hoặc tiêu đề trang.
     */
    public static void apDungMauTieuDe(JLabel lblTieuDe) {
        if (lblTieuDe == null) return;

        if (laGiaoDienToi()) {
            lblTieuDe.setForeground(new Color(135, 206, 250)); // Xanh nhạt sáng trên nền tối
        } else {
            lblTieuDe.setForeground(new Color(2, 132, 199));   // Xanh dương đậm trên nền sáng
        }
    }

    /**
     * Áp dụng màu và hiệu ứng hover cho các nút chức năng chính trên trang chủ.
     */
    public static void apDungMauNutChucNang(JButton btn) {
        if (btn == null) return;

        btn.setFocusPainted(false);
        btn.setOpaque(true);

        if (laGiaoDienToi()) {
            btn.setBackground(new Color(50, 50, 50));
            btn.setForeground(new Color(255, 255, 255));
            btn.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));
        } else {
            btn.setBackground(new Color(241, 245, 249));
            btn.setForeground(new Color(30, 41, 59));
            btn.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        }

        ganHieuUngHoverNut(btn);
    }

    /**
     * Áp dụng màu cho nút Đăng xuất (màu đỏ cảnh báo thanh lịch).
     */
    public static void apDungMauNutDangXuat(JButton btnDangXuat) {
        if (btnDangXuat == null) return;

        btnDangXuat.setFocusPainted(false);
        btnDangXuat.setOpaque(true);

        if (laGiaoDienToi()) {
            btnDangXuat.setBackground(new Color(50, 50, 50));
            btnDangXuat.setForeground(new Color(255, 100, 100));
            btnDangXuat.setBorder(BorderFactory.createLineBorder(new Color(80, 50, 50), 1));
        } else {
            btnDangXuat.setBackground(new Color(254, 242, 242));
            btnDangXuat.setForeground(new Color(220, 38, 38));
            btnDangXuat.setBorder(BorderFactory.createLineBorder(new Color(254, 202, 202), 1));
        }

        ganHieuUngHoverDangXuat(btnDangXuat);
    }

    /**
     * Tinh chỉnh màu cho nút Quay lại hoặc nút Hủy phụ.
     */
    public static void apDungMauNutPhu(JButton btnPhu) {
        if (btnPhu == null) return;

        btnPhu.setFocusPainted(false);
        btnPhu.setOpaque(true);

        if (laGiaoDienToi()) {
            btnPhu.setBackground(new Color(70, 70, 70));
            btnPhu.setForeground(new Color(200, 200, 200));
            btnPhu.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 1));
        } else {
            btnPhu.setBackground(new Color(243, 244, 246));
            btnPhu.setForeground(new Color(75, 85, 99));
            btnPhu.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
        }
    }

    /**
     * Tinh chỉnh Hộp chọn JComboBox (Theme selector)
     * Tránh lỗi chữ trắng trên nền xám nhạt trong popup dropdown khi dùng giao diện sáng.
     */
    public static void cauHinhHopChonGiaoDien(JComboBox<String> themeBox) {
        if (themeBox == null) return;

        themeBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                boolean dark = laGiaoDienToi();

                if (isSelected) {
                    label.setBackground(new Color(2, 132, 199));
                    label.setForeground(Color.WHITE);
                } else {
                    if (dark) {
                        label.setBackground(new Color(38, 40, 46));
                        label.setForeground(new Color(230, 230, 230));
                    } else {
                        label.setBackground(Color.WHITE);
                        label.setForeground(new Color(30, 41, 59));
                    }
                }
                return label;
            }
        });

        capNhatMauHopChon(themeBox);
    }

    /**
     * Cập nhật màu sắc bên ngoài của JComboBox theo theme hiện tại.
     */
    public static void capNhatMauHopChon(JComboBox<String> themeBox) {
        if (themeBox == null) return;

        if (laGiaoDienToi()) {
            themeBox.setBackground(new Color(45, 48, 55));
            themeBox.setForeground(new Color(240, 240, 240));
            themeBox.setBorder(BorderFactory.createLineBorder(new Color(70, 74, 84), 1));
        } else {
            themeBox.setBackground(new Color(255, 255, 255));
            themeBox.setForeground(new Color(30, 41, 59));
            themeBox.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        }
    }

    /**
     * Hiệu ứng rê chuột (Hover) cho nút chức năng.
     */
    private static void ganHieuUngHoverNut(JButton btn) {
        for (java.awt.event.MouseListener ml : btn.getMouseListeners()) {
            if ("ThemeHoverListener".equals(ml.getClass().getSimpleName())) {
                btn.removeMouseListener(ml);
            }
        }

        class ThemeHoverListener extends MouseAdapter {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (laGiaoDienToi()) {
                    btn.setBackground(new Color(65, 68, 75));
                } else {
                    btn.setBackground(new Color(226, 232, 240));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (laGiaoDienToi()) {
                    btn.setBackground(new Color(50, 50, 50));
                } else {
                    btn.setBackground(new Color(241, 245, 249));
                }
            }
        }

        btn.addMouseListener(new ThemeHoverListener());
    }

    /**
     * Hiệu ứng hover cho nút Đăng xuất.
     */
    private static void ganHieuUngHoverDangXuat(JButton btnDangXuat) {
        for (java.awt.event.MouseListener ml : btnDangXuat.getMouseListeners()) {
            if ("DangXuatHoverListener".equals(ml.getClass().getSimpleName())) {
                btnDangXuat.removeMouseListener(ml);
            }
        }

        class DangXuatHoverListener extends MouseAdapter {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (laGiaoDienToi()) {
                    btnDangXuat.setBackground(new Color(80, 40, 40));
                } else {
                    btnDangXuat.setBackground(new Color(254, 226, 226));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (laGiaoDienToi()) {
                    btnDangXuat.setBackground(new Color(50, 50, 50));
                } else {
                    btnDangXuat.setBackground(new Color(254, 242, 242));
                }
            }
        }

        btnDangXuat.addMouseListener(new DangXuatHoverListener());
    }

    /**
     * Tải và trả về đối tượng Image của logo ứng dụng (assets/logo.png).
     * @return Image hoặc null nếu không tìm thấy tệp
     */
    public static Image getLogoUngDung() {
        try {
            File fileLogo = new File("assets/logo.png");
            if (fileLogo.exists()) {
                return ImageIO.read(fileLogo);
            }
        } catch (Exception e) {
            System.err.println("Không thể nạp logo ứng dụng: " + e.getMessage());
        }
        return null;
    }

    /**
     * Tải và trả về ImageIcon của logo với kích thước được co giãn (scale) mịn màng.
     * @param width Chiều rộng (pixel)
     * @param height Chiều cao (pixel)
     * @return ImageIcon hoặc null nếu không tải được ảnh
     */
    public static ImageIcon getLogoIcon(int width, int height) {
        Image img = getLogoUngDung();
        if (img != null) {
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }
}

