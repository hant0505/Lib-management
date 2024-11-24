package com.example.demo;

//import com.example.demo.Direction; // Điều chỉnh đúng đường dẫn tới enum Direction trong game
//import com.mysql.cj.x.protobuf.MysqlxCrud;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.Random;

public class Game2048 {

    public static final int SIZE = 4;
    private int[][] board;
    private Random random;

    @FXML
    private GridPane gameGrid;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private AnchorPane rootPane; // Thêm phần này để truy cập cảnh
    @FXML
    private Label loserLabel; // Đảm bảo rằng bạn đã khai báo đúng ID của Label trong FXML


    private int score = 0;

    public Game2048() {
        board = new int[SIZE][SIZE];
        random = new Random();
    }

    @FXML
    public void initialize() {
        gameGrid.setHgap(5.7);  // Tạo khoảng cách ngang giữa các ô
        gameGrid.setVgap(5.7);  // Tạo khoảng cách dọc giữa các ô
        addRandomDigit(2);
        addRandomDigit(2);
        updateBoard();
        // Thêm bộ lọc sự kiện phím
        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case LEFT:
                    handleLeft();
                    break;
                case RIGHT:
                    handleRight();
                    break;
                case UP:
                    handleUp();
                    break;
                case DOWN:
                    handleDown();
                    break;
            }
        });

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
        // Đảm bảo việc cập nhật giao diện diễn ra trên luồng chính của JavaFX
        Platform.runLater(() -> {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    // Tìm nút Button hiện tại trong GridPane
                    Button button = (Button) getNodeFromGrid(gameGrid, j, i);
                    if (button == null) {
                        // Nếu ô chưa có nút, thêm mới
                        button = new Button();
                        button.setMinSize(65, 65);
//                        button.setMinSize(150, 150);
//                        button.setMaxSize(150, 150);
                        gameGrid.add(button, j, i);
                    }
                    // Cập nhật giá trị và màu sắc cho Button
                    button.setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
                    button.setStyle(getColorForValue(board[i][j]));

                    // Thêm animation nếu cần
//                    if (board[i][j] != 0) {
//                        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
//                        scaleTransition.setFromX(0.97); // Co nhỏ lại
//                        scaleTransition.setFromY(0.97);
//                        scaleTransition.setToX(1.0); // Quay về kích thước ban đầu
//                        scaleTransition.setToY(1.0);
//                        scaleTransition.play();
//                    }
                }
            }
            scoreLabel.setText("Score: " + score);
            if (isGameOver()) {
                //s_howLoserMessage();
                showLoserMessage();
                statusLabel.setText("Loser");
            } else {
                statusLabel.setText(""); // Reset trạng thái nếu game chưa kết thúc
            }
        });
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

    // Di chuyển các ô sang trái
    private void moveLeft() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < SIZE; i++) {
                    int[] row = board[i];
                    int[] newRow = processLeftMove(row);
                    board[i] = newRow;
                    updateProgress(i + 1, SIZE);
                    Thread.sleep(20); // Giả lập thời gian xử lý
                }
                addRandomDigit(2);
                updateBoard();
                //playMoveAnimation(); // Gọi animation sau khi di chuyển
                return null;
            }
        };
        runTask(task);
    }

    // Di chuyển các ô sang phải
    private void moveRight() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < SIZE; i++) {
                    int[] row = board[i];
                    int[] newRow = processRightMove(row);
                    board[i] = newRow;
                    updateProgress(i + 1, SIZE);
                    Thread.sleep(20);
                }
                addRandomDigit(2);
                updateBoard();
                //playMoveAnimation(); // Gọi animation sau khi di chuyển
                return null;
            }
        };
        runTask(task);
    }

    // Di chuyển các ô lên
    private void moveUp() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int j = 0; j < SIZE; j++) {
                    int[] col = new int[SIZE];
                    for (int i = 0; i < SIZE; i++) {
                        col[i] = board[i][j];
                    }
                    int[] newCol = processLeftMove(col);
                    for (int i = 0; i < SIZE; i++) {
                        board[i][j] = newCol[i];
                    }
                    updateProgress(j + 1, SIZE);
                    Thread.sleep(20);
                }
                addRandomDigit(2);
                updateBoard();
                //playMoveAnimation(); // Gọi animation sau khi di chuyển
                return null;
            }
        };
        runTask(task);
    }

    // Di chuyển các ô xuống
    private void moveDown() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int j = 0; j < SIZE; j++) {
                    int[] col = new int[SIZE];
                    for (int i = 0; i < SIZE; i++) {
                        col[i] = board[i][j];
                    }
                    int[] newCol = processRightMove(col);
                    for (int i = 0; i < SIZE; i++) {
                        board[i][j] = newCol[i];
                    }
                    updateProgress(j + 1, SIZE);
                    Thread.sleep(20);
                }
                addRandomDigit(2);
                updateBoard();
                //playMoveAnimation(); // Gọi animation sau khi di chuyển
                return null;
            }
        };
        runTask(task);
    }



    // Phương thức này tìm node trong GridPane tại chỉ số (col, row)
    private Node getNodeFromGrid(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;  // Nếu không tìm thấy, trả về null
    }


    private void playMoveAnimation() {
        Platform.runLater(() -> {
            // Duyệt qua từng ô trong bảng
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] != 0) {
                        // Lấy ô game đã có trong GridPane
                        Button button = (Button) getNodeFromGrid(gameGrid, j, i);
                        if (button == null) {
                            button = new Button();
                            button.setMinSize(60, 60);
                            gameGrid.add(button, j, i);
                        }
                        button.setText(String.valueOf(board[i][j]));
                        button.setStyle(getColorForValue(board[i][j]));

                        // Tính toán vị trí di chuyển
                        double startX = button.getLayoutX();
                        double startY = button.getLayoutY();
                        double targetX = j * 70 + 10;
                        double targetY = i * 70 + 10;

                        // Tạo hiệu ứng nảy (thạch) khi ô di chuyển
                        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
                        scaleTransition.setFromX(1.0); // Kích thước ban đầu
                        scaleTransition.setFromY(1.0); // Kích thước ban đầu
                        scaleTransition.setToX(1.3);   // Phóng to một chút
                        scaleTransition.setToY(1.3);   // Phóng to một chút

                        ScaleTransition scaleBackTransition = new ScaleTransition(Duration.millis(100), button);
                        scaleBackTransition.setFromX(1.3);
                        scaleBackTransition.setFromY(1.3);
                        scaleBackTransition.setToX(1.0);
                        scaleBackTransition.setToY(1.0);

                        // Tạo hiệu ứng di chuyển nhẹ (Translate)
                        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200), button);
                        translateTransition.setByX(targetX - startX);  // Di chuyển theo chiều X
                        translateTransition.setByY(targetY - startY);  // Di chuyển theo chiều Y
                        translateTransition.setInterpolator(Interpolator.EASE_BOTH);

                        // Kết hợp các animation lại trong một Timeline
                        Timeline timeline = new Timeline();
                        KeyFrame moveX = new KeyFrame(
                                Duration.ZERO,
                                new KeyValue(button.translateXProperty(), startX)
                        );
                        KeyFrame moveY = new KeyFrame(
                                Duration.ZERO,
                                new KeyValue(button.translateYProperty(), startY)
                        );

                        // Thêm KeyFrames cho di chuyển và hiệu ứng phóng to/thạch
                        KeyFrame targetMoveX = new KeyFrame(
                                Duration.millis(150), // Thời gian di chuyển
                                new KeyValue(button.translateXProperty(), targetX, Interpolator.EASE_BOTH) // Di chuyển tới vị trí mới
                        );
                        KeyFrame targetMoveY = new KeyFrame(
                                Duration.millis(150), // Thời gian di chuyển
                                new KeyValue(button.translateYProperty(), targetY, Interpolator.EASE_BOTH) // Di chuyển tới vị trí mới
                        );

                        timeline.getKeyFrames().addAll(moveX, moveY, targetMoveX, targetMoveY);

                        // Chạy các animation đồng thời
                        timeline.setOnFinished(event -> {
                            scaleTransition.play();
                            scaleBackTransition.play();
                            translateTransition.play();
                        });

                        timeline.setCycleCount(1);
                        timeline.play();
                    }
                }
            }
        });
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

    private boolean isGameOver() {
        // Kiểm tra xem còn ô trống không
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return false;  // Có ô trống, game chưa kết thúc
                }
            }
        }

        // Kiểm tra khả năng hợp nhất trên dòng và cột
        // Kiểm tra xem có thể di chuyển hoặc gộp ô nào không
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (i < SIZE - 1 && board[i][j] == board[i + 1][j]) return false; // Kiểm tra theo chiều dọc
                if (j < SIZE - 1 && board[i][j] == board[i][j + 1]) return false; // Kiểm tra theo chiều ngang
            }
        }

        // Nếu không còn ô trống và không thể gộp ô nào nữa, game kết thúc
        return true;
    }

