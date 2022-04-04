package com.example.game.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.Game;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameplayActivity extends AppCompatActivity {

    private Game game;
    private Bundle bundle;

    int mapHeight;
    int mapWidth;
    int crateSpawnProbability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity.java", "onCreate()");
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference(bundle.getString("code"));

        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();
                try {
                    mapHeight = dataSnapshot.child("mapHeight").getValue(int.class);
                    mapWidth = dataSnapshot.child("mapWidth").getValue(int.class);
                    crateSpawnProbability = dataSnapshot.child("crateSpawnProbability").
                            getValue(int.class);

                    game = new Game(this, mapHeight, mapWidth, crateSpawnProbability,
                            bundle, this);//todo
                    setContentView(game);

                } catch (Exception e) {
                    Log.e("firebase", "Game parameters not found", e);
                    mapHeight = 11;
                    mapWidth = 11;
                    crateSpawnProbability = 25;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity.java", "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity.java", "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity.java", "onPause()");
        game.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity.java", "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity.java", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (game.canLeave()) {
            super.onBackPressed();
            game.removeServer();
            finish();
        }
    }

    public void makeShortToast(String s) {
        //todo
//        Toast.makeText(GameplayActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}