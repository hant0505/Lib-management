package com.example.user;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
public class DBUtils { // helper class contains static method aka actions

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root = null;
        if (username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInfo(username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(Objects.requireNonNull(DBUtils.class.getResource(fxmlFile))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(Objects.requireNonNull(root), 600, 400));
        stage.show();
    }

    private static void closeDatabase(Connection conn, PreparedStatement psCheckExists, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psCheckExists != null) {
            try {
                psCheckExists.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void signUpUser(ActionEvent event, String username, String password, String rePassword, String email, int phone) {
        Connection conn = null;
        PreparedStatement psInsert;
        PreparedStatement psCheckExists = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://10.10.120.50:3306/library", "luu", "Luuminhanh_123"); // ĐỢI SQL CỦA LỆ
            psCheckExists = conn.prepareStatement("select * from users where user_name = ?");
            psCheckExists.setString(1, username);
            resultSet = psCheckExists.executeQuery();

            if(resultSet.isBeforeFirst()) {// user existed
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("User already existed. Change another username!");
                alert.show();
            } else if (!rePassword.equals(password)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Passwords do not match. Please try again!");
                alert.show();
            } else {
                psInsert = conn.prepareStatement("insert into users(user_name, password, email, phone) values(?,?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, email);
                psInsert.setInt(4, phone);

                psInsert.executeUpdate();

                changeScene(event, "main-home.fxml", "Library Welcome", username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, psCheckExists, resultSet);
        }
    }

    public static void logInUser(ActionEvent event, String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (username.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Please enter your username and password!");
                alert.show();
            }
            conn = DriverManager.getConnection("jdbc:mysql://10.10.120.50:3306/library", "luu", "Luuminhanh_123");
            ps = conn.prepareStatement("select password from users where user_name = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) { // user  not found
                System.out.println("user not found");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            } else {
                while (rs.next()) {
                    String retrievedPassword = rs.getString("password");
                    if (retrievedPassword.equals(password)) {
                        changeScene(event, "main-homemain-home.fxml", "Library Login", username);
                    } else {// Unmatched password
                        System.out.println("Unmatched password");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Provided credentials are incorrect!");
                        alert.show();
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, ps, rs);
        }
    }
}
