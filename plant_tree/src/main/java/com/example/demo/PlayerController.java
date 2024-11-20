package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerController {

    @FXML
    private Label coinsLabel;

    private Player player;
    private String username = "player1"; // Lấy từ hệ thống đăng nhập

    @FXML
    public void initialize() {
        player = new Player();

        // Lấy số coin từ cơ sở dữ liệu
        int coins = UserDAO.getCoins(username);
        player.setCoins(coins);

        // Hiển thị số coin lên giao diện
        updateCoinsLabel();
    }

    @FXML
    private void handleEarnCoins() {
        player.earnCoins(10);
        updateCoinsLabel();

        // Cập nhật số coin vào cơ sở dữ liệu
        UserDAO.updateCoins(username, player.getCoins());
    }

    @FXML
    private void handleSpendCoins() {
        if (player.getCoins() >= 5) {
            player.spendCoins(5);
            updateCoinsLabel();

            // Cập nhật số coin vào cơ sở dữ liệu
            UserDAO.updateCoins(username, player.getCoins());
        } else {
            System.out.println("Not enough coins to spend!");
        }
    }

    private void updateCoinsLabel() {
        coinsLabel.setText(String.valueOf(player.getCoins()));
    }
}
