package com.user;

import com.attendance.AttendanceController;
import com.effect.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.user.AccountController.checkInTime;
import static com.user.AccountController.signOutAcc;

@SuppressWarnings("CallToPrintStackTrace")
public class ChangePassController implements Initializable {
    @FXML
    private Button button_home;
    @FXML
    private Button button_students;
    @FXML
    private Button button_transactionsForLib;
    @FXML
    private Button button_transactionsForStu;
    @FXML
    private Button button_booksForStu;
    @FXML
    private Button button_booksForLib;
    @FXML
    private Button button_accInfo;
    @FXML
    private Button button_signOut;
    @FXML
    private Button button_delAccount;
    @FXML
    private Button button_save;
    @FXML
    private Button button_game;
    @FXML
    private Button button_attendance;
    @FXML
    private Button button_checkIn;

    @FXML
    private PasswordField pf_curPass;
    @FXML
    private PasswordField pf_newPass;
    @FXML
    private PasswordField pf_reNewPass;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (DBUtils.loggedInRole.equals("STUDENT")) { // scene cua stu

            button_home.setOnAction(event -> DBUtils.changeScene(event, "/com/home_student.fxml", "Library"));
            button_game.setOnAction(event -> DBUtils.changeScene(event, "/com/game/game.fxml", "Game"));
            button_booksForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_stu.fxml", "Books"));
            button_transactionsForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_stu.fxml", "Transactions"));
            button_accInfo.setOnAction(event -> DBUtils.changeScene(event, "account_info_stu.fxml", "Account"));
        } else if (DBUtils.loggedInRole.equals("LIBRARIAN")) {// scene cua lib

            button_home.setOnAction(event -> DBUtils.changeScene(event, "/com/home_librarian.fxml", "Library"));
            button_booksForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Info"));
            button_transactionsForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_lib.fxml", "Transactions"));
            button_students.setOnAction(event -> DBUtils.changeScene(event, "/com/user/student_info.fxml", "Students Info"));
            button_accInfo.setOnAction(event -> DBUtils.changeScene(event, "account_info_lib.fxml", "Library"));
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
                    attendanceController.setTestController(com.user.AccountController.getInstance());

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
        button_delAccount.setOnAction(event -> DBUtils.deleteUser(event, com.user.AccountController.getOriginName()));
        button_save.setOnAction(event -> {
            if (pf_curPass.getText().isEmpty() || pf_reNewPass.getText().isEmpty() || pf_newPass.getText().isEmpty()) {
                AlertUtils.warningAlert("Please fill all the fields!");
            } else if (!pf_newPass.getText().equals(pf_reNewPass.getText())) {
                AlertUtils.errorAlert("New passwords do not match!");
            } else {
                DBUtils.changePassword(event, DBUtils.loggedInUser, pf_curPass.getText(), pf_newPass.getText());
            }
        });

    }
}
