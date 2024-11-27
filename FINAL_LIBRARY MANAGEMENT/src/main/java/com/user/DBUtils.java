package com.user;

import com.DatabaseConnection;
import com.attendance.Attendance;
import com.book.QR_SCAN;
import com.effect.AlertUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"CallToPrintStackTrace", "OptionalGetWithoutIsPresent"})
public class DBUtils { // helper class contains static method aka actions

    public static String loggedInUser;
    public static String loggedInName;
    public static String loggedInEmail;
    public static String loggedInPhone;
    public static String loggedInRole;

    public static void changeScene(Event event, String fxmlFile, String title) {
        Parent root;
        FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
        try {
            root = loader.load();
            switch (fxmlFile) {
                case "/com/home_librarian.fxml", "home_librarian.fxml" -> {
                    LibrarianHomeController librarianHomeController = loader.getController();
                    librarianHomeController.setUserInfo(loggedInUser, loggedInName, loggedInEmail, loggedInPhone);
                }
                case "account_info_lib.fxml", "/com/user/account_info_lib.fxml", "account_info_stu.fxml",
                     "/com/user/account_info_stu.fxml" -> {
                    AccountController accountController = loader.getController();
                    accountController.showInfoEdit(loggedInUser, loggedInName, loggedInEmail, loggedInPhone);
                }
                case "/com/home_student.fxml", "home_student.fxml" -> {
                    StudentHomeController studentHomeController = loader.getController();
                    studentHomeController.setUserInfo(loggedInUser, loggedInName, loggedInEmail, loggedInPhone);
                }
                default -> {
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(Objects.requireNonNull(root), 1300, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void signUpUser(ActionEvent event, String username, String name, String password, String email, String phone) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Connection conn = null;
                PreparedStatement psInsert;
                PreparedStatement psCheckExists = null;
                ResultSet resultSet = null;

                try {
                    conn = DatabaseConnection.getConnection();
                    psCheckExists = conn.prepareStatement("select * from users where username = ?");
                    psCheckExists.setString(1, username);
                    resultSet = psCheckExists.executeQuery();

                    if (resultSet.isBeforeFirst()) {// username existed
                        Platform.runLater(() -> AlertUtils.errorAlert("User already existed. Change another username!"));

                    } else {
                        psInsert = conn.prepareStatement("insert into users(username, name, password, email, phone, role) values(?,?,?, ?, ?, ?)");
                        psInsert.setString(1, username);
                        psInsert.setString(2, name);
                        psInsert.setString(3, password);
                        psInsert.setString(4, email);
                        psInsert.setString(5, phone);
                        psInsert.setString(6, "STUDENT");

                        psInsert.executeUpdate();

                        loggedInName = name;
                        loggedInUser = username;
                        loggedInEmail = email;
                        loggedInPhone = phone;
                        loggedInRole = "STUDENT";

                        Platform.runLater(() -> changeScene(event, "/com/home_student.fxml", "Library Welcome"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeDatabase(conn, psCheckExists, resultSet);
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void logInUser(ActionEvent event, String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (username.isEmpty() || password.isEmpty())
                AlertUtils.warningAlert("Please enter your username and password!");

            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement("select * from users where username = ?");
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) { // user  not found
                System.out.println("user not found");
                AlertUtils.errorAlert("Provided credentials are incorrect!");
            } else {
                while (rs.next()) {
                    String retrievedPassword = rs.getString("password");
                    if (retrievedPassword.equals(password)) {
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        String phone = rs.getString("phone");
                        String role = rs.getString("role");
                        loggedInUser = username;
                        loggedInName = Objects.requireNonNullElse(name, username);
                        loggedInEmail = email;
                        loggedInPhone = phone;
                        loggedInRole = role;

                        Main mainApp = new Main();
                        mainApp.showSplashScreen((Stage) ((Node) event.getSource()).getScene().getWindow());
                    } else {// Unmatched password
                        System.out.println("Unmatched password");
                        AlertUtils.errorAlert("Provided credentials are incorrect!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, ps, rs);
        }
    }

    public static void deleteUser(ActionEvent event, String username) {
        Alert alert = AlertUtils.confirmAlert("Are you sure you want to delete this user?");
        if (ButtonType.OK == alert.showAndWait().get()) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("delete from users where username = ?");
                ps.setString(1, username);
                ps.executeUpdate();

                changeScene(event, "sign_in.fxml", "Library");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateUserInfo(ActionEvent event, String curUsername, String newUsername, String curName, String newName, String email, String phone) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();

            //Kiểm tra người dùng hiện tại
            ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND name = ?");
            ps.setString(1, curUsername);
            ps.setString(2, curName);
            rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                AlertUtils.errorAlert("Provided credentials are incorrect!");
                return;
            }
            //Cập nhật thông tin
            ps = conn.prepareStatement("UPDATE users SET username = ?, name = ?, email = ?, phone = ? WHERE username = ?");
            ps.setString(1, newUsername);
            ps.setString(2, newName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, curUsername);
            ps.executeUpdate();

            //Cập nhật mã QR cho các giao dịch liên quan
            ps = conn.prepareStatement("SELECT transactionID, bookTitle, borrowedDate, dueDate, status FROM transactions WHERE username = ?");
            ps.setString(1, newUsername);
            rs = ps.executeQuery();
            while (rs.next()) {
                int transactionID = rs.getInt("transactionID");
                String bookTitle = rs.getString("bookTitle");
                LocalDate borrowedDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate dueDate = rs.getDate("dueDate").toLocalDate();
                String status = rs.getString("status");

                QR_SCAN.updateUsernameInQRCode(transactionID, newUsername, bookTitle, borrowedDate, dueDate, status);
            }

            //Cập nhật thông tin người dùng trong hệ thống
            loggedInUser = newUsername;
            loggedInName = newName.isEmpty() ? newUsername : newName;
            loggedInEmail = email;
            loggedInPhone = phone;

            AlertUtils.infoAlert("User updated successfully!");

            if (loggedInRole.equals("STUDENT")) {
                changeScene(event, "account_info_stu.fxml", "Library Student");
            } else {
                changeScene(event, "account_info_lib.fxml", "Library Librarian");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, ps, rs);
        }
    }

    public static void changePassword(ActionEvent event, String username, String oldPass, String newPass) {
        Connection conn = null;
        PreparedStatement psCheckNewPass = null;
        PreparedStatement psChange;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            psCheckNewPass = conn.prepareStatement("select password from users where username = ?");
            psCheckNewPass.setString(1, username);
            rs = psCheckNewPass.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("user not found");
                AlertUtils.errorAlert("User not found!");
            } else {
                rs.next();
                String originPass = rs.getString("password");

                if (!originPass.equals(oldPass)) AlertUtils.errorAlert("Passwords do not match!");
                else {
                    psChange = conn.prepareStatement("update users set password = ? where username = ?");
                    psChange.setString(1, newPass);
                    psChange.setString(2, username);
                    psChange.executeUpdate();

                    AlertUtils.infoAlert("Password changed successfully!");
                }
            }
            if (loggedInRole.equals("STUDENT")) {
                changeScene(event, "account_changePass_stu.fxml", "Library");
            } else changeScene(event, "account_changePass_lib.fxml", "Library");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase(conn, psCheckNewPass, rs);
        }
    }

    public static void addAttendance(String username, LocalDateTime checkIn, LocalDateTime checkOut, Date date, String status) {
        String sql = "INSERT INTO attendance (username, checkIn, checkOut, date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(checkIn));  // Chuyển LocalDateTime thành Timestamp
            preparedStatement.setTimestamp(3, checkOut != null ? Timestamp.valueOf(checkOut) : null);  // Nếu checkOut là null thì set null
            preparedStatement.setDate(4, date);  // Đảm bảo đối tượng Date đã đúng loại
            preparedStatement.setString(5, status);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Attendance> getAllAttendance() {
        return com.attendance.DBUtils.getAllAttendance();
    }


    public static Attendance getAttendanceByUsernameAndStatus(String username, String status) {
        return com.attendance.DBUtils.getAttendanceByUsernameAndStatus(username, status);
    }

    public static void updateAttendanceStatus(String username, String status, LocalDateTime checkOutTime) {
        com.attendance.DBUtils.updateAttendanceStatus(username, status, checkOutTime);
    }

    public static boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean canBorrowMoreBooks(String username) {
        String query = "SELECT COUNT(*) FROM transactions WHERE username = ? AND status = 'Borrowed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            ResultSet resSet = pstmt.executeQuery();

            if (resSet.next()) {
                int borrowedBooks = resSet.getInt(1);
                return borrowedBooks < 5;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getCoins(String username) {
        int coins = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT coins FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) coins = rs.getInt(1);
            else System.out.println("Cannot get coins");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coins;
    }

    public static Map<String, Integer> getTopBorrowedBooks() {
        Map<String, Integer> topBooks = new LinkedHashMap<>();
        String query = """
                    SELECT bookTitle, COUNT(*) AS borrow_count
                    FROM transactions
                    WHERE status = 'Borrowed'
                    GROUP BY bookTitle
                    ORDER BY borrow_count DESC
                    LIMIT 5;
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                topBooks.put(resultSet.getString("booktitle"), resultSet.getInt("borrow_count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return topBooks;
    }

    public static int getBorrowedBookCount() {
        int borrowedCount = 0;
        String query = """
                    SELECT COUNT(*) AS borrowed_count
                    FROM transactions
                    WHERE status = 'Borrowed';
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                borrowedCount = resultSet.getInt("borrowed_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return borrowedCount;
    }

    // Lấy tổng số sách trong thư viện
    public static int getTotalBookCount() {
        int totalCount = 0;
        String query = """
                    SELECT COUNT(*) AS total_count
                    FROM books;
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                totalCount = resultSet.getInt("total_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalCount;
    }

}

