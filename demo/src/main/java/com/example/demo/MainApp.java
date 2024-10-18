package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("game2048.fxml"));
        primaryStage.setTitle("2048 Game");
        primaryStage.setScene(new Scene(root, 400, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
