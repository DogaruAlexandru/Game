package com.example.game.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ServerData {
    public long seed;
    public PlayerData player1;
    public PlayerData player2;
    public PlayerData player3;
    public PlayerData player4;

    public ServerData() {
    }

    public ServerData(long seed, PlayerData player1, PlayerData player2,
                      PlayerData player3, PlayerData player4) {
        this.seed = seed;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
    }
}
