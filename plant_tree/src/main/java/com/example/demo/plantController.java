package com.example.demo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

import static com.example.demo.UserDAO.*;

public class plantController {

    // Hiển thị số lượt tưới còn lại
    @FXML
    private Label waterCountLabel;

    // Biến lưu trữ số lượt tưới cây
    private int waterCount = 0;
    // Bạn có thể đặt số lượng tối đa lý thuyết nếu cần
    private int maxWaterCount = 100;

    @FXML
    private Button pointAttendanceButton;  // Nút điểm danh


    @FXML
    private Label coinsLabel;

    @FXML
    private VBox treeContainer;  // Thay HBox bằng VBox

    @FXML
    private Button waterButton;

    @FXML
    private Button goTo2048Button;

    @FXML
    private ProgressBar waterProgressBar;

    //private static int coins = 10;  // Số xu ban đầu
    private Player hant;
    private int currentWaterLevel = 0;
    // Lưu cây vào đây để có thể truy cập lại
    private Tree plantedTree = null;
    private int treeCost = 5;  // Chi phí để trồng cây
    private int waterNeeded = 100;
    public static final String username = "user1";



    @FXML
    private void initialize() {
        hant = new Player();
        hant.setCoins(UserDAO.getCoins(username));

        updateCoinsLabel();
///So luot tuoi dc cay
        loadWaterCount();
        // Kiểm tra nếu người chơi đã điểm danh hôm nay để thêm lượt tưới
        checkAttendanceForWatering();
    }

//kiểm tra xem người chơi đã điểm danh trong ngày hôm đó chưa.
// Điều này là cần thiết để đảm bảo người chơi chỉ nhận được một lượt tưới mỗi ngày
// và bạn không cho phép họ điểm danh nhiều lần trong cùng một ngày.
    private void checkAttendanceForWatering() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastAttendanceDate = getLastAttendanceDate(username).toLocalDate();  // Lấy ngày điểm danh cuối cùng

