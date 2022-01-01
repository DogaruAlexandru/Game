package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PlayersDisplayWithStartActivity extends AppCompatActivity {

    private Button backButton;
    private Button startButton;
    private ListView listView;
    private ArrayList<String> list;

    private Bundle bundle;
    private DatabaseReference reference;

    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_display_with_start);

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

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> goBack());

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startGame());
    }

    private void startGame() {
        if (list.size() < 2) {
            return;
        }

        reference.removeEventListener(childEventListener);

        reference.child("gameState").setValue("starting game");

        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        reference.removeValue();

        reference.removeEventListener(childEventListener);

        super.onBackPressed();

        finish();
    }
}