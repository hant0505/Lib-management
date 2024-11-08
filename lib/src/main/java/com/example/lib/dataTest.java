package com.example.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dataTest {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/library"; // Địa chỉ cơ sở dữ liệu
    private static final String DATABASE_USER = "root"; // Tên người dùng
    private static final String DATABASE_PASSWORD = "Thu ha123"; // Mật khẩu người dùng

    // Phương thức lấy kết nối đến cơ sở dữ liệu
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    public static void main(String[] args) {
        try {
            // Đảm bảo driver đã được tải
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Kết nối đến cơ sở dữ liệu
            try (Connection connection = getConnection()) {
                System.out.println("Connection successful!");

                // Thực hiện truy vấn để lấy dữ liệu từ bảng Book
                String selectQuery = "SELECT * FROM books"; // Truy vấn lấy tất cả dữ liệu từ bảng Book
                Statement selectStatement = connection.createStatement();
                ResultSet resultSet = selectStatement.executeQuery(selectQuery);

                // Hiển thị kết quả truy vấn từ bảng Book
                while (resultSet.next()) {
                    System.out.println("Book ID: " + resultSet.getInt("bookID") +
                            ", Title: " + resultSet.getString("title") +
                            ", Author: " + resultSet.getString("author") +
                            ", Edition: " + resultSet.getInt("edition") +
                            ", Quantity: " + resultSet.getInt("quantity") +
                            ", Available: " + resultSet.getString("available"));
                }

            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}