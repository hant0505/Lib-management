package com.game;

import com.DatabaseConnection;

import java.sql.*;

@SuppressWarnings("CallToPrintStackTrace")
public class DBUtils {
    public static void addUser(String username) {

        String insertSQL = "INSERT INTO user_tree (username, growth_level, water_level, last_attendance_date, waterCount) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, 0);
            preparedStatement.setInt(3, 0);
            preparedStatement.setDate(4, null);
            preparedStatement.setInt(5, 10);

            //Tránh duplicate khi insert
            String checkSql = "SELECT COUNT(*) FROM user_tree WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                preparedStatement.executeUpdate();
            }
            System.out.println("User added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding user to database: " + e.getMessage());
        }
    }

    public static void updateDateDaily(String username, Date date) {
        String query = "UPDATE users SET dateToday = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, date);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Date getDateDaily(String username) {
        String query = "SELECT dateToday FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("dateToday");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getDailyMaxScore() {
        int score = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT dailyMaxScore FROM users WHERE username = ?");
            ps.setString(1, com.user.DBUtils.loggedInUser);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                score = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return score;
    }

    public static void setDailyMaxScore(int dailyMaxScore) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET dailyMaxScore = ? WHERE username = ?");
            ps.setInt(1, dailyMaxScore);
            ps.setString(2, com.user.DBUtils.loggedInUser);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getMaxScore() {
        int score = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT maxScore FROM users WHERE username = ?");
            ps.setString(1, com.user.DBUtils.loggedInUser);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                score = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return score;
    }

    public static void setMaxScore(int maxScore) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET maxScore = ? WHERE username = ?");
            ps.setInt(1, maxScore);
            ps.setString(2, com.user.DBUtils.loggedInUser);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        return 0;
    }

    // Cập nhật số lần tưới nước của người dùng
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

    // Lấy ngày điểm danh cuối cùng từ cơ sở dữ liệu
    public static Date getLastAttendanceDate(String username) {
        String query = "SELECT last_attendance_date FROM user_tree WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("last_attendance_date"); // Trả về ngày điểm danh cuối cùng
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    // Cập nhật trạng thái cây của người dùng
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

    // MỨC CÂY VÀ MỨC NƯỚC
    public static Tree getTreeState(String username) {
        String query = "SELECT growth_level, water_level FROM user_tree WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int growthLevel = rs.getInt("growth_level");
                int waterLevel = rs.getInt("water_level");
                return new Tree(growthLevel, waterLevel, "/com/example/demo/tree_level" + growthLevel + ".png");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
