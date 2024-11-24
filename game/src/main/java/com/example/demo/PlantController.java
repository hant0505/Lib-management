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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

import static com.example.demo.UserDAO.*;

public class PlantController {

    @FXML
    private Button plantTreeButton;

    /// Nút điểm danh
    @FXML
    private Button pointAttendanceButton;

    @FXML
    private Label coinsLabel;

    @FXML
    private VBox treeContainer;

    /// WATER
    @FXML
    private Button waterButton;

    @FXML
    private ProgressBar waterProgressBar;

    // Hiển thị số lượt tưới còn lại
    @FXML
    private Label waterCountLabel;
    // Biến lưu trữ số lượt tưới cây
    private int waterCount = 0;
    // Bạn có thể đặt số lượng tối đa lý thuyết nếu cần
    private int maxWaterCount = 100;

    /// WATERLEVEL
    private int currentWaterLevel = 0;
    /// Lưu cây vào đây để có thể truy cập lại
    private Tree plantedTree = null;

    private int treeCost = 5;  // Chi phí để trồng cây

    /// ->PROGRESSBAR MAX 100
    private int waterNeeded = 100;

    public static final String username = "user1";

    //private Player hant;

    @FXML
    private Button goTo2048Button;

    @FXML
    private void initialize() {
        // Tải số xu từ cơ sở dữ liệu
        loadCoins();

        // /Tải trạng thái cây từ cơ sở dữ liệu
        plantedTree = getTreeState(username);
        currentWaterLevel = plantedTree.getWaterLevel();

//        if(plantedTree.isMature()){
//            plantedTree = null;
//        }
        if(plantedTree.getCurrentGrowth()==0) {
            plantedTree = null ;
        }

        // Nếu đã trồng cây trước đó
        if (plantedTree != null) {
            // Hiển thị cây lên giao diện
            updateTreeContainer();


            /// Vô hiệu hóa nút "Plant a Tree" nếu cây chưa trưởng thành
            if (!plantedTree.isMature()) {
                plantTreeButton.setDisable(true);
            }
        }
        ///So luot tuoi dc cay
        loadWaterCount();

        /// Kiểm tra nếu người chơi đã điểm danh hôm nay để thêm lượt tưới
        checkAttendanceForWatering();
    }

    private void loadCoins() {
        int coins = UserDAO.getCoins(username);  // Lấy số xu từ cơ sở dữ liệu thông qua UserDAO
        updateCoinsLabel(coins);  // Gọi phương thức cập nhật label với số xu lấy được
    }


    /// Cập nhật label số xu
    private void updateCoinsLabel(int coins) {
        // Hiển thị số xu lên label
        coinsLabel.setText("Coins: " + coins);

        // Cập nhật số xu vào cơ sở dữ liệu nếu cần
        UserDAO.updateCoins(username, coins);
    }


    private void loadWaterCount() {
        waterCount = getWaterCount(username);  /// Lấy số lượt tưới từ cơ sở dữ liệu
        updateWaterCountLabel();  // Cập nhật lại label để hiển thị
    }

    private void updateWaterCountLabel() {
        waterCountLabel.setText("Watered: " + waterCount );
        updateWaterCount(username, waterCount);  /// Cập nhật số lượt tưới vào cơ sở dữ liệu
    }

    private void updateTreeContainer() {
        if (plantedTree != null) {
            // Tạo Label cho trạng thái cây
            String treeStatus = plantedTree.isMature()
                    ? "Tree mature! Ready to harvest!"
                    : "Tree growing... Level " + plantedTree.getCurrentGrowth();

            Label treeLabel = new Label(treeStatus);

            // Tạo ImageView và đặt hình ảnh cây theo cấp độ hiện tại
            Image treeImage;
            if (plantedTree.getCurrentGrowth() == 1) {
                treeImage = new Image(getClass().getResource("/com/example/demo/tree_level1.png").toExternalForm());
            } else if (plantedTree.getCurrentGrowth() == 2) {
                treeImage = new Image(getClass().getResource("/com/example/demo/tree_level2.png").toExternalForm());
            } else {
                treeImage = new Image(getClass().getResource("/com/example/demo/tree_level3.png").toExternalForm());
            }

            ImageView treeView = new ImageView(treeImage);
            treeView.setFitWidth(100);
            treeView.setFitHeight(100);

            /// Cập nhật ProgressBar (luôn hiển thị, nhưng giá trị = 0 khi cây trưởng thành)
            if (plantedTree.isMature()) {
                waterProgressBar.setProgress(0);
                waterProgressBar.setVisible(true);  // Tiếp tục hiển thị ProgressBar dù cây đã trưởng thành
            } else {
                waterProgressBar.setProgress((double) currentWaterLevel / waterNeeded);
            }

            // Thêm các thành phần vào giao diện
            treeContainer.getChildren().clear();
            treeContainer.getChildren().addAll(treeLabel, treeView, waterProgressBar);

            // Kiểm tra nếu cây trưởng thành để tắt nút tưới nước
            waterButton.setDisable(plantedTree.isMature());
            waterButton.setVisible(!plantedTree.isMature());
            waterProgressBar.setVisible(!plantedTree.isMature());
        }
    }

