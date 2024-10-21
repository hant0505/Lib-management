package com.example.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LoggedInController implements Initializable {

    @FXML
    private Button button_logOut;

    @FXML
    private Label label_welcome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_logOut.setOnAction(event ->
                DBUtils.changeScene(event, "sign-in.fxml", "Library", null));
    }

    public void setUserInfo(String username) {
        label_welcome.setText("Welcome " + username + "!");
    }
}
