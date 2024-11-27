package com.example.lib;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("bookInfo.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Book Info");
        stage.setResizable(false);
        stage.show();

//        // Khởi động server HTTP trong một thread riêng
//        new Thread(() -> {
//            try {
//                QRHttpServer.main(new String[]{});  // Khởi chạy server ở cổng 8080
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();

        /// Khởi động server HTTP để quét mã QR
        try {
            // Thay vì startServer(), gọi main() của QRHttpServer
            new Thread(() -> {
                try {
                    QRHttpServer.main(new String[]{});  // Khởi chạy server ở cổng 8080
                    System.out.println("HTTP Server started.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();  // Chạy server trong một thread riêng biệt
        } catch (Exception e) {
            e.printStackTrace();
        }

        // In thông báo rằng ứng dụng đã bắt đầu
        System.out.println("JavaFX Application started.");
    }


    public static void main(String[] args) {
        launch();
    }
}
