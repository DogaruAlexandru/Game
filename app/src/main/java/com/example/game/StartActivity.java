package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private Button button;
    private EditText nameEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        nameEdt = findViewById(R.id.nameEdt);

        button = findViewById(R.id.playButton);
        button.setOnClickListener(v -> openSetupActivity());
    }

    private void openSetupActivity() {
        Intent intent = new Intent(this, SetupActivity.class);
        Bundle b = new Bundle();
        b.putString("playerName", nameEdt.getText().toString());
        intent.putExtras(b);
        startActivity(intent);
    }
}