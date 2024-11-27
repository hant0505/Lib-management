package com.attendance;

import com.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class DBUtils {
    public static void addAttendance(String username, LocalDateTime checkIn, LocalDateTime checkOut, Date date, String status) {
        String sql = "INSERT INTO attendance (username, checkIn, checkOut, date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(checkIn));  // Chuyển LocalDateTime thành Timestamp
            preparedStatement.setTimestamp(3, checkOut != null ? Timestamp.valueOf(checkOut) : null);  // Nếu checkOut là null thì set null
            preparedStatement.setDate(4, (java.sql.Date) date);  // Đảm bảo đối tượng Date đã đúng loại
            preparedStatement.setString(5, status);

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Rows inserted: " + rowsInserted);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Attendance> getAllAttendance() {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM attendance";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                Timestamp checkInTimestamp = resultSet.getTimestamp("checkIn");
                LocalDateTime checkIn = (checkInTimestamp != null) ? checkInTimestamp.toLocalDateTime() : null;

                Timestamp checkOutTimestamp = resultSet.getTimestamp("checkOut");
                LocalDateTime checkOut = (checkOutTimestamp != null) ? checkOutTimestamp.toLocalDateTime() : null;

                Date date = resultSet.getDate("date");
                String status = resultSet.getString("status");

                Attendance attendance = new Attendance(id, username, checkIn, checkOut, date, status);
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceList;
    }


    public static Attendance getAttendanceByUsernameAndStatus(String username, String status) {
        String sql = "SELECT * FROM attendance WHERE username = ? AND status = ?";
        Attendance attendance = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, status);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    LocalDateTime checkIn = rs.getTimestamp("checkIn").toLocalDateTime();
                    LocalDateTime checkOut = null;
                    if (rs.getTimestamp("checkOut") != null) {
                        checkOut = rs.getTimestamp("checkOut").toLocalDateTime();
                    }
                    Date date = rs.getDate("date");
                    String statusFromDb = rs.getString("status");

                    attendance = new Attendance(id, username, checkIn, checkOut, date, statusFromDb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendance;
    }

    public static void updateAttendanceStatus(String username, String status, LocalDateTime checkOutTime) {
        String sql = "UPDATE attendance SET status = ?, checkOut = ? WHERE username = ? AND status = 'pending'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, status);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(checkOutTime));
            preparedStatement.setString(3, username);

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
