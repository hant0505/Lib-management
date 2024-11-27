package com.example.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class UserDAO {

    // Lấy số coin từ cơ sở dữ liệu
    public static int getCoins(String username) {
        String query = "SELECT coins FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("coins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Cập nhật số coin sau khi thu hoạch
    public static void updateCoins(String username, int newCoins) {
        String query = "UPDATE users SET coins = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newCoins);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /// Lấy ngày điểm danh cuối cùng từ cơ sở dữ liệu
    public static Date getLastAttendanceDate(String username) {
        String query = "SELECT last_attendance_date FROM user_tree WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("last_attendance_date");  // Trả về ngày điểm danh cuối cùng
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Nếu không tìm thấy, trả về null
    }

    // Cập nhật ngày điểm danh cuối cùng
    public static void updateLastAttendanceDate(String username, Date date) {
        String query = "UPDATE user_tree SET last_attendance_date = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, date);  // Set ngày điểm danh mới
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy số lượt tưới của người dùng
    public static int getWaterCount(String username) {
        String query = "SELECT waterCount FROM user_tree WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("waterCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Nếu không tìm thấy, trả về 0
    }

    /// Cập nhật số lần tưới nước của người dùng
    public static void updateWaterCount(String username, int newWaterCount) {
        String query = "UPDATE user_tree SET waterCount = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newWaterCount);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void saveTreeState(String username, int growthLevel, int waterLevel) {
//        String query = "UPDATE Trees SET growth_level = ?, water_level = ? WHERE username = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, growthLevel);
//            stmt.setInt(2, waterLevel);
//            stmt.setString(3, username);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//
//    public static void updateWaterLevel(String username, int newWaterLevel) {
//        String query = "UPDATE user_tree SET water_level = ? WHERE username = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, newWaterLevel);  // Mức nước mới
//            stmt.setString(2, username);  // Tên người dùng
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    /// Cập nhật trạng thái cây của người dùng
    public static void updateTreeStatus(String username, int growthLevel, int waterLevel) {
        String query = "UPDATE user_tree SET growth_level = ?, water_level = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, growthLevel);  // Cấp độ phát triển cây
            stmt.setInt(2, waterLevel);   // Mức nước của cây
            stmt.setString(3, username);  // Tên người dùng
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Lấy trạng thái cây của người dùng
    /// YEA MỨC CÂY VÀ MỨC NƯỚC
    public static Tree getTreeState(String username) {
        String query = "SELECT growth_level, water_level FROM user_tree WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int growthLevel = rs.getInt("growth_level");
                int waterLevel = rs.getInt("water_level");
                return new Tree("Tree", growthLevel, waterLevel, "/com/example/demo/tree_level" + growthLevel + ".png");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
