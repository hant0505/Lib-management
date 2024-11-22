package com.example.lib;


import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrCode.Ecc;
import io.nayuki.qrcodegen.QrSegment;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
public class QR_Code {

    public static void main(String[] args) {
        // Thông tin cần mã hóa vào mã QR
        String bookInfo = "ISBN: 978-3-16-148410-0\nTên sách: Java Programming\nTình trạng: Đang mượn";

        // Tạo mã QR từ thông tin sách
        QrCode qr = QrCode.encodeText(bookInfo, Ecc.MEDIUM);

        // Chuyển mã QR thành hình ảnh và lưu lại
        BufferedImage img = toImage(qr, 4, 10);
        try {
            ImageIO.write(img, "PNG", new File("qr-code.png"));
            System.out.println("Mã QR đã được tạo và lưu thành công.");
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu mã QR: " + e.getMessage());
        }

        // Tạo mã QR từ chuỗi các segment
        List<QrSegment> segs = QrSegment.makeSegments("3141592653589793238462643383");
        QrCode qr1 = QrCode.encodeSegments(segs, QrCode.Ecc.HIGH, 5, 5, 2, false);

//        // Vẽ mã QR theo cách thủ công
//        for (int y = 0; y < qr1.size; y++) {
//            for (int x = 0; x < qr1.size; x++) {
//                if (qr1.getModule(x, y)) {
//                    // Vẽ module đen tại (x, y)
//                    // Đây là nơi bạn có thể vẽ mã QR lên đồ họa hoặc chuyển nó thành hình ảnh
//                }
//            }
//        }
    }

    // Phương thức chuyển mã QR thành BufferedImage
    private static BufferedImage toImage(QrCode qr, int scale, int border) {
        int size = qr.size;  // Sử dụng qr.size() thay vì qr.getSize()
        int imgSize = scale * size + 2 * border;
        BufferedImage img = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        // Vẽ nền trắng
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imgSize, imgSize);

        // Vẽ mã QR
        g.setColor(Color.BLACK);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (qr.getModule(x, y)) {
                    g.fillRect(border + scale * x, border + scale * y, scale, scale);
                }
            }
        }

        g.dispose();
        return img;
    }
}