package com.example.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;

public class StartMenuActivity extends AppCompatActivity {

    private Button multiplayerBtn;
    private Button singlePlayerBtn;
    private EditText nameEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        nameEdt = findViewById(R.id.nameEdt);

        singlePlayerBtn = findViewById(R.id.singleplayerBtn);
        singlePlayerBtn.setOnClickListener(v -> openSingleplayerActivity());

        multiplayerBtn = findViewById(R.id.multiplayerBtn);
        multiplayerBtn.setOnClickListener(v -> openMultiplayerSetupActivity());
    }

    private void openSingleplayerActivity() {
        Intent intent = new Intent(this, SingleplayerStartSetupActivity.class);
        Bundle b = new Bundle();
        b.putString("playerName", nameEdt.getText().toString());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openMultiplayerSetupActivity() {
        Intent intent = new Intent(this, MultiplayerCodeSetupActivity.class);
        Bundle b = new Bundle();
        b.putString("playerName", nameEdt.getText().toString());
        intent.putExtras(b);
        startActivity(intent);
    }
}