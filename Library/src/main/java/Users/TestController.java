package Users;

import Attendance.AttendanceController;
import Book.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class TestController implements Initializable {

    @FXML
    private Button button_checkIn;

    @FXML
    private Button button_checkOut;
    @FXML
    private Button button_show;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
        // Xử lý sự kiện cho nút checkIn
        button_checkIn.setOnAction(event -> {
            checkInTime = LocalDateTime.now();
            System.out.println("Check-in at: " + checkInTime);
        });

        // Xử lý sự kiện cho nút checkOut
        button_checkOut.setOnAction(event -> {
            checkOutTime = LocalDateTime.now(); // Lấy thời gian hiện tại khi nhấn nút check-out
            System.out.println("Check-out at: " + checkOutTime);
        });

        button_show.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Attendance/attendance.fxml"));
                Parent root = loader.load();

                // Lấy AttendanceController và truyền TestController vào
                AttendanceController attendanceController = loader.getController();
                attendanceController.setTestController(this);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Attendance");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public LocalDateTime getCheckInTime() {
        if (checkInTime == null) {
            System.out.println("Check-in time is not set.");
            return null; // Trả về null nếu chưa được khởi tạo
        }
        return this.checkInTime;  // Lấy ngày từ checkInTime
    }

    // Phương thức lấy thời gian check-out (chỉ lấy phần ngày)
    public LocalDateTime getCheckOutTime() {
        if (checkOutTime == null) {
            System.out.println("Check-out time is not set.");
            return null; // Trả về null nếu chưa được khởi tạo
        }
        return this.checkOutTime; // Lấy ngày từ checkOutTime
    }
}
