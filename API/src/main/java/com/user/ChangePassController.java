package com.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        MenuHovering(button_home, button_issuedBooks, button_books, button_transactions, button_student, button_accInfo, button_signOut, button_delAccount, button_save);
        button_home.setOnAction(event -> DBUtils.changeScene(event, "main-home.fxml", "Library", AccountController.getOriginName(), AccountController.getOriginEmail(), AccountController.getOriginPhone()));
        button_books.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo.fxml", "Book Info", null, null, null));

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

    public static void MenuHovering(Button buttonHome, Button buttonIssuedBooks, Button buttonBooks, Button buttonTransactions, Button buttonStudent, Button buttonAccInfo, Button buttonSignOut, Button buttonDelAccount, Button buttonSave) {
        List<Button> buttons = new ArrayList<>();
        Collections.addAll(buttons, buttonHome, buttonIssuedBooks, buttonBooks, buttonTransactions, buttonStudent);
        DBUtils.hoverButtonFlowPane(buttons);
        DBUtils.hoverButton(buttonAccInfo, "-fx-background-color: #fcc194; -fx-border-color: #fda35a; -fx-border-width: 0px 0px 0px 8px; -fx-font-weight: bold;");
        DBUtils.hoverButton(buttonSignOut, "-fx-background-color: #fcc194; -fx-border-color: #fda35a; -fx-border-width: 0px 0px 0px 8px; -fx-font-weight: bold;");
        DBUtils.hoverButton(buttonDelAccount, "-fx-background-color: #fcc194; -fx-border-color: #fda35a; -fx-border-width: 0px 0px 0px 8px; -fx-font-weight: bold;");
        DBUtils.hoverButton(buttonSave, "-fx-background-color: #eaf36b; -fx-border-color: #eaf36b; -fx-border-width: 3px;");
    }
}
