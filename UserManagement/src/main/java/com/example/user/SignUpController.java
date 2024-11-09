package com.example.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
        DBUtils.hoverButton(button_signUp, "-fx-background-color: #eaf36b; -fx-border-color: #eaf36b; -fx-border-width: 3px;");
        label_error.setVisible(false);
        pf_rePassword.textProperty().addListener((observable, oldValue, newValue) -> validPassword());

        button_signUp.setOnAction(event -> {
            if (!tf_username.getText().trim().isEmpty() && !pf_password.getText().isEmpty()
                    && !pf_rePassword.getText().isEmpty()) {
                DBUtils.signUpUser(event, tf_username.getText(), pf_password.getText(), tf_email.getText(), tf_phone.getText());
            } else {
                System.out.println("Please fill in all information!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill in all information to sign up.");
                alert.show();
            }
        });

        button_logIn.setOnAction(event -> DBUtils.changeScene(event, "sign-in.fxml", "Library", null, null, null));
    }
}