    ///kiểm tra xem người chơi đã điểm danh trong ngày hôm đó chưa.
    // Điều này là cần thiết để đảm bảo người chơi chỉ nhận được một lượt tưới mỗi ngày
    // và bạn không cho phép họ điểm danh nhiều lần trong cùng một ngày.
    private void checkAttendanceForWatering() {
        LocalDate currentDate = LocalDate.now();
        Date lastAttendanceSQLDate = getLastAttendanceDate(username); // Lấy ngày điểm danh cuối cùng
        LocalDate lastAttendanceDate = null;

        // Kiểm tra nếu chưa từng điểm danh
        if (lastAttendanceSQLDate != null) {
            lastAttendanceDate = lastAttendanceSQLDate.toLocalDate();
        }

        // Kích hoạt hoặc vô hiệu hóa nút "Điểm danh"
        if (lastAttendanceDate == null || !currentDate.isEqual(lastAttendanceDate)) {
            // Chưa điểm danh hôm nay
            pointAttendanceButton.setDisable(false);
        } else {
            // Đã điểm danh hôm nay
            pointAttendanceButton.setDisable(true);
        }
    }


    /// Hàm này sẽ gọi khi người chơi nhấn nút "Điểm danh"
    @FXML
    private void handlePointAttendance() {
        LocalDate currentDate = LocalDate.now();

        // Tăng số lượt tưới
        waterCount = Math.min(waterCount + 1, maxWaterCount);

        // Cập nhật cơ sở dữ liệu
        updateWaterCount(username, waterCount); /// updatewwaterCOuntLabel
        updateLastAttendanceDate(username, Date.valueOf(currentDate));

        // Cập nhật giao diện
        updateWaterCountLabel();

        // Vô hiệu hóa nút "Điểm danh" sau khi nhấn
        pointAttendanceButton.setDisable(true);

        System.out.println("Attendance rewarded! You got +1 watering chance.");
    }

    ///
    public boolean isGrowable() {
        return !plantedTree.isMature() && plantedTree!=null;
    }

    // Xử lý sự kiện khi nhấn nút "Plant a Tree"
    @FXML
    private void handlePlantTree() {
        if (plantedTree != null && !plantedTree.isMature()) {
            System.out.println("A tree is already planted and growing. Wait until it matures.");
            return;  // Không cho phép trồng cây mới khi cây cũ chưa trưởng thành
        }

        int coins = getCoins(username);
        if (coins >= treeCost) {
            coins -= treeCost;
            updateCoinsLabel(coins);

            // Tạo cây mới với phần thưởng và thời gian phát triển khác nhau
            Tree tree = new Tree("Tree", 1, 0, "/com/example/demo/tree_level1.png");
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

            // Vô hiệu hóa nút "Plant a Tree"
            plantTreeButton.setDisable(true);
        } else {
            System.out.println("Not enough coins!");
        }
    }

    // Xử lý sự kiện khi nhấn nút "Water Tree"
    ///Tưới cây
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
            /// DATA - Ở  updateWaterCountLabel đã đưa lên r
            //updateWaterCount(username,waterCount);
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
                    waterProgressBar.setVisible(false);
                } else {
                    ((Label) treeContainer.getChildren().get(0)).setText("Tree growing... Level " + plantedTree.getCurrentGrowth());
                }
            }

            ///UPDATE
            updateTreeStatus(username,plantedTree.getCurrentGrowth(),currentWaterLevel);

            ///Nếu ko còn lượt tưới , vô hiệu hóa nút "Water
            if (waterCount == 0) {
                waterButton.setDisable(true); // hết lượt thì cook
            }
        } else {
            System.out.println("Tree is already mature or not planted.");
        }
    }

    private void updateTreeImage(Tree tree, ImageView treeView) {
        String imagePath = String.format("/com/example/demo/tree_level%d.png", tree.getCurrentGrowth());
        treeView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
    }

    // Xử lý sự kiện khi nhấn nút "Harvest Tree"
    @FXML
    private void handleHarvestTree() {
        int coins = getCoins(username);

        if (plantedTree != null && plantedTree.isMature()) {
            int harvestReward = plantedTree.harvest();
            //System.out.println("Nhaajn thuong hong: " + harvestReward+ "level :" + plantedTree.getCurrentGrowth());
            coins += harvestReward;
            /// Nhận xuuuuuu
            updateCoinsLabel(coins);/// lên cơ sở dữ liệu
            //updateCoins(username,coins);
            ///NHẬN XU NÈ

            updateTreeStatus(username, 0, 0); // Level 1, Water Level 0

            ///CÂNF KO>?
            treeContainer.getChildren().clear();// Xóa cây khỏi giao diện
            plantedTree = null;
            //updateTreeStatus(username,0,0);
            waterButton.setDisable(true);
            // Kích hoạt nút "Plant a Tree"
            plantTreeButton.setDisable(false);

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
            Scene game2048Scene = new Scene(game2048Layout, 700, 700);

            // Lấy Stage hiện tại và đổi scene
            Stage stage = (Stage) goTo2048Button.getScene().getWindow();
            stage.setScene(game2048Scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
