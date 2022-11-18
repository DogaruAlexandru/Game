package com.example.game.activity;

import static com.example.game.Utils.NIL;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;

public abstract class StartSetupActivity extends AppCompatActivity {

    public final static String MAX_HEIGHT = "Using maximum height.";
    public final static String MIN_HEIGHT = "Using minimum height.";
    public final static String MAX_WIDTH = "Using maximum width.";
    public final static String MIN_WIDTH = "Using minimum width.";
    public final static String MAX_SPAWN = "Using maximum crate spawn probability.";
    public final static String INVALID_HEIGHT = "Invalid height. Using default.";
    public final static String INVALID_WIDTH = "Invalid width. Using default.";
    public final static String INVALID_SPAWN = "Invalid crate spawn probability. Using default.";

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
            makeToast(MAX_HEIGHT);
        } else if (mapHeight < 11) {
            mapHeight = 11;
            makeToast(MIN_HEIGHT);
        }

        if (mapWidth > 41) {
            mapWidth = 41;
            makeToast(MAX_WIDTH);
        } else if (mapWidth < 11) {
            mapWidth = 11;
            makeToast(MIN_WIDTH);
        }

        if (crateSpawnProbability > 100) {
            crateSpawnProbability = 100;
            makeToast(MAX_SPAWN);
        }
    }

    private void makeToast(String str) {
        Toast.makeText(StartSetupActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    protected void getInputs() {
        try {
            String s = mapHeightEdt.getText().toString();
            if (s.equals(NIL)) {
                mapHeight = 11;
            } else {
                mapHeight = Integer.parseInt(s);
            }

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            makeToast(INVALID_HEIGHT);

            mapHeight = 11;
        }

        try {
            String s = mapWidthEdt.getText().toString();
            if (s.equals(NIL)) {
                mapWidth = 11;
            } else {
                mapWidth = Integer.parseInt(s);
            }

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            makeToast(INVALID_WIDTH);

            mapWidth = 11;
        }

        try {
            String s = crateSpawnProbabilityEdt.getText().toString();
            if (s.equals(NIL)) {
                crateSpawnProbability = 50;
            } else {
                crateSpawnProbability = Integer.parseInt(s);
            }

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            makeToast(INVALID_SPAWN);

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
