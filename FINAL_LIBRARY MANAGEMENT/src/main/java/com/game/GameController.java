package com.game;

import com.effect.AlertUtils;
import com.user.DBUtils;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.game.DBUtils.setDailyMaxScore;
import static com.game.DBUtils.setMaxScore;
import static com.game.PlantController.username;
import static javafx.scene.control.ButtonType.OK;

@SuppressWarnings({"CallToPrintStackTrace", "OptionalGetWithoutIsPresent"})
public class GameController implements Initializable {
    public static final int SIZE = 4;
    private final int[][] board;
    private final Random random;
    int[] possibleDigits = {2, 4, 8};// Mảng chứa các số có thể random

    @FXML
    private Button button_home;
    @FXML
    private Button button_transactionsForStu;
    @FXML
    private Button button_booksForStu;
    @FXML
    private Button button_account;
    @FXML
    private Button button_tree;
    @FXML
    private Button button_start;

    @FXML
    private GridPane gameGrid;

    @FXML
    private Label scoreLabel;
    @FXML
    private Label maxScoreLabel;
    @FXML
    private Label dailyMaxScoreLabel;
    @FXML
    private Label loserLabel;

    @FXML
    private AnchorPane rootPane; // Thêm phần này để truy cập cảnh

    @FXML
    private ProgressBar maxScoreBar;
    @FXML
    private ImageView imageGift2000;
    @FXML
    private ImageView imageGift4000;
    @FXML
    private ImageView imageGift6000;
    @FXML
    private ImageView imageGift8000;
    @FXML
    private ImageView imageGift10000;

    private int score = 0;
    private int dailyMaxScore = 0;
    private int maxScore = 0;
    private boolean gameActive = false; // Trạng thái game

