package com.example.demo;

public class Player {
    private int coins;

    public Player() {
        this.coins = 0;
    }

    public int getCoins() {
        return coins;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }


    public void earnCoins(int amount) {
        coins += amount;
    }

    public void spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
        }
    }

}
