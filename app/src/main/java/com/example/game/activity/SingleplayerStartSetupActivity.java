package com.example.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.game.R;

public class SingleplayerStartSetupActivity extends StartSetupActivity {

    private final String[] enemies = {"1 Enemy", "2 Enemies", "3 Enemies"};

    private int enemyCount;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_singleplayer_start_setup);

        super.onCreate(savedInstanceState);

        spinner = findViewById(R.id.enemiesCount);
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, enemies));
    }

    protected void startGame() {
        getInputs();

        bundle.putInt("mapHeight", mapHeight);
        bundle.putInt("mapWidth", mapWidth);
        bundle.putInt("crateSpawnProbability", crateSpawnProbability);
        bundle.putInt("enemyCount", enemyCount);
        bundle.putString("playerId", "player1");

        Intent intent = new Intent(this, SingleplayerGameplayActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    protected void getInputs() {
        super.getInputs();

        enemyCount = (int) spinner.getSelectedItemId() + 1;
    }
}