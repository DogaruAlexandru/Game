package com.example.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;

public class SingleplayerStartSetupActivity extends AppCompatActivity {
    private final String[] enemies = {"1 Enemy", "2 Enemies", "3 Enemies"};
    private Spinner spinner;

    private EditText mapHeightEdt;
    private EditText mapWidthEdt;
    private EditText crateSpawnProbabilityEdt;

    private int mapHeight;
    private int mapWidth;
    private int crateSpawnProbability;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_start_setup);

        bundle = getIntent().getExtras();

        spinner = findViewById(R.id.enemiesCount);
        spinner.setAdapter(new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, enemies));

        mapHeightEdt = findViewById(R.id.mapHeight);
        mapWidthEdt = findViewById(R.id.mapWidth);
        crateSpawnProbabilityEdt = findViewById(R.id.crateSpawnProbability);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startGame());
    }

    private void verifyInputs() {
        if (mapHeight > 31) {
            mapHeight = 31;
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Using maximum height.", Toast.LENGTH_SHORT).show();
        } else if (mapHeight < 11) {
            mapHeight = 11;
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Using minimum height.", Toast.LENGTH_SHORT).show();
        }

        if (mapWidth > 41) {
            mapWidth = 41;
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Using maximum width.", Toast.LENGTH_SHORT).show();
        } else if (mapWidth < 11) {
            mapWidth = 11;
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Using minimum width.", Toast.LENGTH_SHORT).show();
        }

        if (crateSpawnProbability > 100) {
            crateSpawnProbability = 100;
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Using maximum crate spawn probability.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame() {
        getInputs();

        Intent intent = new Intent(this, GameplayActivity.class);//todo singleplayer gameplay activity
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void getInputs() {
        try {
            String s = mapHeightEdt.getText().toString();
            if (s.equals(""))
                mapHeight = 11;
            else
                mapHeight = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Invalid height. Using default.", Toast.LENGTH_SHORT).show();

            mapHeight = 11;
        }

        try {
            String s = mapWidthEdt.getText().toString();
            if (s.equals(""))
                mapWidth = 11;
            else
                mapWidth = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Invalid width. Using default.", Toast.LENGTH_SHORT).show();

            mapWidth = 11;
        }

        try {
            String s = crateSpawnProbabilityEdt.getText().toString();
            if (s.equals(""))
                crateSpawnProbability = 20;
            else
                crateSpawnProbability = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(SingleplayerStartSetupActivity.this,
                    "Invalid crate spawn probability. Using default.",
                    Toast.LENGTH_SHORT).show();

            crateSpawnProbability = 20;
        }

        verifyInputs();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}