package util;

import model.BaoCaoTaiChinh;
import model.GiaoDich;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Lớp tiện ích xuất dữ liệu báo cáo tài chính (Report Exporter).
 * Hỗ trợ xuất ra định dạng Excel CSV (có UTF-8 BOM chuẩn tiếng Việt) và Báo cáo HTML in PDF.
 */
public class XuatBaoCaoHelper {

    /**
     * Xuất báo cáo ra tệp CSV (tương thích mở trực tiếp bằng Microsoft Excel không bị lỗi font tiếng Việt).
     * @param baoCao đối tượng BaoCaoTaiChinh chứa dữ liệu
     * @param fileDich tệp đích cần ghi
     * @return true nếu ghi thành công
     */
    public static boolean xuatFileCSV(BaoCaoTaiChinh baoCao, File fileDich) {
        try (FileOutputStream fos = new FileOutputStream(fileDich);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            // Ghi ký tự Byte Order Mark (BOM) UTF-8 để Microsoft Excel nhận diện đúng tiếng Việt Unicode
            writer.write("\uFEFF");

            // 1. PHẦN TIÊU ĐỀ BÁO CÁO
            writer.write("========================================================================\n");
            writer.write("\"" + baoCao.getTieuDe().toUpperCase() + "\"\n");
            writer.write("\"Người tạo: " + baoCao.getNguoiDung() + "\"\n");
            writer.write("\"Ngày xuất báo cáo: " + baoCao.getNgayXuatBaoCao() + "\"\n");
            writer.write("========================================================================\n\n");

            // 2. PHẦN TỔNG KẾT TÀI CHÍNH
            writer.write("\"--- TỔNG KẾT TÀI CHÍNH KỲ NÀY ---\"\n");
            writer.write("\"Chỉ số\",\"Giá trị\"\n");
            writer.write("\"Tổng Thu Nhập\",\"" + baoCao.getTongThuDinhDang() + "\"\n");
            writer.write("\"Tổng Chi Tiêu\",\"" + baoCao.getTongChiDinhDang() + "\"\n");
            writer.write("\"Tiết Kiệm Ròng / Thặng Dư\",\"" + baoCao.getSoDuThangDuDinhDang() + "\"\n");
            writer.write("\"Tỷ Lệ Tiết Kiệm\",\"" + String.format("%.2f", baoCao.getTyLeTietKiem()) + " %\"\n");
            writer.write("\"Tổng Số Lượng Giao Dịch\",\"" + baoCao.getSoLuongGiaoDich() + " giao dịch\"\n\n");

            // 3. PHẦN CƠ CẤU CHI TIÊU THEO DANH MỤC
            writer.write("\"--- CƠ CẤU CHI TIÊU THEO DANH MỤC ---\"\n");
            writer.write("\"Danh Mục\",\"Số Tiền\",\"Tỷ Trọng (%)\"\n");
            double tongChi = baoCao.getTongChi();
            if (baoCao.getChiTieuTheoDanhMuc().isEmpty()) {
                writer.write("\"(Không có khoản chi nào trong kỳ)\",\"0 VNĐ\",\"0%\"\n");
            } else {
                for (Map.Entry<String, Double> entry : baoCao.getChiTieuTheoDanhMuc().entrySet()) {
                    double phanTram = (tongChi > 0) ? (entry.getValue() / tongChi * 100) : 0;
                    writer.write("\"" + entry.getKey() + "\",\"" +
                            DinhDangTienHelper.dinhDangVND(entry.getValue()) + "\",\"" +
                            String.format("%.1f", phanTram) + "%\"\n");
                }
            }
            writer.write("\n");

            // 4. PHẦN CHI TIẾT DANH SÁCH GIAO DỊCH
            writer.write("\"--- CHI TIẾT CÁC GIAO DỊCH PHÁT SINH ---\"\n");
            writer.write("\"STT\",\"Thời Gian\",\"Phân Loại\",\"Danh Mục\",\"Ví Nguồn\",\"Số Tiền (VNĐ)\",\"Mô Tả / Ghi Chú\"\n");

            int stt = 1;
            for (GiaoDich gd : baoCao.getDanhSachGiaoDich()) {
                String soTien = ("income".equalsIgnoreCase(gd.getType()) ? "+" : "-") + DinhDangTienHelper.dinhDangVND(gd.getAmount());
                String moTa = gd.getDescription() != null ? gd.getDescription().replace("\"", "\"\"") : "";
                writer.write(stt++ + ",\"" +
                        gd.getDateTime() + "\",\"" +
                        gd.getLoaiHienThi() + "\",\"" +
                        gd.getCategoryName() + "\",\"" +
                        gd.getTenVi() + "\",\"" +
                        soTien + "\",\"" +
                        moTa + "\"\n");
            }

            writer.flush();
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xuất file CSV: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xuất báo cáo ra định dạng HTML hiện đại (có thể mở xem trực tiếp trên trình duyệt và bấm In ra PDF).
     */
    public static boolean xuatFileHTML(BaoCaoTaiChinh baoCao, File fileDich) {
        try (FileOutputStream fos = new FileOutputStream(fileDich);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html lang=\"vi\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("<title>").append(baoCao.getTieuDe()).append("</title>\n")
                .append("<style>\n")
                .append("  * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }\n")
                .append("  body { background-color: #f4f6f9; color: #333; padding: 30px 20px; }\n")
                .append("  .container { max-width: 900px; margin: 0 auto; background: #fff; border-radius: 12px; padding: 35px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }\n")
                .append("  .header { text-align: center; border-bottom: 2px solid #e2e8f0; padding-bottom: 20px; margin-bottom: 25px; }\n")
                .append("  .header h1 { color: #0d9488; font-size: 26px; text-transform: uppercase; margin-bottom: 8px; }\n")
                .append("  .header p { color: #64748b; font-size: 14px; }\n")
                .append("  .summary-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; margin-bottom: 30px; }\n")
                .append("  .card { padding: 18px; border-radius: 10px; text-align: center; color: white; }\n")
                .append("  .card h3 { font-size: 13px; text-transform: uppercase; letter-spacing: 0.5px; opacity: 0.9; margin-bottom: 8px; }\n")
                .append("  .card .amount { font-size: 22px; font-weight: bold; }\n")
                .append("  .card.income { background: linear-gradient(135deg, #10b981, #059669); }\n")
                .append("  .card.expense { background: linear-gradient(135deg, #ef4444, #dc2626); }\n")
                .append("  .card.balance { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }\n")
                .append("  h2 { font-size: 18px; color: #1e293b; margin: 25px 0 12px; display: flex; align-items: center; border-left: 4px solid #0d9488; padding-left: 10px; }\n")
                .append("  table { width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 14px; }\n")
                .append("  th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #e2e8f0; }\n")
                .append("  th { background-color: #f8fafc; color: #475569; font-weight: 600; }\n")
                .append("  tr:hover { background-color: #f1f5f9; }\n")
                .append("  .badge { display: inline-block; padding: 3px 8px; border-radius: 12px; font-size: 12px; font-weight: 600; }\n")
                .append("  .badge.income { background-color: #d1fae5; color: #065f46; }\n")
                .append("  .badge.expense { background-color: #fee2e2; color: #991b1b; }\n")
                .append("  .text-right { text-align: right; }\n")
                .append("  .text-center { text-align: center; }\n")
                .append("  .actions { text-align: center; margin-top: 30px; }\n")
                .append("  .btn-print { background: #0d9488; color: white; border: none; padding: 10px 24px; border-radius: 6px; font-size: 15px; font-weight: 600; cursor: pointer; transition: 0.2s; }\n")
                .append("  .btn-print:hover { background: #0f766e; }\n")
                .append("  @media print { .actions { display: none; } body { padding: 0; background: #fff; } .container { box-shadow: none; padding: 0; } }\n")
                .append("</style>\n</head>\n<body>\n")
                .append("<div class=\"container\">\n")
                .append("  <div class=\"header\">\n")
                .append("    <h1>").append(baoCao.getTieuDe()).append("</h1>\n")
                .append("    <p>Người tạo: <strong>").append(baoCao.getNguoiDung()).append("</strong> | Ngày tạo: <strong>").append(baoCao.getNgayXuatBaoCao()).append("</strong></p>\n")
                .append("  </div>\n")
                .append("  <div class=\"summary-cards\">\n")
                .append("    <div class=\"card income\"><h3>Tổng Thu Nhập</h3><div class=\"amount\">").append(baoCao.getTongThuDinhDang()).append("</div></div>\n")
                .append("    <div class=\"card expense\"><h3>Tổng Chi Tiêu</h3><div class=\"amount\">").append(baoCao.getTongChiDinhDang()).append("</div></div>\n")
                .append("    <div class=\"card balance\"><h3>Tiết Kiệm Ròng</h3><div class=\"amount\">").append(baoCao.getSoDuThangDuDinhDang()).append("</div></div>\n")
                .append("  </div>\n");

            // Bảng Cơ cấu danh mục
            html.append("  <h2>Cơ Cấu Chi Tiêu Theo Danh Mục</h2>\n")
                .append("  <table>\n")
                .append("    <thead><tr><th>Danh Mục</th><th class=\"text-right\">Số Tiền</th><th class=\"text-right\">Tỷ Trọng</th></tr></thead>\n")
                .append("    <tbody>\n");

            double tongChi = baoCao.getTongChi();
            if (baoCao.getChiTieuTheoDanhMuc().isEmpty()) {
                html.append("      <tr><td colspan=\"3\" class=\"text-center\" style=\"color:#94a3b8;\">Chưa có khoản chi nào trong kỳ này</td></tr>\n");
            } else {
                for (Map.Entry<String, Double> entry : baoCao.getChiTieuTheoDanhMuc().entrySet()) {
                    double phanTram = (tongChi > 0) ? (entry.getValue() / tongChi * 100) : 0;
                    html.append("      <tr><td>").append(entry.getKey()).append("</td>")
                        .append("<td class=\"text-right\">").append(DinhDangTienHelper.dinhDangVND(entry.getValue())).append("</td>")
                        .append("<td class=\"text-right\"><strong>").append(String.format("%.1f", phanTram)).append("%</strong></td></tr>\n");
                }
            }
            html.append("    </tbody>\n  </table>\n");

            // Bảng Chi tiết giao dịch
            html.append("  <h2>Chi Tiết Các Giao Dịch (").append(baoCao.getSoLuongGiaoDich()).append(" giao dịch)</h2>\n")
                .append("  <table>\n")
                .append("    <thead><tr><th>STT</th><th>Thời Gian</th><th>Loại</th><th>Danh Mục</th><th>Ví</th><th class=\"text-right\">Số Tiền</th><th>Mô Tả</th></tr></thead>\n")
                .append("    <tbody>\n");

            int stt = 1;
            for (GiaoDich gd : baoCao.getDanhSachGiaoDich()) {
                boolean laThu = "income".equalsIgnoreCase(gd.getType());
                String badgeClass = laThu ? "badge income" : "badge expense";
                String dauTien = laThu ? "+" : "-";
                String mauTien = laThu ? "color:#059669; font-weight:bold;" : "color:#dc2626; font-weight:bold;";

                html.append("      <tr>")
                    .append("<td class=\"text-center\">").append(stt++).append("</td>")
                    .append("<td>").append(gd.getDateTime()).append("</td>")
                    .append("<td><span class=\"").append(badgeClass).append("\">").append(gd.getLoaiHienThi()).append("</span></td>")
                    .append("<td>").append(gd.getCategoryName()).append("</td>")
                    .append("<td>").append(gd.getTenVi()).append("</td>")
                    .append("<td class=\"text-right\" style=\"").append(mauTien).append("\">").append(dauTien).append(DinhDangTienHelper.dinhDangVND(gd.getAmount())).append("</td>")
                    .append("<td>").append(gd.getDescription() != null ? gd.getDescription() : "").append("</td>")
                    .append("</tr>\n");
            }

            html.append("    </tbody>\n  </table>\n")
                .append("  <div class=\"actions\">\n")
                .append("    <button class=\"btn-print\" onclick=\"window.print()\">In Báo Cáo / Lưu PDF</button>\n")
                .append("  </div>\n")
                .append("</div>\n</body>\n</html>");

            writer.write(html.toString());
            writer.flush();
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xuất file HTML: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

