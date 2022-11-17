package com.example.game.model;

import com.example.game.gameobject.PlayerState;
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
    public int died;
    public String playerName;
    public String movingState;

    public PlayerData() {
        livesCount = -1;
        movingState = PlayerState.State.NOT_MOVING.toString();
    }

    public PlayerData(double posX,
                      double posY,
                      int rotationData,
                      int livesCount,
                      int bombRange,
                      int bombCount,
                      boolean bombUsed,
                      int died,
                      String playerName,
                      String movingState) {

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
