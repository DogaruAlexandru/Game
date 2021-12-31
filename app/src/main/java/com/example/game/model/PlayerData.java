package com.example.game.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PlayerData {
    public double posX;
    public double posY;
    public int rotationData;
    public Boolean bombUsed;
    public String playerName;

    public PlayerData() {
    }

    public PlayerData(double posX, double posY, int rotationData,
                      Boolean bombUsed, String playerName) {
        this.posX = posX;
        this.posY = posY;
        this.rotationData = rotationData;
        this.bombUsed = bombUsed;
        this.playerName = playerName;
    }
}
