package com.example.user;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign-in.fxml")));
        stage.setTitle("Library");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        stage.setFullScreen(false);
        stage.setScene(new Scene(root));
        stage.setWidth(1300);
        stage.setHeight(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

