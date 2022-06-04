package com.example.game.activity;

import static com.example.game.Utils.CODE;
import static com.example.game.Utils.CRATE_SPAWN_PROBABILITY;
import static com.example.game.Utils.FIREBASE_TAG;
import static com.example.game.Utils.MAP_HEIGHT;
import static com.example.game.Utils.MAP_WIDTH;
import static com.example.game.Utils.RETRIEVE_DATA_ERROR;

import android.util.Log;

import com.example.game.game.MultiplayerGame;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MultiplayerGameplayActivity extends GameplayActivity {

    private final static String GAME_PARAMS_ERROR = "Game parameters not found";

    @Override
    public void instantiateGame() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(bundle.getString(CODE));

        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(FIREBASE_TAG, RETRIEVE_DATA_ERROR, task.getException());
            } else {
                Log.d(FIREBASE_TAG, String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();
                try {
                    mapHeight = Objects.requireNonNull(dataSnapshot.child(MAP_HEIGHT).getValue(int.class));
                    mapWidth = Objects.requireNonNull(dataSnapshot.child(MAP_WIDTH).getValue(int.class));
                    crateSpawnProbability = Objects.requireNonNull(dataSnapshot.
                            child(CRATE_SPAWN_PROBABILITY).getValue(int.class));

                    bundle.putInt(MAP_HEIGHT, mapHeight);
                    bundle.putInt(MAP_WIDTH, mapWidth);
                    bundle.putInt(CRATE_SPAWN_PROBABILITY, crateSpawnProbability);

                    game = new MultiplayerGame(this, bundle, this);
                    setContentView(game);

                } catch (Exception e) {
                    Log.e(FIREBASE_TAG, GAME_PARAMS_ERROR, e);
                    mapHeight = 11;
                    mapWidth = 11;
                    crateSpawnProbability = 50;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (((MultiplayerGame) game).canLeave()) {
            super.onBackPressed();
            ((MultiplayerGame) game).removeServer();
            finish();
        }
    }
}
