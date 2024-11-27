package com.game;

import com.effect.AlertUtils;
import com.user.DBUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;


public class PlantController {

    public static final String username = DBUtils.loggedInUser;
    private final int maxWaterCount = 100;
    @FXML
    private Button plantTreeButton;
    @FXML
    private Button pointAttendanceButton;
    @FXML
    private Label coinsLabel;
    @FXML
    private VBox treeContainer;
    @FXML
    private Button waterButton;
    @FXML
    private ProgressBar waterProgressBar;
    @FXML
    private Label waterCountLabel;
    @FXML
    private ImageView treeImageView;
    private int waterCount = 0;
    private int currentWaterLevel = 0;
    // Lưu cây vào đây để truy cập lại
    private Tree plantedTree = null;
    private int coins;
    @FXML
    private Button goTo2048Button;

    @FXML
    private void initialize() {
        goTo2048Button.setOnAction(event -> DBUtils.changeScene(event, "/com/game/game.fxml", "Game"));
        com.game.DBUtils.addUser(username);
        loadCoins();
        loadTreeState();
        loadWaterCount();
        checkAttendanceForWatering();
    }

    private void loadTreeState() {
        plantedTree = com.game.DBUtils.getTreeState(username);
        if (plantedTree == null) {
            currentWaterLevel = 0;
            return;
        }

        currentWaterLevel = plantedTree.getWaterLevel();
        if (plantedTree.getCurrentGrowth() == 0) {
            plantedTree = null;
        }

        // Nếu đã trồng cây
        if (plantedTree != null) {
            updateTreeContainer();
            if (!plantedTree.isMature()) {
                plantTreeButton.setDisable(true);
            }
        }
    }

    private void loadCoins() {
        coins = com.game.DBUtils.getCoins(username);
        updateCoinsLabel();
    }

    private void loadWaterCount() {
        waterCount = com.game.DBUtils.getWaterCount(username);
        updateWaterCountLabel();
    }

    private void updateCoinsLabel() {
        coinsLabel.setText("Coins: " + coins);
        com.game.DBUtils.updateCoins(username, coins);
    }


    private void updateWaterCountLabel() {
        if (waterCount == 0) {
            waterButton.setDisable(true);
        }
        waterCountLabel.setText("Water Drop: " + waterCount);
        com.game.DBUtils.updateWaterCount(username, waterCount);  // Cập nhật số lượt tưới vào cơ sở dữ liệu
    }

    private void updateTreeContainer() {
        if (plantedTree != null) {
            // Tạo Label cho trạng thái cây
            String treeStatus = plantedTree.isMature()
                    ? "Tree mature! Ready to harvest!"
                    : "Tree growing... Level " + plantedTree.getCurrentGrowth();

            Label treeLabel = new Label(treeStatus);

            // Tạo ImageView và đặt hình ảnh cây theo cấp độ hiện tại
            Image treeImage = new Image(Objects.requireNonNull(getClass().getResource(plantedTree.getImagePath())).toExternalForm());

            treeImageView.setImage(treeImage);

            if (!plantedTree.isMature()) {
                waterProgressBar.setProgress((double) currentWaterLevel / 100);
            }

            // Thêm các thành phần vào giao diện
            treeContainer.getChildren().clear();
            treeContainer.getChildren().addAll(treeLabel, treeImageView, waterProgressBar, waterCountLabel);

            waterButton.setDisable(plantedTree.isMature());
            waterButton.setVisible(!plantedTree.isMature());
            waterProgressBar.setVisible(!plantedTree.isMature());
        }
    }

    /**
     * kiểm tra xem người chơi đã điểm danh trong ngày hôm đó chưa.
     * đảm bảo người chơi chỉ nhận được một lượt tưới mỗi ngày
     */
    private void checkAttendanceForWatering() {
        LocalDate currentDate = LocalDate.now();
        Date lastAttendanceSQLDate = com.game.DBUtils.getLastAttendanceDate(username); // Lấy ngày điểm danh cuối cùng
        LocalDate lastAttendanceDate = null;

        // Kiểm tra nếu chưa từng điểm danh
        if (lastAttendanceSQLDate != null) {
            lastAttendanceDate = lastAttendanceSQLDate.toLocalDate();
        }

        // Check điểm danh hôm nay
        pointAttendanceButton.setDisable(lastAttendanceDate != null && currentDate.isEqual(lastAttendanceDate));
    }