        if (!currentDate.isEqual(lastAttendanceDate)) {
            // Người chơi chưa điểm danh trong ngày, có thể nhận lượt tưới
            // Cộng thêm 1 lượt tưới nếu chưa điểm danh
            waterCount = Math.min(waterCount + 1, maxWaterCount);

            // Cập nhật số lượt tưới vào cơ sở dữ liệu
            updateWaterCount(username, waterCount);
            updateLastAttendanceDate(username, Date.valueOf(currentDate));  // Cập nhật ngày điểm danh
            updateWaterCountLabel();

            // Kích hoạt nút "Điểm danh"
            pointAttendanceButton.setDisable(false);
        } else {
            pointAttendanceButton.setDisable(true);
        }
    }

    private void loadWaterCount() {
        waterCount = getWaterCount(username);  // Lấy số lượt tưới từ cơ sở dữ liệu
        updateWaterCountLabel();  // Cập nhật lại label để hiển thị
    }

    private void updateWaterCountLabel() {
        waterCountLabel.setText("Watered: " + waterCount );
        updateWaterCount(username, waterCount);  // Cập nhật số lượt tưới vào cơ sở dữ liệu

    }

    @FXML
    // Hàm này sẽ gọi khi người chơi nhấn nút "Điểm danh"    @FXML
    private void handlePointAttendance() {
        // Tăng số lượt tưới khi điểm danh
            waterCount++;
            updateWaterCountLabel();

            System.out.println("Attendance rewarded! You got +1 watering chance.");
            // Sau khi điểm danh, vô hiệu hóa nút để tránh người chơi điểm danh nhiều lần trong ngày
            pointAttendanceButton.setDisable(true);
    }


    // Cập nhật label số xu
    private void updateCoinsLabel() {
        coinsLabel.setText("Coins: " + hant.getCoins());
    }

    // Xử lý sự kiện khi nhấn nút "Plant a Tree"
    @FXML
    private void handlePlantTree() {
        int coins = hant.getCoins();
        if (coins >= treeCost) {
            //coins -= treeCost;
            hant.spendCoins(treeCost);
            updateCoinsLabel();

            // Tạo cây mới với phần thưởng và thời gian phát triển khác nhau
            Tree tree = new Tree("Tree", 3, 10, "/com/example/demo/tree_level1.png");
            Label treeLabel = new Label("Tree growing...");

            Image image = new Image(getClass().getResource("/com/example/demo/tree_level1.png").toExternalForm());
            ImageView treeView = new ImageView(image);
            waterButton.setDisable(false);

            plantedTree = tree;

//            /// Cập nhật trạng thái cây vào cơ sở dữ liệu
//            saveTreeState(username, 1, currentWaterLevel);  // Trạng thái cây (1 - đang trồng), mức độ nước = 0
//            updateTreeImage(plantedTree, treeView);

            // Đặt kích thước ImageView (tuỳ ý)
            treeView.setFitWidth(100);
            treeView.setFitHeight(100);

            treeContainer.getChildren().clear();
            treeContainer.getChildren().addAll(treeLabel, treeView, waterProgressBar);
            waterButton.setVisible(true);
            waterProgressBar.setVisible(true);
            waterButton.setDisable(false);  // Cho phép người chơi tưới cây
        } else {
            System.out.println("Not enough coins!");
        }
    }

    // Xử lý sự kiện khi nhấn nút "Water Tree"
    //Tưới cây
    @FXML
    private void handleWaterTree() {

        if (waterCount == 0) {
            // Nếu không còn lượt tưới, in ra thông báo và không thực hiện các bước tưới cây
            System.out.println("No more watering chances today.");
            return;  // Dừng hàm lại ngay lập tức nếu không còn lượt tưới
        }

        if (plantedTree != null && !plantedTree.isMature()) {
            /// Giảm số lượt tưới khi người chơi tưới cây
            waterCount--;
            ///LABEL
            updateWaterCountLabel();  // Cập nhật lại label hiển thị số lượt tưới
            /// DATA
            updateWaterCount(username,waterCount);
            currentWaterLevel += 10;
            waterProgressBar.setProgress((double) currentWaterLevel / waterNeeded);

            if (currentWaterLevel >= waterNeeded) {
                currentWaterLevel = 0;
                plantedTree.grow(); // Cây phát triển thêm một cấp

                // Cập nhật hình ảnh cây sau mỗi lần phát triển
                updateTreeImage(plantedTree, (ImageView) treeContainer.getChildren().get(1));
                waterProgressBar.setProgress(0);

                // Cập nhật trạng thái cây
                if (plantedTree.isMature()) {
                    ((Label) treeContainer.getChildren().get(0)).setText("Tree mature! Ready to harvest!");
                    waterButton.setDisable(true);  // Tắt nút tưới khi cây trưởng thành
                } else {
                    ((Label) treeContainer.getChildren().get(0)).setText("Tree growing... Level " + plantedTree.getCurrentGrowth());
                }
            }

            ///Nếu ko còn lượt tưới , vô hiệu hóa nút "Water
            if (waterCount == 0) {
                waterButton.setDisable(true); // hết lượt thì cook
            }
        } else {
            System.out.println("Tree is already mature or not planted.");
        }
    }

    private void updateTreeImage(Tree tree, ImageView treeView) {
        if (tree.getCurrentGrowth() == 2) {
            treeView.setImage(new Image(getClass().getResource("/com/example/demo/tree_level2.png").toExternalForm()));
        } else if (tree.isMature()) {
            treeView.setImage(new Image(getClass().getResource("/com/example/demo/tree_mature.png").toExternalForm()));
        }
    }

    // Xử lý sự kiện khi nhấn nút "Harvest Tree"
    @FXML
    private void handleHarvestTree() {
        //int coins = hant.getCoins();

        if (plantedTree != null && plantedTree.isMature()) {
            int harvestReward = plantedTree.harvest();
            hant.earnCoins(harvestReward);
            updateCoinsLabel();
            UserDAO.updateCoins(username,hant.getCoins());
            ///NHẬN XU NÈ

            treeContainer.getChildren().clear();// Xóa cây khỏi giao diện
            plantedTree = null;
            waterButton.setDisable(true);

            System.out.println("Harvested! You earned " + harvestReward + " coins.");
        } else {
            System.out.println("Tree is not ready for harvest yet.");
        }
    }

    // Phương thức chuyển sang scene 2048
    @FXML
    private void handleGoTo2048() {
        try {
            // Tải FXML của game 2048
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GAME2048.fxml"));
            AnchorPane game2048Layout = loader.load();

            // Tạo Scene cho game 2048
            Scene game2048Scene = new Scene(game2048Layout, 400, 450);

            // Lấy Stage hiện tại và đổi scene
            Stage stage = (Stage) goTo2048Button.getScene().getWindow();
            stage.setScene(game2048Scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
