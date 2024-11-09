package com.example.user;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.sql.*;
import java.util.List;
import java.util.Objects;

import static com.example.user.DBTables.DBConnection;

@SuppressWarnings({"CallToPrintStackTrace", "OptionalGetWithoutIsPresent"})
public class DBUtils { // helper class contains static method aka actions

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String email, String phone) {
        Parent root = null;
        if (username != null && phone != null && email != null) {
            if (fxmlFile.equals("main-home.fxml")) {
                try {
                    FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                    root = loader.load();

                    HomeController homeController = loader.getController();
                    homeController.setUserInfo(username, email, phone);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (fxmlFile.equals("account-info.fxml")) {
                try {
                    FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                    root = loader.load();

                    AccountController accountController = loader.getController();
                    accountController.showInfoEdit(username, email, phone);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(Objects.requireNonNull(DBUtils.class.getResource(fxmlFile))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(Objects.requireNonNull(root), 1300, 700)); stage.show();
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

    public static void signUpUser(ActionEvent event, String username, String password, String email, String phone) {
        Connection conn = null;
        PreparedStatement psInsert;
        PreparedStatement psCheckExists = null;
        ResultSet resultSet = null;
        boolean unique = true;
        StringBuilder errorMessage = new StringBuilder();

        try {
            conn = DBConnection();
            psCheckExists = conn.prepareStatement("select * from users where username = ?");
            psCheckExists.setString(1, username);
            resultSet = psCheckExists.executeQuery();
            if(resultSet.isBeforeFirst()) {
                unique = false;// username existed
                errorMessage.append("User already existed. Change another username!\n");
            }

            conn = DBConnection();
            psCheckExists = conn.prepareStatement("select * from users where email = ?");
            psCheckExists.setString(1, email);
            resultSet = psCheckExists.executeQuery();
            if(resultSet.isBeforeFirst()) {
                unique = false; // email existed
                errorMessage.append("Email already existed. Change another email!\n");
            }

            psCheckExists = conn.prepareStatement("select * from users where phone = ?");
            psCheckExists.setString(1, phone);
            resultSet = psCheckExists.executeQuery();
            if(resultSet.isBeforeFirst()) {
                unique = false; // phone existed
                errorMessage.append("Phone already existed. Change another phone!\n");
            }

            if (!unique) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(errorMessage.toString());
                alert.show();
            } else {
                psInsert = conn.prepareStatement("insert into users(username, password, email, phone) values(?,?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, email);
                psInsert.setString(4, phone);

                psInsert.executeUpdate();

                loggedInUser = username;
                loggedInEmail = email;
                loggedInPhone = phone;

                changeScene(event, "main-home.fxml", "Library Welcome", username, email, phone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, psCheckExists, resultSet);
        }
    }

    public static String loggedInUser;
    public static String loggedInEmail;
    public static String loggedInPhone;
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
            conn = DBConnection();
            ps = conn.prepareStatement("select password, email, phone from users where username = ?");
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
                        String email = rs.getString("email");
                        String phone = rs.getString("phone");
                        loggedInUser = username;
                        loggedInEmail = email;
                        loggedInPhone = phone;

                        changeScene(event, "main-home.fxml", "Library Login", username, email, phone);
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

    public static void deleteUser(ActionEvent event, String username) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("This will delete this user and log you out.");

        if (ButtonType.OK == alert.showAndWait().get()) {
            try {
                Connection conn = DBTables.DBConnection();
                PreparedStatement ps = conn.prepareStatement("delete from users where username = ?");
                ps.setString(1, username);
                ps.executeUpdate();

                changeScene(event, "sign-in.fxml", "Library", null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateUserInfo(ActionEvent event, String currentUsername, String newUsername, String email, String phone) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBTables.DBConnection();
            ps = conn.prepareStatement("select id from users where username = ?");
            ps.setString(1, currentUsername);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("user not found");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("User not found!");
                alert.show();
            } else {
                rs.next();
                int id = rs.getInt("id");
                ps = conn.prepareStatement("update users set username = ?, email = ?, phone = ? where id = ?");
                ps.setString(1, newUsername);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, id);
                ps.executeUpdate();

                loggedInUser = newUsername;
                loggedInEmail = email;
                loggedInPhone = phone;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Update User");
                alert.setHeaderText("Update User Information");
                alert.setContentText("User successfully updated!");
                alert.show();
                changeScene(event, "account-info.fxml", "Library", newUsername, email, phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, ps, rs);
        }
    }

    public static void changePassword(ActionEvent event, String username, String curPass, String newPass) {
        Connection conn = null;
        PreparedStatement psCheckNewPass = null;
        PreparedStatement psChange;
        ResultSet rs = null;

        try {
            conn = DBTables.DBConnection();
            psCheckNewPass = conn.prepareStatement("select password from users where username = ?");
            psCheckNewPass.setString(1, username);
            rs = psCheckNewPass.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("user not found");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("User not found!");
                alert.show();
            } else {
                rs.next();
                String password = rs.getString("password");
                System.out.println(password);
                if (!curPass.equals(password)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Password Mismatch");
                    alert.setHeaderText("Password Mismatch");
                    alert.setContentText("Passwords do not match!");
                    alert.show();
                } else {
                    psChange = conn.prepareStatement("update users set password = ? where username = ?");
                    psChange.setString(1, newPass);
                    psChange.setString(2, username);
                    psChange.executeUpdate();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Password");
                    alert.setHeaderText("Update Password");
                    alert.setContentText("Password successfully updated!");
                    alert.show();

                    changeScene(event, "account-changePass.fxml", "Library", null, null, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, psCheckNewPass, rs);
        }
    }

    public static void hoverButton(Button button, String style) {
        button.styleProperty().bind(Bindings.when(button.hoverProperty())
                .then(style)
                .otherwise(button.getStyle()));
    }

    public static void hoverButtonFlowPane(List<Button> buttons) {
        for (Button button : buttons) {
            button.styleProperty().bind(Bindings.when(button.hoverProperty())
                    .then("-fx-background-color: #dfc19f; " +
                            "-fx-border-color: #fda35a; " +
                            "-fx-border-width: 0px 0px 5px 0px; " +
                            "-fx-font-weight: bold;")
                    .otherwise(button.getStyle()));
        }
    }
}
