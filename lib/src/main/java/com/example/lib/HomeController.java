package com.example.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("CallToPrintStackTrace")
public class HomeController implements Initializable {
    @FXML
    private Button button_home;
    @FXML
    private Button button_account;
    @FXML
    private Button button_students;
    @FXML
    private Button button_issuedBook;
    @FXML
    private Button button_transactions;
    @FXML
    private Button button_books;

    @FXML
    public Label label_welcome;

    private String username;
    private String email;
    private String phone;

    public void setUserInfo(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    private void showAccountInfo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account-info.fxml"));
            Parent root = loader.load();

            AccountController accountController = loader.getController();
            accountController.showInfo(username, email, phone);
            accountController.showInfoEdit(username, email, phone);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_account.setOnAction(this::showAccountInfo);
        label_welcome.setText("Welcome " + username);
    }
}
