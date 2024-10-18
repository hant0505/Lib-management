package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Random;

public class Game2048 {

    public static final int SIZE = 4;
    private int[][] board;
    private Random random;

    @FXML
    private GridPane gameGrid;
    @FXML
    private Label scoreLabel;

    private int score = 0;

    public Game2048() {
        board = new int[SIZE][SIZE];
        random = new Random();
    }

    @FXML
    public void initialize() {
        addRandomDigit(2);
        addRandomDigit(2);
        updateBoard();
    }

    private void addRandomDigit(int digit) {
        int i, j;
        do {
            i = random.nextInt(SIZE);
            j = random.nextInt(SIZE);
        } while (board[i][j] != 0);
        board[i][j] = digit;
    }

    private void updateBoard() {
        gameGrid.getChildren().clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Button button = new Button();
                button.setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
                button.setMinSize(70, 70);
                button.setStyle(getColorForValue(board[i][j])); // Thêm màu sắc
                gameGrid.add(button, j, i);
            }
        }
        scoreLabel.setText("Score: " + score);
    }

    private String getColorForValue(int value) {
        switch (value) {
            case 0: return "-fx-background-color: lightgray;"; // Màu cho ô 0
            case 2: return "-fx-background-color: #eee4da;"; // Màu cho ô 2
            case 4: return "-fx-background-color: #ede0c8;"; // Màu cho ô 4
            case 8: return "-fx-background-color: #f2b179;"; // Màu cho ô 8
            case 16: return "-fx-background-color: #f59563;"; // Màu cho ô 16
            case 32: return "-fx-background-color: #f67c5f;"; // Màu cho ô 32
            case 64: return "-fx-background-color: #f67c5f;"; // Màu cho ô 64
            case 128: return "-fx-background-color: #f9e79f;"; // Màu cho ô 128
            case 256: return "-fx-background-color: #f9c8d7;"; // Màu cho ô 256
            case 512: return "-fx-background-color: #f8c8d7;"; // Màu cho ô 512
            case 1024: return "-fx-background-color: #f7c2a8;"; // Màu cho ô 1024
            case 2048: return "-fx-background-color: #f5b29e;"; // Màu cho ô 2048
            default: return "-fx-background-color: #d7d9d7;"; // Màu cho ô lớn hơn 2048
        }
    }

    private void moveLeft() {
        for (int i = 0; i < SIZE; i++) {
            int[] row = board[i];
            int[] newRow = processLeftMove(row);
            board[i] = newRow;
        }
        addRandomDigit(2);
        updateBoard();
    }

    private void moveRight() {
        for (int i = 0; i < SIZE; i++) {
            int[] row = board[i];
            int[] newRow = processRightMove(row);
            board[i] = newRow;
        }
        addRandomDigit(2);
        updateBoard();
    }

    private void moveUp() {
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                col[i] = board[i][j];
            }
            int[] newCol = processLeftMove(col);
            for (int i = 0; i < SIZE; i++) {
                board[i][j] = newCol[i];
            }
        }
        addRandomDigit(2);
        updateBoard();
    }

    private void moveDown() {
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                col[i] = board[i][j];
            }
            int[] newCol = processRightMove(col);
            for (int i = 0; i < SIZE; i++) {
                board[i][j] = newCol[i];
            }
        }
        addRandomDigit(2);
        updateBoard();
    }

    private int[] processLeftMove(int[] row) {
        int[] newRow = new int[SIZE];
        int j = 0;
        for (int i = 0; i < SIZE; i++) {
            if (row[i] != 0) {
                newRow[j++] = row[i];
            }
        }

        for (int i = 0; i < SIZE - 1; i++) {
            if (newRow[i] != 0 && newRow[i] == newRow[i + 1]) {
                newRow[i] *= 2;
                score += newRow[i];
                newRow[i + 1] = 0;
                i++;
            }
        }

        int[] result = new int[SIZE];
        j = 0;
        for (int value : newRow) {
            if (value != 0) {
                result[j++] = value;
            }
        }

        return result;
    }

    private int[] processRightMove(int[] row) {
        int[] reversed = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            reversed[i] = row[SIZE - 1 - i];
        }
        int[] newRow = processLeftMove(reversed);
        int[] result = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            result[i] = newRow[SIZE - 1 - i];
        }
        return result;
    }

    @FXML
    public void handleLeft() {
        moveLeft();
    }

    @FXML
    public void handleRight() {
        moveRight();
    }

    @FXML
    public void handleUp() {
        moveUp();
    }

    @FXML
    public void handleDown() {
        moveDown();
    }
}