    @FXML
    private void handlePointAttendance() {
        LocalDate currentDate = LocalDate.now();

        waterCount = Math.min(waterCount + 1, maxWaterCount);
        updateWaterCountLabel();
        com.game.DBUtils.updateLastAttendanceDate(username, Date.valueOf(currentDate));
        //com.game.DBUtils.setDailyMaxScore(0); // reset lại
        pointAttendanceButton.setDisable(true);
        System.out.println("Attendance rewarded! You got +1 watering chance.");
    }

    @FXML
    private void handlePlantTree() {
        if (plantedTree != null && !plantedTree.isMature()) {
            AlertUtils.warningAlert("A tree is already planted and growing. Wait until it matures.");
            return;  // not allow new plant khi cây cũ chưa trưởng thành
        }

        updateCoinsLabel();

        Tree tree = new Tree(1, 0, "/com/Image/tree_level1.png");
        Label treeLabel = new Label("Tree growing...");
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/com/Image/tree_level1.png")).toExternalForm());
        treeImageView.setImage(image);

        plantedTree = tree;

        treeContainer.getChildren().clear();
        treeContainer.getChildren().addAll(treeLabel, treeImageView, waterProgressBar, waterCountLabel);
        waterButton.setVisible(true);
        waterProgressBar.setVisible(true);
        waterButton.setDisable(false);

        plantTreeButton.setDisable(true);
    }

    @FXML
    private void handleWaterTree() {
        if (waterCount == 0) {
            AlertUtils.errorAlert("No more watering chances today.");
            return;  // Dừng lại ngay lập tức nếu không còn lượt tưới
        }

        if (plantedTree != null && !plantedTree.isMature()) {
            waterCount--;
            updateWaterCountLabel();

            currentWaterLevel += 10;
            //progress bar
            int waterNeeded = 100;
            waterProgressBar.setProgress((double) currentWaterLevel / waterNeeded);

            if (currentWaterLevel >= waterNeeded) {
                currentWaterLevel = 0;
                plantedTree.grow(); // Cây phát triển thêm một cấp

                // Cập nhật hình ảnh cây sau mỗi lần phát triển
                updateTreeImage(plantedTree);
                waterProgressBar.setProgress(0);

                // Cập nhật trạng thái cây
                if (plantedTree.isMature()) {
                    ((Label) treeContainer.getChildren().getFirst()).setText("Tree mature! Ready to harvest!");
                    waterButton.setDisable(true);
                    waterProgressBar.setVisible(false);
                } else {
                    ((Label) treeContainer.getChildren().getFirst()).setText("Tree growing... Level " + plantedTree.getCurrentGrowth());
                }
            }

            //UPDATE
            com.game.DBUtils.updateTreeStatus(username, plantedTree.getCurrentGrowth(), currentWaterLevel);
            // đến khi hết lượt
            if (waterCount == 0) {
                waterButton.setDisable(true);
            }
        } else {
            AlertUtils.warningAlert("Tree is already mature or not planted.");
        }
    }

    private void updateTreeImage(Tree tree) {
        String imagePath = String.format("/com/Image/tree_level%d.png", tree.getCurrentGrowth());
        treeImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm()));
    }

    @FXML
    private void handleHarvestTree() {
        if (plantedTree != null && plantedTree.isMature()) {
            int harvestReward = plantedTree.getReward();
            coins += harvestReward;
            updateCoinsLabel();
            com.game.DBUtils.updateTreeStatus(username, 0, 0);

            treeContainer.getChildren().clear();
            plantedTree = null;
            waterButton.setDisable(false);
            plantTreeButton.setDisable(false);
            AlertUtils.infoAlert("Harvested! You earned " + harvestReward + " coins.");
        } else {
            AlertUtils.errorAlert("Tree is not ready for harvest yet.");
        }
    }
}
