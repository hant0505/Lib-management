package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataConnection {

    private static final String DATABASE_URL = "jdbc:mysql://192.168.1.197:3306/library";
    private static final String DATABASE_USER = "hant"; // Tên người dùng
    private static final String DATABASE_PASSWORD = "Thu ha123"; // Mật khẩu người dùng

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Connection successful!");


            // Thực hiện truy vấn để lấy dữ liệu từ bảng
            String selectQuery = "SELECT * FROM users"; // Truy vấn lấy tất cả dữ liệu từ bảng users
            Statement selectStatement = connection.createStatement();
            ResultSet resultSet = selectStatement.executeQuery(selectQuery);

            // Hiển thị kết quả truy vấn
            while (resultSet.next()) {
                System.out.println("User ID: " + resultSet.getString("user_id") + ", Username: " + resultSet.getString("user_name") + ", Password: " + resultSet.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
