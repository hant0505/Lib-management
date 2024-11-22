package com.example.lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.time.LocalDate;

public class TransactionHandler {
    public static void handleReturn(String qrData) {
        try {
            // Giả định qrData có định dạng JSON: {"transactionID": "12345", "username": "stuA001"}
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode qrJson = objectMapper.readTree(qrData);

            int transactionID = qrJson.get("transactionID").asInt();
            String username = qrJson.get("username").asText();

            // Kết nối DB
            try (Connection connection = DatabaseConnection.getConnection()) {
                // Kiểm tra giao dịch có hợp lệ không
                String checkSQL = "SELECT * FROM transactions WHERE transactionID = ? AND username = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
                    checkStmt.setInt(1, transactionID);
                    checkStmt.setString(2, username);

                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        String status = rs.getString("status");
                        if ("returned".equalsIgnoreCase(status)) {
                            System.out.println("Sách này đã được trả.");
                            return;
                        }

                        // Cập nhật trạng thái giao dịch
                        String updateSQL = "UPDATE transactions SET status = 'returned', returnedDate = ? WHERE transactionID = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                            updateStmt.setDate(1, Date.valueOf(LocalDate.now()));
                            updateStmt.setInt(2, transactionID);

                            updateStmt.executeUpdate();
                            System.out.println("Giao dịch đã được cập nhật thành 'returned'.");
                        }
                    } else {
                        System.out.println("Không tìm thấy giao dịch hợp lệ.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý mã QR: " + e.getMessage());
        }
    }
}
