package com.user;

import com.DatabaseConnection;
import com.attendance.AttendanceController;
import com.book.QRHttpServer;
import com.effect.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "CallToPrintStackTrace"})
public class AccountController implements Initializable {
    public static LocalDateTime checkInTime;
    public static LocalDateTime checkOutTime;
    private static String originUsername;
    private static String originName;
    private static String originEmail;
    private static String originPhone;
    private static AccountController instance;
    @FXML
    private Button button_students;
    @FXML
    private Button button_signOut;
    @FXML
    private Button button_changePassword;
    @FXML
    private Button button_home;
    @FXML
    private Button button_transactionsForLib;
    @FXML
    private Button button_transactionsForStu;
    @FXML
    private Button button_booksForLib;
    @FXML
    private Button button_booksForStu;
    @FXML
    private Button button_edit;
    @FXML
    private Button button_delAccount;
    @FXML
    private Button button_save;
    @FXML
    private Button button_back;
    @FXML
    private Button button_game;
    @FXML
    private Button button_checkIn;
    @FXML
    private Button button_attendance;
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_phone;
    @FXML
    private TextField tf_name;
    @FXML
    private Label label_username;
    @FXML
    private Label label_email;
    @FXML
    private Label label_phone;
    @FXML
    private Label label_name;

    public AccountController() {
        instance = this; // Lưu tham chiếu đến đối tượng hiện tại
    }

    public static AccountController getInstance() {
        return instance;
    }

    public static String getOriginUsername() {
        return originUsername;
    }

    public static void setOriginUsername(String originUsername) {
        AccountController.originUsername = originUsername;
    }

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

    public static void signOutAcc(Button buttonSignOut) {
        buttonSignOut.setOnAction(event -> {
            Alert alert = AlertUtils.confirmAlert("Are you sure want to sign out?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (DBUtils.loggedInRole.equals("LIBRARIAN")) {
                    checkOutTime = LocalDateTime.now(); // Lấy thời gian hiện tại khi nhấn nút check-out
                    System.out.println("Check-out at: " + checkOutTime);
                    QRHttpServer.stopServer(); // Dừng server
                }
                DBUtils.changeScene(event, "sign_in.fxml", "Library");
            }
        });
    }

    public void showInfoEdit(String username, String name, String email, String phone) {
        // Save tên gốc
        setOriginUsername(username);
        setOriginName(name);
        setOriginEmail(email);
        setOriginPhone(phone);

        label_username.setText(username);
        label_name.setText(name);
        label_email.setText(email);
        label_phone.setText(phone);

        tf_username.setText(username);
        tf_name.setText(name);
        tf_email.setText(email);
        tf_phone.setText(phone);

        disableEditing();
    }

    private void enableEditing() {
        label_username.setVisible(false);
        label_name.setVisible(false);
        label_email.setVisible(false);
        label_phone.setVisible(false);

        tf_username.setVisible(true);
        tf_name.setVisible(true);
        tf_email.setVisible(true);
        tf_phone.setVisible(true);

        button_save.setVisible(true);
        button_back.setVisible(true);
    }

    private void disableEditing() {
        label_username.setVisible(true);
        label_email.setVisible(true);
        label_phone.setVisible(true);
        label_name.setVisible(true);

        tf_username.setVisible(false);
        tf_email.setVisible(false);
        tf_phone.setVisible(false);
        tf_name.setVisible(false);

        button_save.setVisible(false);
        button_back.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (DBUtils.loggedInRole.equals("STUDENT")) { // scene cua stu

            button_home.setOnAction(event -> DBUtils.changeScene(event, "/com/home_student.fxml", "Library"));
            button_transactionsForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_stu.fxml", "Transactions Information"));
            button_booksForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_stu.fxml", "Books"));
            button_game.setOnAction(event -> DBUtils.changeScene(event, "/com/game/game.fxml", "Game"));
            button_changePassword.setOnAction(event -> DBUtils.changeScene(event, "account_changePass_stu.fxml", "Library"));
        } else if (DBUtils.loggedInRole.equals("LIBRARIAN")) { // scene cua lib

            button_home.setOnAction(event -> DBUtils.changeScene(event, "/com/home_librarian.fxml", "Library"));
            button_booksForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Info"));
            button_transactionsForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_lib.fxml", "Transactions Information"));
            button_students.setOnAction(event -> DBUtils.changeScene(event, "/com/user/student_info.fxml", "Student Info"));
            button_changePassword.setOnAction(event -> DBUtils.changeScene(event, "account_changePass_lib.fxml", "Library"));
            button_checkIn.setOnAction(_ -> {
                checkInTime = LocalDateTime.now();
                System.out.println("Check-in at: " + checkInTime);
            });
            button_attendance.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/attendance/attendance.fxml"));
                    Parent root = loader.load();

                    // Lấy AttendanceController và truyền dữ liệu cần thiết nếu có
                    AttendanceController attendanceController = loader.getController();
                    attendanceController.setTestController(this);

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setTitle("Attendance");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }

        signOutAcc(button_signOut);
        button_delAccount.setOnAction(event -> DBUtils.deleteUser(event, label_username.getText()));
        button_edit.setOnAction(_ -> enableEditing());
        button_save.setOnAction(event -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
                ps.setString(1, tf_username.getText());
                ResultSet rs = ps.executeQuery();

                if (rs.isBeforeFirst() && !Objects.equals(tf_username.getText(), label_username.getText())) {
                    System.out.println("username already exists");
                    AlertUtils.errorAlert("Username already exists!");
                } else {
                    DBUtils.updateUserInfo(event, label_username.getText(), tf_username.getText(), label_name.getText(), tf_name.getText(), tf_email.getText(), tf_phone.getText());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        button_back.setOnAction(_ -> {
            tf_username.setText(getOriginUsername());
            tf_name.setText(getOriginName());
            tf_email.setText(getOriginEmail());
            tf_phone.setText(getOriginPhone());
            disableEditing();
        });
    }

    public LocalDateTime getCheckInTime() {
        if (checkInTime == null) {
            System.out.println("Check-in time is not set.");
            return null; // Trả về null nếu chưa được khởi tạo
        }
        return checkInTime;  // Lấy ngày từ checkInTime
    }

    // Phương thức lấy thời gian check-out (chỉ lấy phần ngày)
    public LocalDateTime getCheckOutTime() {
        if (checkOutTime == null) {
            System.out.println("Check-out time is not set.");
            return null; // Trả về null nếu chưa được khởi tạo
        }
        return checkOutTime; // Lấy ngày từ checkOutTime
    }
}
