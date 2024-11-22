package com.example.lib;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;

public class DisplayQRCode extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Đường dẫn đến tệp ảnh mã QR đã lưu
        File qrCodeImageFile = new File("qr-code.png");

        // Kiểm tra xem tệp có tồn tại không
        if (qrCodeImageFile.exists()) {
            // Tạo đối tượng Image từ tệp ảnh mã QR
            Image qrImage = new Image(qrCodeImageFile.toURI().toString());

            // Tạo ImageView để hiển thị ảnh
            ImageView imageView = new ImageView(qrImage);
            imageView.setFitWidth(300); // Kích thước ảnh theo chiều rộng
            imageView.setFitHeight(300); // Kích thước ảnh theo chiều cao
            imageView.setPreserveRatio(true); // Giữ tỷ lệ của ảnh

            // Tạo một StackPane để chứa ImageView
            StackPane root = new StackPane();
            root.getChildren().add(imageView);

            // Tạo cảnh và gán vào stage
            Scene scene = new Scene(root, 400, 400);
            primaryStage.setTitle("Mã QR");
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            System.err.println("Tệp mã QR không tồn tại.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
