package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DATABASE_USER = "root"; // Tên người dùng
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
                System.out.println("User ID: " + resultSet.getString("id")
                        + ", Username: " + resultSet.getString("username")
                        + ", Password: " + resultSet.getString("password")
                    + ", Email: " + resultSet.getString("email")
                + ", Phone: " + resultSet.getString("phone")
                + ", Coin: " + resultSet.getString("coins")
                + ", ngay diem danh cuoi: " + resultSet.getDate("last_attendance_date")+
                        ", lượt tưới: " + resultSet.getString("waterCount"));
            }

            ///TRUY VẤN USER_TREE
            String Qq = "SELECT * FROM user_tree"; // Truy vấn lấy tất cả dữ liệu từ bảng users
            Statement st = connection.createStatement();
            ResultSet rS = st.executeQuery(Qq);

            // Hiển thị kết quả truy vấn
            while (rS.next()) {
                System.out.println("User ID: " + rS.getString("id")
                        + ", Username: " + rS.getString("username")
                        + ", Password: " + rS.getString("password")
                        + ", Email: " + rS.getString("email")
                );
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
