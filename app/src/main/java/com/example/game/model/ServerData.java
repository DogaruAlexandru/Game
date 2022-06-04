package com.example.game.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ServerData {
    public Long seed;
    public String gameState;
    public PlayerData PLAYER1;
    public PlayerData PLAYER2;
    public PlayerData PLAYER3;
    public PlayerData PLAYER4;

    public ServerData() {
    }

    public ServerData(Long seed, String gameState, PlayerData player1, PlayerData player2,
                      PlayerData player3, PlayerData player4) {
        this.seed = seed;
        this.gameState = gameState;
        this.PLAYER1 = player1;
        this.PLAYER2 = player2;
        this.PLAYER3 = player3;
        this.PLAYER4 = player4;
    }
}
