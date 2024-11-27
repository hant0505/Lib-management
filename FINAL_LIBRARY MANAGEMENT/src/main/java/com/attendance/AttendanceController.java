package com.attendance;


import com.user.AccountController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.ResourceBundle;

import static com.user.AccountController.checkInTime;

public class AttendanceController implements Initializable {
    @FXML
    private Button  button_home;
    @FXML
    private Button button_students;
    @FXML
    private Button button_booksForLib;
    @FXML
    private Button button_transactionsForLib;
    @FXML
    private Button button_accInfo;
    @FXML
    private Button button_changePassword;
    @FXML
    private Button button_checkIn;
    @FXML
    private Button button_signOut;
    @FXML
    private Button button_delAccount;

    @FXML
    private TableView<Attendance> attendanceTableView;
    @FXML
    private TableColumn<Attendance, Integer> id;
    @FXML
    private TableColumn<Attendance, String> username;
    @FXML
    private TableColumn<Attendance, LocalDate> checkIn;
    @FXML
    private TableColumn<Attendance, LocalDate> checkOut;
    @FXML
    private TableColumn<Attendance, Date> date;
    @FXML
    private TableColumn<Attendance, String> status;

    private AccountController accountController;

    private final ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        checkIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        attendanceTableView.setItems(attendanceList);

        button_home.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/home_librarian.fxml", "Library"));
        button_booksForLib.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Information"));
        button_transactionsForLib.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/book/transactions_lib.fxml", "Transactions Information"));
        button_students.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/user/student_info.fxml", "Student Information"));
        button_changePassword.setOnAction(event -> com.user.DBUtils.changeScene(event, "account_changePass_lib.fxml", "Library"));
        button_accInfo.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/user/account_info_lib.fxml", "Account Information"));
        button_checkIn.setOnAction(_ -> {
            checkInTime = LocalDateTime.now();
            System.out.println("Check-in at: " + checkInTime);
        });
        button_signOut.setOnAction(_ -> AccountController.signOutAcc(button_signOut));
        button_delAccount.setOnAction(event -> com.user.DBUtils.deleteUser(event, com.user.DBUtils.loggedInUser));

    }

    public void setTestController(AccountController accountController) {
        this.accountController = accountController;

        if (accountController == null) {
            System.out.println("TestController is null.");
            return;
        }

        // Debug: Kiểm tra giá trị check-in và check-out từ TestController
        LocalDateTime checkInTime = accountController.getCheckInTime();
        LocalDateTime checkOutTime = accountController.getCheckOutTime();

        if (checkInTime != null) {
            System.out.println("Received Check-in Time: " + checkInTime);
        }
        if (checkOutTime != null) {
            System.out.println("Received Check-out Time: " + checkOutTime);
        }

        loadData();
    }

    private void loadData() {
        if (accountController == null) {
            System.out.println("TestController is not set. Skipping loadData.");
            return;
        }

        // Lấy thông tin check-in và check-out từ TestController
        String usernameFromSession = com.user.DBUtils.loggedInUser;
        LocalDateTime checkInTime = accountController.getCheckInTime();
        LocalDateTime checkOutTime = accountController.getCheckOutTime();

        if (checkInTime != null && checkOutTime == null) {
            // Kiểm tra nếu đã tồn tại attendance với trạng thái 'pending'
            Attendance existingAttendance = DBUtils.getAttendanceByUsernameAndStatus(usernameFromSession, "pending");

            if (existingAttendance == null) {
                // Thêm attendance mới nếu chưa tồn tại
                Attendance newAttendance = new Attendance(usernameFromSession, checkInTime, null, Date.valueOf(checkInTime.toLocalDate()), "pending");
                attendanceList.add(newAttendance);
                DBUtils.addAttendance(usernameFromSession, checkInTime, null, Date.valueOf(checkInTime.toLocalDate()), "pending");
                System.out.println("Added new attendance for user: " + usernameFromSession);
            } else {
                System.out.println("Attendance already exists with status 'pending'.");
            }
        }

        if (checkInTime != null && checkOutTime != null) {
            // Cập nhật attendance nếu có check-out
            DBUtils.updateAttendanceStatus(usernameFromSession, "checkout", checkOutTime);
        }

        // Lấy tất cả attendance từ DB và cập nhật TableView
        attendanceList.clear();
        attendanceList.addAll(DBUtils.getAllAttendance());
        attendanceTableView.setItems(attendanceList);

        // CHECK
        System.out.println("Loaded Attendance Data: " + attendanceList);
        attendanceTableView.refresh();
    }
}
