package com.example.game.activity;

import android.util.Log;

import com.example.game.MultiplayerGame;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MultiplayerGameplayActivity extends GameplayActivity {

    @Override
    public void instantiateGame() {
        DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference(bundle.getString("code"));

        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();
                try {
                    mapHeight = Objects.requireNonNull(dataSnapshot.child("mapHeight").
                            getValue(int.class));
                    mapWidth = Objects.requireNonNull(dataSnapshot.child("mapWidth").
                            getValue(int.class));
                    crateSpawnProbability = Objects.requireNonNull(
                            dataSnapshot.child("crateSpawnProbability").getValue(int.class));

                    bundle.putInt("mapHeight", mapHeight);
                    bundle.putInt("mapWidth", mapWidth);
                    bundle.putInt("crateSpawnProbability", crateSpawnProbability);

                    game = new MultiplayerGame(this, bundle, this);
                    setContentView(game);

                } catch (Exception e) {
                    Log.e("firebase", "Game parameters not found", e);
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
