package com.example.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePassController implements Initializable {
    @FXML
    private Button button_home;
    @FXML
    private Button button_student;
    @FXML
    private Button button_issuedBooks;
    @FXML
    private Button button_transactions;
    @FXML
    private Button button_books;
    @FXML
    private Button button_accInfo;
    @FXML
    private Button button_signOut;
    @FXML
    private Button button_delAccount;
    @FXML
    private Button button_save;

    @FXML
    private PasswordField pf_curPass;
    @FXML
    private PasswordField pf_newPass;
    @FXML
    private PasswordField pf_reNewPass;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_home.setOnAction(event -> DBUtils.changeScene(event, "main-home.fxml", "Library", null, null, null));
        button_accInfo.setOnAction(event -> DBUtils.changeScene(event, "account-info.fxml", "Library", AccountController.getOriginName(), AccountController.getOriginEmail(), AccountController.getOriginPhone()));
        button_delAccount.setOnAction(event -> DBUtils.deleteUser(event, AccountController.getOriginName()));
        AccountController.signOutAcc(button_signOut);
        button_save.setOnAction(event -> {
            if (pf_curPass.getText().isEmpty() || pf_reNewPass.getText().isEmpty() || pf_newPass.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No changes made.");
                alert.setContentText("Please fill all the fields!");
                alert.show();
            } else if (!pf_newPass.getText().equals(pf_reNewPass.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("New passwords do not match.");
                alert.setContentText("Please re enter new passwords!");
                alert.show();
            }
            else {
                DBUtils.changePassword(event, AccountController.getOriginName(), pf_curPass.getText(), pf_newPass.getText());
            }
        });

    }
}
