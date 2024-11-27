package com.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    @FXML
    private Button button_logIn;

    @FXML
    private Button button_signUp;

    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField pf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_signUp.setOnAction(event -> DBUtils.changeScene(event, "sign-up.fxml", "Library Sign Up"));
        button_logIn.setOnAction(event -> DBUtils.logInUser(event, tf_username.getText(), pf_password.getText()));
    }
}