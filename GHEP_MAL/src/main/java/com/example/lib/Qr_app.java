package com.example.lib;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class Qr_app {
    /// Phương thức scan để quét và giải mã dữ liệu QR
    public static void scan (String qrCodeData) {
        System.out.println(qrCodeData + "  đc kết nối rồi");///DEBUG
        // Giải mã dữ liệu từ chuỗi QR (giả sử dữ liệu là chuỗi mã hóa UTF-8)
        //String decodedData = URLDecoder.decode(qrCodeData.split("\\?data=")[1], StandardCharsets.UTF_8);
        int transactionID;
        StringBuilder formattedData = new StringBuilder();
        String[] qrDetails = qrCodeData.split(";");

        // Lấy các thông tin từ dữ liệu đã giải mã
        if (qrDetails.length >= 6) { // Kiểm tra đủ dữ liệu
            transactionID = Integer.parseInt(qrDetails[0]);
            String username = qrDetails[1];
            String title = qrDetails[2];
            String bookTitle = title.replace("+", " ");
            LocalDate borrowedDate = LocalDate.parse(qrDetails[3]);
            LocalDate dueDate = LocalDate.parse(qrDetails[4]);
            String status = qrDetails[5];

            formattedData.append("Transaction ID: ").append(transactionID).append("\n\n")
                    .append("Username: ").append(username).append("\n\n")
                    .append("Book Title: ").append(bookTitle).append("\n\n")
                    .append("Borrowed Date: ").append(borrowedDate).append("\n\n")
                    .append("Due Date: ").append(dueDate).append("\n\n")
                    .append("Status: ").append(status);

            showQRCodePopup(formattedData.toString(), transactionID);
        } else {
            formattedData.append("Dữ liệu QR không hợp lệ.");
            System.out.println(formattedData);
        }
    }

    public static void showQRCodePopup(String data,int transactionID) {
        // Gọi Platform.runLater() để chắc chắn rằng giao diện JavaFX được cập nhật trên thread JavaFX
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("QR Code Data");
            alert.setHeaderText("Dữ liệu mã QR");
            alert.setContentText(data);

            /// nhấn OK
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("Cập nhật trạng thái giao dịch.");
                    DBUtils.updateTransactionStatus(transactionID);
                    }
            });
        });
    }
}
