package com.example.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.game.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class MultiplayerStartSetupActivity extends StartSetupActivity {

    private ArrayList<String> list;
    private DatabaseReference reference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_multiplayer_start_setup);

        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(bundle.getString("code"));

        ListView listView = findViewById(R.id.listView);
        TextView textView = new TextView(this);
        textView.setText(R.string.PlayersListHeader);
        listView.addHeaderView(textView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, list);
        listView.setAdapter(adapter);

        childEventListener = reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                switch (Objects.requireNonNull(dataSnapshot.getKey())) {
                    case "gameState":
                    case "seed":
                        return;
                }
                list.add(dataSnapshot.child("playerName").getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                switch (Objects.requireNonNull(dataSnapshot.getKey())) {
                    case "gameState":
                    case "seed":
                        return;
                }
                list.remove(dataSnapshot.child("playerName").getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    protected void startGame() {
        if (list.size() < 2) {
            Toast.makeText(MultiplayerStartSetupActivity.this,
                    "You can not start with one player.", Toast.LENGTH_SHORT).show();
            return;
        }

        getInputs();

        reference.removeEventListener(childEventListener);

        reference.child("gameState").setValue("starting game");//todo put them in ServerData
        reference.child("mapHeight").setValue(mapHeight);
        reference.child("mapWidth").setValue(mapWidth);
        reference.child("crateSpawnProbability").setValue(crateSpawnProbability);

        Intent intent = new Intent(this, MultiplayerGameplayActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        reference.removeValue();
        reference.removeEventListener(childEventListener);

        super.onBackPressed();
    }
}