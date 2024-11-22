package com.example.lib;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public class QRCodeReader {
    public static String decodeQRCode(File qrFile) {
        try {
            BufferedImage bufferedImage = ImageIO.read(qrFile);
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText(); // Trả về nội dung mã QR
        } catch (Exception e) {
            System.err.println("Không thể đọc mã QR: " + e.getMessage());
            return null;
        }
    }
}
