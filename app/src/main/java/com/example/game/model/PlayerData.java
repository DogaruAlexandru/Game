package com.example.game.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PlayerData {
    public double posX;
    public double posY;
    public int rotationData;
    public int livesCount;
    public int bombRange;
    public int bombCount;
    public boolean bombUsed;
    public boolean died;
    public String playerName;
    public String movingState;

    public PlayerData() {
        livesCount = -1;
    }

    public PlayerData(double posX, double posY, int rotationData, int livesCount, int bombRange,
                      int bombCount, boolean bombUsed, boolean died, String playerName, String movingState) {
        this.posX = posX;
        this.posY = posY;
        this.rotationData = rotationData;
        this.livesCount = livesCount;
        this.bombRange = bombRange;
        this.bombCount = bombCount;
        this.bombUsed = bombUsed;
        this.died = died;
        this.playerName = playerName;
        this.movingState = movingState;
    }
}
