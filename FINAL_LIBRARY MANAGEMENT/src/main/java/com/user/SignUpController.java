package com.user;

import com.effect.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_phone;
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_name;

    @FXML
    private Label label_error;


    @FXML
    private PasswordField pf_password;
    @FXML
    private PasswordField pf_rePassword;

    @FXML
    private Button button_signUp;
    @FXML
    private Button button_logIn;

    private void validPassword() {
        String password = pf_password.getText();
        String rePassword = pf_rePassword.getText();
        label_error.setVisible(!rePassword.equals(password));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label_error.setVisible(false);
        label_error.setVisible(false);
        pf_rePassword.textProperty().addListener((_, _, _) -> validPassword());

        button_signUp.setOnAction(event -> {
            if (!tf_username.getText().trim().isEmpty() && !pf_password.getText().isEmpty()
                    && !pf_rePassword.getText().isEmpty() && !tf_name.getText().isEmpty()
                    && !tf_email.getText().isEmpty() && !tf_phone.getText().isEmpty()) {
                DBUtils.signUpUser(event, tf_username.getText(), tf_name.getText(), pf_password.getText(), tf_email.getText(), tf_phone.getText());
            } else AlertUtils.warningAlert("Please fill in all information to sign up.");
        });

        button_logIn.setOnAction(event -> DBUtils.changeScene(event, "sign_in.fxml", "Library"));
    }
}
