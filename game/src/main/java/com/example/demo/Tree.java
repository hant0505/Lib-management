package com.example.demo;

public class Tree {

    private int currentGrowth;
    private int reward;
    private String imagePath;
    private int waterLevel;


//    public Tree(String name, int currentGrowth, int waterLevel, String imagePath) {
//        this.name = name;
//        this.currentGrowth = currentGrowth;
//        this.waterLevel = waterLevel;
//        this.imagePath = imagePath;
//    }

    public Tree(int currentGrowth, int waterLevel, String imagePath) {
        this.currentGrowth = currentGrowth;
        this.waterLevel = waterLevel;
        this.imagePath = imagePath;
    }


    public void setCurrentGrowth(int currentGrowth) {
        this.currentGrowth = currentGrowth;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    // Getter

    public int getCurrentGrowth() {
        return currentGrowth;
    }

    public int getReward() {
        return reward;
    }

    public String getImagePath() {
        return  String.format("/com/example/demo/tree_level%d.png", currentGrowth);//
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
                reward = 20; /// Phần thưởng khi cây đạt cấp trưởng thành
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
        //reward = 5;  // Phần thưởng ban đầu khi cây mới trồng
        imagePath = "/com/example/demo/tree_level1.png"; // Ảnh cây cấp 1
    }
}
