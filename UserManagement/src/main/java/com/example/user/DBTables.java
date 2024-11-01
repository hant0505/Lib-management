package com.example.user;
import java.sql.*;

@SuppressWarnings("CallToPrintStackTrace")
public class DBTables {
        public static void main(String[] args) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library",
                        "root", "0730AyUu");
                Statement ppapStatement = conn.createStatement();
                String str = "SELECT * FROM users";
                ResultSet resSet = ppapStatement.executeQuery(str);
                while (resSet.next()) {
                    System.out.print("user: ");
                    System.out.print(resSet.getString("username"));
                    System.out.print(" - pass: ");
                    System.out.print(resSet.getString("password"));
                    System.out.println();
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
