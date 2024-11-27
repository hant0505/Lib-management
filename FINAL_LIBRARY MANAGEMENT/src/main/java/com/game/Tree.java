package com.game;

public class Tree {
    private static final int REWARD = 20;
    private static final int MAX_GROWTH_LEVEL = 3;

    private int currentGrowth;
    private String imagePath;
    private int waterLevel;

    public Tree(int currentGrowth, int waterLevel, String imagePath) {
        this.currentGrowth = currentGrowth;
        this.waterLevel = waterLevel;
        this.imagePath = imagePath;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public int getCurrentGrowth() {
        return currentGrowth;
    }

    public void setCurrentGrowth(int currentGrowth) {
        this.currentGrowth = currentGrowth;
    }

    public int getReward() {
        return REWARD;
    }

    public String getImagePath() {
        return String.format("/com/Image/tree_level%d.png", currentGrowth);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isMature() {
        return currentGrowth >= MAX_GROWTH_LEVEL;
    }

    public void grow() {
        if (currentGrowth < MAX_GROWTH_LEVEL) {
            currentGrowth++;

            if (currentGrowth == 2) {
                imagePath = String.format("/com/Image/tree_level%d.png", currentGrowth);
            } else if (currentGrowth == 3) {
                imagePath = String.format("/com/Image/tree_level%d.png", currentGrowth);
            }
        }
    }
}

