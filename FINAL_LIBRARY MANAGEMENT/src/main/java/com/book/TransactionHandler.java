package com.book;

import io.nayuki.qrcodegen.QrCode;

import java.awt.*;
import java.awt.image.BufferedImage;


public class TransactionHandler {
    // Chuyển mã QR thành BufferedImage
    public static BufferedImage toImage(QrCode qr, int scale, int border) {
        int size = qr.size;
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