//    //15_11
//    private boolean isGameOver() {
//        // Kiểm tra có ô nào trống không
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                if (board[i][j] == 0) {
//                    return false; // Còn ô trống, game chưa thua
//                }
//            }
//        }
//
//        // Kiểm tra khả năng hợp nhất trên dòng và cột
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE - 1; j++) {
//                // Kiểm tra theo hàng
//                if (board[i][j] == board[i][j + 1]) {
//                    return false; // Có ô có thể hợp nhất theo hàng, game chưa thua
//                }
//                // Kiểm tra theo cột
//                if (board[j][i] == board[j + 1][i]) {
//                    return false; // Có ô có thể hợp nhất theo cột, game chưa thua
//                }
//            }
//        }
//
//        // Nếu không có ô trống và không có ô nào có thể hợp nhất
//        return true; // Game over
//    }

    // Phương thức để hiển thị "LOSER" với hiệu ứng zoom out
    public void showLoserMessage() {
        // Hiển thị chữ "LOSER"
        loserLabel.setVisible(true);

        // Tạo hiệu ứng phóng to và thu nhỏ chữ "LOSER"
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), loserLabel);
        scaleTransition.setFromX(1);  // Kích thước ban đầu
        scaleTransition.setFromY(1);  // Kích thước ban đầu
        scaleTransition.setToX(3);   // Phóng to gấp 3 lần
        scaleTransition.setToY(3);   // Phóng to gấp 3 lần
        scaleTransition.setCycleCount(1);  // Lặp lại 1 lần
        scaleTransition.setAutoReverse(true);  // Tự động thu nhỏ lại

        // Sau khi hiệu ứng kết thúc, ẩn chữ "LOSER"
        scaleTransition.setOnFinished(event -> loserLabel.setVisible(false));

        // Chạy hiệu ứng phóng to
        scaleTransition.play();
    }



    private void s_howLoserMessage() {
        System.out.println("Loser! Không còn di chuyển nào có thể thực hiện được.");

        // Hiển thị thông báo trong JavaFX
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Bạn đã thua!");
            alert.setContentText("Không còn di chuyển nào có thể thực hiện được.");
            alert.showAndWait();
        });
    }


    // Chạy task trong một luồng riêng
    private void runTask(Task<Void> task) {

        // Đoạn này xử lý khi task hoàn thành
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            // Cập nhật giao diện khi task hoàn tất
            //KO CẦN LẮM
            // scoreLabel.setText("Tác vụ hoàn tất!");
        }));

        // Đoạn này xử lý khi task thất bại
        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            exception.printStackTrace();  // In lỗi ra console
            Platform.runLater(() -> {
                scoreLabel.setText("Tác vụ thất bại!");
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
