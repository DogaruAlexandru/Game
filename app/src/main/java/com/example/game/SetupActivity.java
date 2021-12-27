package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SetupActivity extends AppCompatActivity {

    private Button buttonJoin;
    private Button buttonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        buttonJoin = (Button) findViewById(R.id.buttonJoin);
        buttonJoin.setOnClickListener(v -> joinGameActivity());

        buttonCreate = (Button) findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(v -> createGameActivity());
    }

    private void createGameActivity() {
        Intent intent = new Intent(this, GameplayActivity.class);
        startActivity(intent);
    }

    private void joinGameActivity() {
        Intent intent = new Intent(this, GameplayActivity.class);
        startActivity(intent);
    }
}