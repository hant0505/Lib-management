package com.example.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class SignUpController implements Initializable {
    @FXML
    public TextField tf_email;

    @FXML
    public TextField tf_phone;
    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField pf_password;

    @FXML
    private PasswordField pf_rePassword;

    @FXML
    private Button button_signUp;

    @FXML
    private Button button_logIn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_signUp.setOnAction(event -> {
            if (!tf_username.getText().trim().isEmpty() && !pf_password.getText().isEmpty()
                && !pf_rePassword.getText().isEmpty()) {
                if (!pf_rePassword.getText().equals(pf_password.getText())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Passwords do not match!");
                    alert.show();
                } else {
                    DBUtils.signUpUser(event, tf_username.getText(), pf_password.getText(), pf_rePassword.getText(), tf_email.getText(), parseInt(tf_phone.getText()));
                }
            } else {
                System.out.println("Please fill in all information!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill in all information to sign up.");
                alert.show();
            }
        });

        button_logIn.setOnAction(event -> DBUtils.changeScene(event, "sign-in.fxml", "Library", null));
    }
}
