package com.example.game;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PlayersDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_display);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo remove player from people for game
    }
}