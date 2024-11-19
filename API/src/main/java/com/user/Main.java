package com.user;

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
        // Load FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign-in.fxml")));
        // Set stage properties
        stage.setTitle("Library");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/com/Image/logo.png"))));
        stage.setResizable(false);

        // Set scene with stylesheet
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Show stage
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
