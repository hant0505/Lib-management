package Attendance;

import Users.TestController;
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

public class AttendanceController implements Initializable {

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

    @FXML
    private Button button_close;

    private TestController testController;

    private final ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        checkIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void setTestController(TestController testController) {
        this.testController = testController;

        if (testController == null) {
            System.out.println("TestController is null.");
            return;
        }

        // Debug: Kiểm tra giá trị check-in và check-out từ TestController
        LocalDateTime checkInTime = testController.getCheckInTime();
        LocalDateTime checkOutTime = testController.getCheckOutTime();

        if (checkInTime != null) {
            System.out.println("Received Check-in Time: " + checkInTime);
        }
        if (checkOutTime != null) {
            System.out.println("Received Check-out Time: " + checkOutTime);
        }

        loadData();
        button_close.setOnAction(e -> Book.DBUtils.changeScene(e, "test.fxml", "Account"));
    }

    private void loadData() {
        if (testController == null) {
            System.out.println("TestController is not set. Skipping loadData.");
            return;
        }

        // Lấy thông tin check-in và check-out từ TestController
        String usernameFromSession = "hmmm";  // Thử lấy username từ UserSession hoặc mặc định
        LocalDateTime checkInTime = testController.getCheckInTime();
        LocalDateTime checkOutTime = testController.getCheckOutTime();

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



