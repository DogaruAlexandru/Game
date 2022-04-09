package com.example.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MultiplayerStartWaitActivity extends AppCompatActivity {

    private Button backButton;
    private ListView listView;
    private ArrayList<String> list;

    private Bundle bundle;
    private DatabaseReference reference;

    private ValueEventListener gameInstanceValueEventListener;
    private ChildEventListener childEventListener;
    private ValueEventListener gameStateValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_start_wait);

        list = new ArrayList<>();
        bundle = getIntent().getExtras();
        reference = FirebaseDatabase.getInstance().getReference(bundle.getString("code"));

        listView = findViewById(R.id.listView);
        TextView textView = new TextView(this);
        textView.setText(R.string.PlayersListHeader);
        listView.addHeaderView(textView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, list);
        listView.setAdapter(adapter);

        // Listener for game deleted
        gameInstanceValueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    return;
                }
                Toast.makeText(MultiplayerStartWaitActivity.this,
                        "The creator closed the server.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Listener for players in list
        childEventListener = reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
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
                switch (dataSnapshot.getKey()) {
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

        // Listener for gameState changing
        gameStateValueEventListener = reference.child("gameState").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!"starting game".equals(dataSnapshot.getValue(String.class))) {
                            return;
                        }
                        startGameplayActivity();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        reference.child(bundle.getString("playerId")).removeValue();

        reference.removeEventListener(gameInstanceValueEventListener);
        reference.removeEventListener(childEventListener);
        reference.child("gameState").removeEventListener(gameStateValueEventListener);

        super.onBackPressed();

        finish();
    }

    private void startGameplayActivity() {
        reference.removeEventListener(gameInstanceValueEventListener);
        reference.removeEventListener(childEventListener);
        reference.child("gameState").removeEventListener(gameStateValueEventListener);

        Intent intent = new Intent(this, MultiplayerGameplayActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}