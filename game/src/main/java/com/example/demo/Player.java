//package com.example.demo;
//
//public class Player {
//    private String username;
//    private int coins;
//    private int waterCount;
//    private int maxWaterCount = 100;
//
//    private Tree plantedTree;
//
//    // Constructor
//    public Player(String username, int initialCoins) {
//        this.username = username;
//        this.coins = initialCoins;
//        this.waterCount = UserDAO.getWaterCount(username); // Lấy từ DB
//        this.plantedTree = UserDAO.getTreeState(username); // Lấy cây hiện tại từ DB
//    }
//
//    // Getters and Setters
//    public String getUsername() {
//        return username;
//    }
//
//    public int getCoins() {
//        return coins;
//    }
//
//    public void setCoins(int coins) {
//        this.coins = coins;
//    }
//
//    public int getWaterCount() {
//        return waterCount;
//    }
//
//    public void setWaterCount(int waterCount) {
//        this.waterCount = Math.min(waterCount, maxWaterCount); // Không vượt max
//        UserDAO.updateWaterCount(username, this.waterCount);  // Cập nhật DB
//    }
//
//    public Tree getPlantedTree() {
//        return plantedTree;
//    }
//
//    public void setPlantedTree(Tree plantedTree) {
//        this.plantedTree = plantedTree;
//        if (plantedTree != null) {
//            UserDAO.saveTreeState(username, plantedTree.getCurrentGrowth(), 0);
//        } else {
//            UserDAO.removeTreeState(username); // Nếu không có cây, xóa trạng thái cây khỏi DB
//        }
//    }
//
//    // Add or spend coins
//    public void earnCoins(int amount) {
//        this.coins += amount;
//        UserDAO.updateCoins(username, this.coins); // Cập nhật DB
//    }
//
//    public void spendCoins(int amount) {
//        if (this.coins >= amount) {
//            this.coins -= amount;
//            UserDAO.updateCoins(username, this.coins); // Cập nhật DB
//        }
//    }
//
//    // Water tree
//    public boolean waterTree() {
//        if (waterCount > 0 && plantedTree != null && !plantedTree.isMature()) {
//            waterCount--;
//            UserDAO.updateWaterCount(username, waterCount);
//
//            plantedTree.addWater(10); // Tăng nước tưới
//            UserDAO.updateTreeStatus(username, plantedTree.getCurrentGrowth(), plantedTree.getWaterLevel());
//            return true;
//        }
//        return false;
//    }
//
//    // Harvest tree
//    public int harvestTree() {
//        if (plantedTree != null && plantedTree.isMature()) {
//            int reward = plantedTree.harvest();
//            this.earnCoins(reward); // Thêm xu
//            this.setPlantedTree(null); // Xóa cây sau khi thu hoạch
//            return reward;
//        }
//        return 0;
//    }
//}
