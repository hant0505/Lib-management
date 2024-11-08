package com.example.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class AccountController implements Initializable {
    @FXML
    private Button button_student;
    @FXML
    private Button button_signOut;
    @FXML
    private Button button_changePassword;
    @FXML
    private Button button_home;
    @FXML
    private Button button_issuedBooks;
    @FXML
    private Button button_transactions;
    @FXML
    private Button button_books;
    @FXML
    private Button button_edit;
    @FXML
    private Button button_delAccount;
    @FXML
    private Button button_save;
    @FXML
    private Button button_back;

    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_phone;

    @FXML
    private Label label_username;
    public Label label_email;
    public Label label_phone;

    private static String originName;
    private static String originEmail;
    private static String originPhone;

    public static String getOriginName() {
        return originName;
    }

    public static void setOriginName(String originName) {
        AccountController.originName = originName;
    }

    public static String getOriginEmail() {
        return originEmail;
    }

    public static void setOriginEmail(String originEmail) {
        AccountController.originEmail = originEmail;
    }

    public static String getOriginPhone() {
        return originPhone;
    }

    public static void setOriginPhone(String originPhone) {
        AccountController.originPhone = originPhone;
    }

    public void showInfoEdit(String username, String email, String phone) {
        // Save tên gốc
        setOriginName(username);
        setOriginEmail(email);
        setOriginPhone(phone);

        label_username.setText(username);
        label_email.setText(email);
        label_phone.setText(phone);

        tf_username.setText(username);
        tf_email.setText(email);
        tf_phone.setText(phone);

        disableEditing();
    }

    private void enableEditing() {
        label_username.setVisible(false);
        label_email.setVisible(false);
        label_phone.setVisible(false);

        tf_username.setVisible(true);
        tf_email.setVisible(true);
        tf_phone.setVisible(true);

        button_save.setVisible(true);
        button_back.setVisible(true);
    }
    private void disableEditing() {
        label_username.setVisible(true);
        label_email.setVisible(true);
        label_phone.setVisible(true);

        tf_username.setVisible(false);
        tf_email.setVisible(false);
        tf_phone.setVisible(false);
        button_save.setVisible(false);
        button_back.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Button> buttons = new ArrayList<>();
        Collections.addAll(buttons, button_home, button_issuedBooks, button_books, button_transactions, button_student);
        HomeController.hoverButtonFlowPane(buttons);
        signOutAcc(button_signOut);
        button_home.setOnAction(event -> DBUtils.changeScene(event, "main-home.fxml", "Library", label_username.getText(), label_email.getText(), label_phone.getText()));
        button_delAccount.setOnAction(event -> DBUtils.deleteUser(event, label_username.getText()));
        button_edit.setOnAction(_ -> enableEditing());
        button_save.setOnAction(event -> DBUtils.updateUserInfo(event, label_username.getText(), tf_username.getText(), tf_email.getText(), tf_phone.getText()));
        button_back.setOnAction(_ -> {
            tf_username.setText(getOriginName());
            tf_email.setText(getOriginEmail());
            tf_phone.setText(getOriginPhone());
            disableEditing();
        });
        button_changePassword.setOnAction(event -> DBUtils.changeScene(event, "account-changePass.fxml", "Library", null, null ,null));
    }

    static void signOutAcc(Button buttonSignOut) {
        buttonSignOut.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Sign Out");
            alert.setHeaderText("Are you sure you want to sign out?");
            alert.setContentText("This will log you out of your current session.");
            if (alert.showAndWait().get() == ButtonType.OK) {
                DBUtils.changeScene(event, "sign-in.fxml", "Library", null, null, null);
            }
        });
    }

    public void showInfo(String username, String email, String phone) {
        label_username.setText(username);
        label_email.setText(email);
        label_phone.setText(phone);
    }
}
