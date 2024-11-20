package com.example.demo;

public class Tree {

    private String name;
    private int growthTime;   // Thời gian phát triển hoàn toàn
    private int currentGrowth; // MỨC cây đã phát triển
    private int reward;      // Phần thưởng nhận được khi thu hoạch
    private String imagePath; // Đường dẫn đến hình ảnh cây

    // Constructor
    public Tree(String name, int growthTime, int reward, String imagePath) {
        this.name = name;
        this.growthTime = growthTime;
        this.reward = reward;
        this.imagePath = imagePath;
        this.currentGrowth = 1;
    }

    public Tree(int growthTime, int reward, String imagePath) {
        this.growthTime = growthTime;
        this.reward = reward;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter
    public int getGrowthTime() {
        return growthTime;
    }

    public int getCurrentGrowth() {
        return currentGrowth;
    }

    public int getReward() {
        return reward;
    }

    public String getImagePath() {
        return imagePath;
    }

    // Kiểm tra xem cây đã trưởng thành chưa
    public boolean isMature() {
        return currentGrowth >= 3;
    }

    // Cây phát triển
    public void grow() {
        if (currentGrowth < 3) {
            currentGrowth++;

            // Cập nhật phần thưởng và hình ảnh khi cây lên cấp
            if (currentGrowth == 2) {
                imagePath = "/com/example/demo/tree_level2.png"; // Cây cấp 2
            } else if (currentGrowth == 3) {
                reward = 20; // Phần thưởng khi cây đạt cấp trưởng thành
                imagePath = "/com/example/demo/tree_mature.png"; // Cây trưởng thành
            }
        }
    }

    // Thu hoạch cây
    public int harvest() {
        if (isMature()) {
            int finalReward = reward;
            resetTree();  // Reset cây về trạng thái ban đầu sau khi thu hoạch
            return finalReward;
        }
        return 0; // Nếu chưa trưởng thành, không nhận phần thưởng
    }

    // Đặt lại cây về trạng thái ban đầu sau khi thu hoạch
    private void resetTree() {
        currentGrowth = 1;
        reward = 5;  // Phần thưởng ban đầu khi cây mới trồng
        imagePath = "/com/example/demo/tree_level1.png"; // Ảnh cây cấp 1
    }
}