    public GameController() {
        board = new int[SIZE][SIZE];
        random = new Random();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_home.setOnAction(event -> DBUtils.changeScene(event, "/com/home_student.fxml", "Home"));
        button_account.setOnAction(event -> DBUtils.changeScene(event, "/com/user/account_info_stu.fxml", "Account"));
        button_booksForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_stu.fxml", "Books"));
        button_transactionsForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_stu.fxml", "Transactions"));
        button_tree.setOnAction(event -> DBUtils.changeScene(event, "/com/game/plant.fxml", "Game"));

        // Hiển thị bảng trống khi khởi tạo ứng dụng
        resetBoard(); // Đặt lại bảng
        updateBoard(); // Hiển thị bảng trống ngay khi khởi chạy
        scheduleResetAtMidnight(); // Lên lịch reset mỗi ngày
        resetScoreDaily();
        updateProgress();

        // khoi tao game vs 2 so 2
        button_start.setOnAction(_ -> {
            if (!gameActive) {
                gameActive = true; // Đánh dấu game bắt đầu
                resetBoard();      // Xóa bàn chơi
                addRandomDigit(2);
                addRandomDigit(2);
                updateBoard();
            }
        });

        // Thêm bộ lọc sự kiện phím
        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (gameActive) {
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
            }

        });
    }

    private void resetScoreDaily() {
        LocalDate currentDate = LocalDate.now();
        Date daily = com.game.DBUtils.getDateDaily(username);
        LocalDate dateDaily = null;

        if (daily != null) {
            dateDaily = daily.toLocalDate();
        }

        // Kiểm tra và reset nếu cần
        if (dateDaily == null || !currentDate.isEqual(dateDaily)) {
            com.game.DBUtils.setDailyMaxScore(0);
            com.game.DBUtils.updateDateDaily(username, Date.valueOf(currentDate));
        }
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
            double buttonSize = 380.0 / SIZE;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    Button button = (Button) getNodeFromGrid(gameGrid, j, i);
                    if (button == null) {
                        button = new Button();
                        button.setMinSize(buttonSize, buttonSize);
                        button.setMaxSize(buttonSize, buttonSize);
                        gameGrid.add(button, j, i);
                    }
                    button.setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
                    button.setStyle(
                            getColorForValue(board[i][j]) +
                                    "-fx-font-size: 24px;" +
                                    "-fx-font-family: 'Century Gothic';"
                    );
                }
            }
            scoreLabel.setText("Score: " + score);
            if (isGameOver()) {
                gameActive = false;
                checkAndUpdateMaxScores();
                updateProgress();
                showLoserMessage();
            }
        });
    }

    private void checkAndUpdateMaxScores() {
        int currentDailyMax = com.game.DBUtils.getDailyMaxScore();
        int currentMax = com.game.DBUtils.getMaxScore();

        if (score > currentDailyMax) {
            com.game.DBUtils.setDailyMaxScore(score);
        }
        if (score > currentMax) {
            com.game.DBUtils.setMaxScore(score);
        }
    }


    private void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
        score = 0; // Reset current score
        updateProgress();
    }

    // Update ProgressBar & Label Score
    private void updateProgress() {
        maxScore = com.game.DBUtils.getMaxScore();
        dailyMaxScore = com.game.DBUtils.getDailyMaxScore();
        String username = DBUtils.loggedInUser;

        if (score >= 2000) {
            com.game.DBUtils.updateWaterCount(username, com.game.DBUtils.getWaterCount(username) + 1);
            AlertUtils.infoAlert("Congratulations! You have received 1 water!");
        }
        if (score >= 4000) {
            com.game.DBUtils.updateWaterCount(username, com.game.DBUtils.getWaterCount(username) + 2);
            AlertUtils.infoAlert("Congratulations! You have received 2 water!");
        }
        if (score >= 6000) {
            com.game.DBUtils.updateWaterCount(username, com.game.DBUtils.getWaterCount(username) + 3);
            AlertUtils.infoAlert("Congratulations! You have received 3 water!");
        }
        if (score >= 8000) {
            com.game.DBUtils.updateWaterCount(username, com.game.DBUtils.getWaterCount(username) + 4);
            AlertUtils.infoAlert("Congratulations! You have received 4 water!");

        }
        if (score >= 10000) {
            com.game.DBUtils.updateWaterCount(username, com.game.DBUtils.getWaterCount(username) + 5);
            AlertUtils.infoAlert("Congratulations! You have received 5 water!");
        }
        Platform.runLater(() -> {
            dailyMaxScoreLabel.setText("Daily Max Score: " + dailyMaxScore);
            maxScoreLabel.setText("Max Score: " + maxScore);
            double progress = Math.min((double) dailyMaxScore / 10000, 1.0);
            maxScoreBar.setProgress(progress);
            if (dailyMaxScore >= 2000) {
                imageGift2000.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/Image/gift_claimed.png")).toExternalForm()));
            }
            if (dailyMaxScore >= 4000) {
                imageGift4000.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/Image/gift_claimed.png")).toExternalForm()));

            }
            if (dailyMaxScore >= 6000) {
                imageGift6000.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/Image/gift_claimed.png")).toExternalForm()));
            }
            if (dailyMaxScore >= 8000) {
                imageGift8000.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/Image/gift_claimed.png")).toExternalForm()));
            }
            if (dailyMaxScore >= 10000) {
                imageGift10000.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/Image/gift_claimed.png")).toExternalForm()));
            }
        });
    }

    // Lên lịch reset vào lúc 12 AM
    private void scheduleResetAtMidnight() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        long initialDelay = calculateInitialDelay();
        scheduler.scheduleAtFixedRate(this::resetDailyProgress, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    private long calculateInitialDelay() {
        long currentTime = System.currentTimeMillis();
        long targetTime = getTargetTime();

        if (currentTime > targetTime) {
            targetTime += TimeUnit.DAYS.toMillis(1);
        }

        return targetTime - currentTime;
    }

    private long getTargetTime() {
        LocalTime now = LocalTime.now();
        LocalTime midnight = LocalTime.MIDNIGHT;
        if (now.isAfter(midnight)) {
            return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        }
        return System.currentTimeMillis();
    }

    // Reset progress và cập nhật ngày
    private void resetDailyProgress() {
        Platform.runLater(() -> {
            com.game.DBUtils.setDailyMaxScore(0); // Reset trong DB
            score = 0;
            updateProgress();
            System.out.println("Daily Max Score reset.");
        });
    }

    private String getColorForValue(int value) {
        return switch (value) {
            case 0 -> "-fx-background-color: lightgray;";
            case 2 -> "-fx-background-color: #eee4da;";
            case 4 -> "-fx-background-color: #ede0c8;";
            case 8 -> "-fx-background-color: #f2b179;";
            case 16 -> "-fx-background-color: #f59563;";
            case 32 -> "-fx-background-color: #f67c5f;";
            case 64 -> "-fx-background-color: #f65e3b;";
            case 128 -> "-fx-background-color: #edcf72;";
            case 256 -> "-fx-background-color: #edcc61;";
            case 512 -> "-fx-background-color: #edc850;";
            case 1024 -> "-fx-background-color: #edc53f;";
            case 2048 -> "-fx-background-color: #edc22e;";
            default -> "-fx-background-color: #d7d9d7;";

        };
    }

    // Hàm di chuyển chung
    private void move(int direction) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean moved = false; // Biến kiểm tra nếu có sự thay đổi

                for (int i = 0; i < SIZE; i++) {
                    // Lấy hàng hoặc cột, tuỳ vào hướng di chuyển
                    int[] line = (direction == 1 || direction == 2) ? board[i] : getColumn(i);

                    int[] newLine = null;

                    // Xử lý di chuyển cho từng hướng
                    if (direction == 1 || direction == 2) {
                        // Di chuyển trái hoặc phải
                        newLine = (direction == 1) ? processLeftMove(line) : processRightMove(line);
                    } else if (direction == 3 || direction == 4) {
                        // Di chuyển lên hoặc xuống
                        newLine = (direction == 3) ? processUpMove(line) : processDownMove(line);
                    }

                    // So sánh hai mảng, nếu khác nhau thì có sự thay đổi
                    if (!Arrays.equals(line, newLine)) {
                        moved = true;
                    }

                    // Cập nhật lại hàng hoặc cột sau khi di chuyển
                    if (direction == 1 || direction == 2) {
                        board[i] = newLine;
                    } else {
                        setColumn(i, newLine);
                    }

                    updateProgress(i + 1, SIZE);
                    Thread.sleep(15); // Tạm dừng để thực hiện hiệu ứng động
                }
                //Kiểm tra sự thay đổi và thêm số ngẫu nhiên nếu có sự thay đổi
                if (moved) {
                    addRandomDigit(possibleDigits[random.nextInt(possibleDigits.length)]);
                    updateBoard();
                }
                return null;
            }
        };
        runTask(task);
    }

    private int[] getColumn(int colIndex) {
        int[] col = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            col[i] = board[i][colIndex];
        }
        return col;
    }

    // Cập nhật cột sau khi di chuyển
    private void setColumn(int colIndex, int[] newCol) {
        for (int i = 0; i < SIZE; i++) {
            board[i][colIndex] = newCol[i];
        }
    }

    private void moveLeft() {
        move(1);
    }

    private void moveRight() {
        move(2);
    }

    private void moveUp() {
        move(3);
    }

    private void moveDown() {
        move(4);
    }

    private Node getNodeFromGrid(GridPane grid, int col, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
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

    private int[] processUpMove(int[] col) {
        return processLeftMove(col);
    }

    private int[] processDownMove(int[] col) {
        int[] reversed = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            reversed[i] = col[SIZE - 1 - i];
        }
        int[] newCol = processLeftMove(reversed);
        int[] result = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            result[i] = newCol[SIZE - 1 - i];
        }
        return result;
    }


    private boolean isGameOver() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return false;  // Có ô trống, game chưa kết thúc
                }
            }
        }
        // Kiểm tra xem có thể di chuyển hoặc gộp ô nào không
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (i < SIZE - 1 && board[i][j] == board[i + 1][j]) return false;
                if (j < SIZE - 1 && board[i][j] == board[i][j + 1]) return false;
            }
        }
        return true; // Nếu không còn ô trống và không thể gộp ô nào nữa, game kết thúc
    }

    public void showLoserMessage() {
        loserLabel.setVisible(true);
        // Tạo hiệu ứng phóng to và thu nhỏ chữ "LOSER"
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), loserLabel);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(5);
        scaleTransition.setToY(5);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);

        // Sau khi hiệu ứng kết thúc, ẩn chữ "LOSER"
        scaleTransition.setOnFinished(_ -> {
            loserLabel.setVisible(false);
            Platform.runLater(() -> {
                Alert a = AlertUtils.confirmAlert("Game Over! \n" + "Your score: " + score);
                if (a.showAndWait().get() == OK) {
                    setDailyMaxScore(Math.max(score, dailyMaxScore));
                    setMaxScore(Math.max(maxScore, dailyMaxScore));
                    resetBoard();
                }
            });
        });
        scaleTransition.play();
    }

    // Chạy task trong một luồng riêng
    private void runTask(Task<Void> task) {
        //  xử lý khi task thất bại
        task.setOnFailed(_ -> {
            Throwable exception = task.getException();
            exception.printStackTrace();// In lỗi ra console
            Platform.runLater(() -> scoreLabel.setText("Tác vụ thất bại!"));
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

