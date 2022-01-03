package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.model.PlayerData;
import com.example.game.model.ServerData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class SetupActivity extends AppCompatActivity {

    private Button joinBtn;
    private Button createBtn;
    private ProgressBar loadingPB;
    private EditText codeEdt;
    private TextView titleTV;

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        auth = FirebaseAuth.getInstance();

        loadingPB = findViewById(R.id.loadingPB);
        codeEdt = findViewById(R.id.codeEdt);
        titleTV = findViewById(R.id.titleTV);

        joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(v -> joinGameActivity());

        createBtn = findViewById(R.id.createBtn);
        createBtn.setOnClickListener(v -> createGameActivity());

        bundle = getIntent().getExtras();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = auth.getCurrentUser();
        if (user == null) {
            signInAnonymously();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void signInAnonymously() {
        auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success
                Log.d("AnonymousAuth", "signInAnonymously:success");
                user = auth.getCurrentUser();
                Toast.makeText(SetupActivity.this, "Authentication sign up succeeded.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // If sign in fails
                Log.w("AnonymousAuth", "signInAnonymously:failure", task.getException());
                Toast.makeText(SetupActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        });
    }

    private void createGameActivity() {
        if (codeEdt.getText().toString().length() > 3) {
            createGameData();
            return;
        }
        Toast.makeText(SetupActivity.this, "Code needs to have at least 4 characters.",
                Toast.LENGTH_SHORT).show();
    }

    private void joinGameActivity() {
        if (codeEdt.getText().toString().length() > 3) {
            getGameData();
            return;
        }
        Toast.makeText(SetupActivity.this, "Code needs to have at least 4 characters.",
                Toast.LENGTH_SHORT).show();
    }

    private void getGameData() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();


                if (!dataSnapshot.hasChild(codeEdt.getText().toString())) {
                    Toast.makeText(SetupActivity.this, "Code not found.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                DataSnapshot gameInstance = dataSnapshot.child(codeEdt.getText().toString());
                if (gameInstance.getChildrenCount() > 5) {
                    Toast.makeText(SetupActivity.this, "All spots taken.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!gameInstance.child("gameState").getValue(String.class).
                        equals("waiting players")) {
                    Toast.makeText(SetupActivity.this, "Game already started.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Utils.generator = new Random((long) gameInstance.child("seed").getValue());

                PlayerData playerData = new PlayerData();
                playerData.playerName = "";
                if (bundle == null)
                    return;

                playerData.playerName = bundle.getString("playerName");

                if (!gameInstance.hasChild("player1")) {
                    reference.child(codeEdt.getText().toString()).
                            child("player1").setValue(playerData);
                    bundle.putString("playerId", "player1");

                } else if (!gameInstance.hasChild("player2")) {
                    reference.child(codeEdt.getText().toString()).
                            child("player2").setValue(playerData);
                    bundle.putString("playerId", "player2");

                } else if (!gameInstance.hasChild("player3")) {
                    reference.child(codeEdt.getText().toString()).
                            child("player3").setValue(playerData);
                    ;
                    bundle.putString("playerId", "player3");

                } else if (!gameInstance.hasChild("player4")) {
                    reference.child(codeEdt.getText().toString()).
                            child("player4").setValue(playerData);
                    bundle.putString("playerId", "player4");
                }

                goToNextActivity(false);
            }
        });
    }

    private void createGameData() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();

                if (dataSnapshot.hasChild(codeEdt.getText().toString())) {
                    Toast.makeText(SetupActivity.this, "Code already in use.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Random generator = new Random();
                long seed = generator.nextLong();
                generator.setSeed(seed);
                Utils.generator = generator;

                PlayerData playerData = new PlayerData();
                playerData.playerName = "";
                if (bundle != null)
                    playerData.playerName = bundle.getString("playerName");

                if (bundle != null)
                    bundle.putString("playerId", "player1");

                ServerData data = new ServerData(seed, "waiting players",
                        playerData, null, null, null);
                reference.child(codeEdt.getText().toString()).setValue(data);
                goToNextActivity(true);
            }
        });
    }

    private void goToNextActivity(boolean withStart) {
        Intent intent;
        if (withStart)
            intent = new Intent(this, PlayersDisplayWithStartActivity.class);
        else
            intent = new Intent(this, PlayersDisplayActivity.class);
        bundle.putString("code", codeEdt.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
//        finish();
    }
}