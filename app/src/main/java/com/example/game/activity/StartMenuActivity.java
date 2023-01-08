package com.example.game.activity;

import static com.example.game.Utils.PLAYER_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;

public class StartMenuActivity extends AppCompatActivity {

    private EditText nameEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        nameEdt = findViewById(R.id.nameEdt);
        String name = PreferenceManager.getDefaultSharedPreferences(this).
                getString(PLAYER_NAME, "");
        nameEdt.setText(name);

        Button singlePlayerBtn = findViewById(R.id.singleplayerBtn);
        singlePlayerBtn.setOnClickListener(v -> openActivity(
                new Intent(this, SingleplayerStartSetupActivity.class)));

        Button multiplayerBtn = findViewById(R.id.multiplayerBtn);
        multiplayerBtn.setOnClickListener(v -> openActivity(
                new Intent(this, MultiplayerCodeSetupActivity.class)));
    }

    private void openActivity(Intent intent) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().
                putString(PLAYER_NAME, nameEdt.getText().toString()).apply();
        Bundle b = new Bundle();
        b.putString(PLAYER_NAME, nameEdt.getText().toString());
        intent.putExtras(b);
        startActivity(intent);
    }
}