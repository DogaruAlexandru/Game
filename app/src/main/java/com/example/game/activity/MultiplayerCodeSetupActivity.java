package com.example.game.activity;

import static com.example.game.Utils.CODE;
import static com.example.game.Utils.FIREBASE_TAG;
import static com.example.game.Utils.GAME_STATE;
import static com.example.game.Utils.NIL;
import static com.example.game.Utils.PLAYER_ID;
import static com.example.game.Utils.PLAYER_NAME;
import static com.example.game.Utils.Players;
import static com.example.game.Utils.RETRIEVE_DATA_ERROR;
import static com.example.game.Utils.SEED;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;
import com.example.game.Utils;
import com.example.game.model.PlayerData;
import com.example.game.model.ServerData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.Random;

public class MultiplayerCodeSetupActivity extends AppCompatActivity {

    private final static String AUTH_TAG = "AnonymousAuth";
    private final static String SIGN_IN_SUCCESS = "Authentication sign up succeeded.";
    private final static String SIGN_IN_FAIL = "Authentication failed.";
    private final static String CODE_LENGTH_WARNING = "Code needs to have at least 4 characters.";
    private final static String CODE_NOT_FOUND_WARNING = "Code not found.";
    private final static String CODE_IN_USE_WARNING = "Code already in use.";
    private final static String LOBBY_FULL_WARNING = "All spots taken.";
    private final static String WAITING = "waiting players";
    private final static String GAME_STARTED = "Game already started.";

    private EditText codeEdt;

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_code_setup);

        auth = FirebaseAuth.getInstance();

        codeEdt = findViewById(R.id.codeEdt);

        Button joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(v -> joinGameActivity());

        Button createBtn = findViewById(R.id.createBtn);
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
                Log.d(AUTH_TAG, SIGN_IN_SUCCESS);
                user = auth.getCurrentUser();
                Toast.makeText(MultiplayerCodeSetupActivity.this, SIGN_IN_SUCCESS,
                        Toast.LENGTH_SHORT).show();
            } else {
                // If sign in fails
                Log.w(AUTH_TAG, SIGN_IN_FAIL, task.getException());
                Toast.makeText(MultiplayerCodeSetupActivity.this, SIGN_IN_FAIL,
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
        Toast.makeText(MultiplayerCodeSetupActivity.this, CODE_LENGTH_WARNING,
                Toast.LENGTH_SHORT).show();
    }

    private void joinGameActivity() {
        if (codeEdt.getText().toString().length() > 3) {
            getGameData();
            return;
        }
        Toast.makeText(MultiplayerCodeSetupActivity.this, CODE_LENGTH_WARNING,
                Toast.LENGTH_SHORT).show();
    }

    private void getGameData() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(FIREBASE_TAG, RETRIEVE_DATA_ERROR, task.getException());
            } else {
                Log.d(FIREBASE_TAG, String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();

                if (!dataSnapshot.hasChild(codeEdt.getText().toString())) {
                    Toast.makeText(MultiplayerCodeSetupActivity.this, CODE_NOT_FOUND_WARNING,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                DataSnapshot gameInstance = dataSnapshot.child(codeEdt.getText().toString());
                if (gameInstance.getChildrenCount() > 5) {
                    Toast.makeText(MultiplayerCodeSetupActivity.this, LOBBY_FULL_WARNING,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Objects.equals(gameInstance.child(GAME_STATE).getValue(String.class), WAITING)) {
                    Toast.makeText(MultiplayerCodeSetupActivity.this, GAME_STARTED,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Utils.generator = new Random((long) Objects.requireNonNull(gameInstance.child(SEED).
                        getValue()));

                PlayerData playerData = new PlayerData();
                playerData.playerName = NIL;
                if (bundle == null)
                    return;

                playerData.playerName = bundle.getString(PLAYER_NAME);

                if (!gameInstance.hasChild(Players.PLAYER1.toString())) {
                    reference.child(codeEdt.getText().toString()).child(Players.PLAYER1.toString()).
                            setValue(playerData);
                    bundle.putString(PLAYER_ID, Players.PLAYER1.toString());

                } else if (!gameInstance.hasChild(Players.PLAYER2.toString())) {
                    reference.child(codeEdt.getText().toString()).child(Players.PLAYER2.toString()).
                            setValue(playerData);
                    bundle.putString(PLAYER_ID, Players.PLAYER2.toString());

                } else if (!gameInstance.hasChild(Players.PLAYER3.toString())) {
                    reference.child(codeEdt.getText().toString()).child(Players.PLAYER3.toString()).
                            setValue(playerData);
                    bundle.putString(PLAYER_ID, Players.PLAYER3.toString());

                } else if (!gameInstance.hasChild(Players.PLAYER4.toString())) {
                    reference.child(codeEdt.getText().toString()).child(Players.PLAYER4.toString()).
                            setValue(playerData);
                    bundle.putString(PLAYER_ID, Players.PLAYER4.toString());
                }

                goToNextActivity(false);
            }
        });
    }

    private void createGameData() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(FIREBASE_TAG, RETRIEVE_DATA_ERROR, task.getException());
            } else {
                Log.d(FIREBASE_TAG, String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();

                if (dataSnapshot.hasChild(codeEdt.getText().toString())) {
                    Toast.makeText(MultiplayerCodeSetupActivity.this, CODE_IN_USE_WARNING,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Random generator = new Random();
                long seed = generator.nextLong();
                generator.setSeed(seed);
                Utils.generator = generator;

                PlayerData playerData = new PlayerData();
                playerData.playerName = NIL;
                if (bundle != null)
                    playerData.playerName = bundle.getString(PLAYER_NAME);

                if (bundle != null)
                    bundle.putString(PLAYER_ID, Players.PLAYER1.toString());

                ServerData data = new ServerData(seed, WAITING, playerData,
                        null, null, null);
                reference.child(codeEdt.getText().toString()).setValue(data);
                goToNextActivity(true);
            }
        });
    }

    private void goToNextActivity(boolean withStart) {
        Intent intent;
        if (withStart)
            intent = new Intent(this, MultiplayerStartSetupActivity.class);
        else
            intent = new Intent(this, MultiplayerStartWaitActivity.class);

        bundle.putString(CODE, codeEdt.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}