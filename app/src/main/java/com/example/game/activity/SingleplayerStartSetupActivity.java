package com.example.game.activity;

import static com.example.game.Utils.CRATE_SPAWN_PROBABILITY;
import static com.example.game.Utils.ENEMY_COUNT;
import static com.example.game.Utils.MAP_HEIGHT;
import static com.example.game.Utils.MAP_WIDTH;
import static com.example.game.Utils.PLAYER_ID;
import static com.example.game.Utils.Players;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.game.R;

public class SingleplayerStartSetupActivity extends StartSetupActivity {

    private final static String[] enemies = {"1 Enemy", "2 Enemies", "3 Enemies"};

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

        bundle.putInt(MAP_HEIGHT, mapHeight);
        bundle.putInt(MAP_WIDTH, mapWidth);
        bundle.putInt(CRATE_SPAWN_PROBABILITY, crateSpawnProbability);
        bundle.putInt(ENEMY_COUNT, enemyCount);
        bundle.putString(PLAYER_ID, Players.PLAYER1.toString());

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