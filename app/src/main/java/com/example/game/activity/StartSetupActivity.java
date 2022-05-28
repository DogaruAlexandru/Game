package com.example.game.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;

public abstract class StartSetupActivity extends AppCompatActivity {

    protected EditText mapHeightEdt;
    protected EditText mapWidthEdt;
    protected EditText crateSpawnProbabilityEdt;

    protected int mapHeight;
    protected int mapWidth;
    protected int crateSpawnProbability;

    protected Bundle bundle;
    protected Button startButton;
    protected Button backButton;

    protected void verifyInputs() {
        if (mapHeight > 31) {
            mapHeight = 31;
            Toast.makeText(StartSetupActivity.this,
                    "Using maximum height.", Toast.LENGTH_SHORT).show();
        } else if (mapHeight < 11) {
            mapHeight = 11;
            Toast.makeText(StartSetupActivity.this,
                    "Using minimum height.", Toast.LENGTH_SHORT).show();
        }

        if (mapWidth > 41) {
            mapWidth = 41;
            Toast.makeText(StartSetupActivity.this,
                    "Using maximum width.", Toast.LENGTH_SHORT).show();
        } else if (mapWidth < 11) {
            mapWidth = 11;
            Toast.makeText(StartSetupActivity.this,
                    "Using minimum width.", Toast.LENGTH_SHORT).show();
        }

        if (crateSpawnProbability > 100) {
            crateSpawnProbability = 100;
            Toast.makeText(StartSetupActivity.this,
                    "Using maximum crate spawn probability.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void getInputs() {
        try {
            String s = mapHeightEdt.getText().toString();
            if (s.equals(""))
                mapHeight = 11;
            else
                mapHeight = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(StartSetupActivity.this,
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
            Toast.makeText(StartSetupActivity.this,
                    "Invalid width. Using default.", Toast.LENGTH_SHORT).show();

            mapWidth = 11;
        }

        try {
            String s = crateSpawnProbabilityEdt.getText().toString();
            if (s.equals(""))
                crateSpawnProbability = 50;
            else
                crateSpawnProbability = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(StartSetupActivity.this,
                    "Invalid crate spawn probability. Using default.",
                    Toast.LENGTH_SHORT).show();

            crateSpawnProbability = 50;
        }

        verifyInputs();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();

        mapHeightEdt = findViewById(R.id.mapHeight);
        mapWidthEdt = findViewById(R.id.mapWidth);
        crateSpawnProbabilityEdt = findViewById(R.id.crateSpawnProbability);
        backButton = findViewById(R.id.backButton);
        startButton = findViewById(R.id.startButton);

        backButton.setOnClickListener(v -> onBackPressed());
        startButton.setOnClickListener(v -> startGame());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected abstract void startGame();
}
