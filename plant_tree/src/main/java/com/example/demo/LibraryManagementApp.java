package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryManagementApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Quản Lý Thư Viện");

        VBox layout = new VBox(10);
        Button play2048Button = new Button("Chơi Game 2048");

        play2048Button.setOnAction(e -> openGame2048Window());

        layout.getChildren().add(play2048Button);
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openGame2048Window() {
        Stage gameStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GAME2048.fxml")); // Đảm bảo đường dẫn đúng
            Scene scene = new Scene(loader.load(), 400, 450);
            gameStage.setTitle("Game 2048");
            gameStage.setScene(scene);
            gameStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
