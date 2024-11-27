package com.book;

import com.DatabaseConnection;
import io.nayuki.qrcodegen.QrCode;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static com.book.TransactionHandler.toImage;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class QR_SCAN {
    /**
     * Phương thức scan để quét và giải mã dữ liệu QR.
     *
     * @param qrCodeData qr
     */
    public static void scan(String qrCodeData) {
        String decodedData = URLDecoder.decode(qrCodeData, StandardCharsets.UTF_8);
        int transactionID;
        StringBuilder formattedData = new StringBuilder();

        String[] qrDetails = decodedData.split(";");
        if (qrDetails.length >= 6) { // Kiểm tra đủ dữ liệu
            transactionID = Integer.parseInt(qrDetails[0]);
            String username = qrDetails[1];
            String bookTitle = qrDetails[2];
            LocalDate borrowedDate = LocalDate.parse(qrDetails[3]);
            LocalDate dueDate = LocalDate.parse(qrDetails[4]);
            String status = qrDetails[5];

            // late Fee
            long lateFee = 0;
            if (LocalDate.now().isAfter(dueDate)) {
                long daysPast = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
                lateFee = daysPast * 10000;
            }

            // Xây dựng chuỗi hiển thị, mỗi mục cách nhau một dòng
            formattedData.append("Transaction ID: ").append(transactionID).append("\n\n")
                    .append("Username: ").append(username).append("\n\n")
                    .append("Book Title: ").append(bookTitle).append("\n\n")
                    .append("Borrowed Date: ").append(borrowedDate).append("\n\n")
                    .append("Due Date: ").append(dueDate).append("\n\n")
                    .append("Status: ").append(status).append("\n\n")
                    .append("Late Fee: ").append(lateFee).append("\n\n")
                    .append("Click OK to confirm returning book.");

            showQRCodePopup(formattedData.toString(), transactionID);
        } else {
            formattedData.append("Dữ liệu QR không hợp lệ.");
            System.out.println(formattedData);
        }
    }

    // Phương thức này sẽ được gọi khi có dữ liệu QR được nhận từ server
    public static void showQRCodePopup(String data, int transactionID) {
        Platform.runLater(() -> {
            // Tạo thông báo Pop-up để hiển thị dữ liệu QR
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("QR Code Data");
            alert.setHeaderText("Transaction Information");
            alert.setContentText(data);  // Hiển thị dữ liệu QR nhận được

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("Update transaction status: Returned.");
                    DBUtils.updateTransactionStatus(transactionID);
                }
            });
        });
    }

    public static void createQR(int transactionID, String username, String bookTitle, LocalDate borrowedDate, LocalDate dueDate, String status) {
        try {
            bookTitle = URLEncoder.encode(bookTitle, StandardCharsets.UTF_8);
            String serverIP = ServerIP.getServerIP();
            String qrData = "http://" + serverIP + ":8080/qrs?data=" + transactionID + ";" + username + ";" + bookTitle + ";" + borrowedDate + ";" + dueDate + ";" + status;

            QrCode qr = QrCode.encodeText(qrData, QrCode.Ecc.MEDIUM);
            BufferedImage img = toImage(qr, 4, 10);

            String qrPath = "qrs/trans_" + transactionID + ".png";
            File qrFile = new File(qrPath);
            qrFile.getParentFile().mkdirs(); // Tạo thư mục nếu chưa tồn tại
            ImageIO.write(img, "PNG", qrFile);

            // Cập nhật đường dẫn QR code trong cơ sở dữ liệu
            String updateSQL = "UPDATE transactions SET qrCodePath = ? WHERE transactionID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setString(1, qrPath);
                updateStmt.setInt(2, transactionID);
                updateStmt.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Failed to generate updated QR code: " + e.getMessage());
        }
    }

    public static void updateUsernameInQRCode(int transactionID, String newUsername, String bookTitle, LocalDate borrowedDate, LocalDate dueDate, String status) {
        String qrPath = "qrs/trans_" + transactionID + ".png";

        // Xóa file QR cũ
        try {
            Path path = Paths.get(qrPath);
            Files.deleteIfExists(path);
        } catch (Exception e) {
            System.err.println("Failed to delete QR code file: " + e.getMessage());
        }
        // Tạo mã QR mới
        createQR(transactionID, newUsername, bookTitle, borrowedDate, dueDate, status);
    }

    public static void updateDueDateInQRCode(int transactionID, String username, String bookTitle, LocalDate borrowedDate, LocalDate newDueDate, String status) {
        String qrPath;
        qrPath = "qrs/trans_" + transactionID + ".png";

        // Xóa file QR cũ
        try {
            Path path = Paths.get(qrPath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Failed to delete QR code file: " + e.getMessage());
        }
        // Tạo mã QR mới
        createQR(transactionID, username, bookTitle, borrowedDate, newDueDate, status);
    }
}
