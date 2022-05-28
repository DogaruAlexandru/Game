package com.example.game.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.Game;

public abstract class GameplayActivity extends AppCompatActivity {

    protected Game game;
    protected Bundle bundle;

    protected int mapHeight;
    protected int mapWidth;
    protected int crateSpawnProbability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity.java", "onCreate()");
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();

        instantiateGame();
    }

    abstract void instantiateGame();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        game.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}