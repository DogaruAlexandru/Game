package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PlayersDisplayWithStartActivity extends AppCompatActivity {

    EditText mapHeightEdt;
    EditText mapWidthEdt;
    EditText crateSpawnProbabilityEdt;

    private ArrayList<String> list;
    private int mapHeight;
    private int mapWidth;
    private int crateSpawnProbability;

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

        mapHeightEdt = findViewById(R.id.mapHeight);
        mapWidthEdt = findViewById(R.id.mapWidth);
        crateSpawnProbabilityEdt = findViewById(R.id.crateSpawnProbability);


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


        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startGame());
    }

    private void verifyInputs() {
        if (mapHeight > 31) {
            mapHeight = 31;
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Using maximum height.", Toast.LENGTH_SHORT).show();
        } else if (mapHeight < 11) {
            mapHeight = 11;
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Using minimum height.", Toast.LENGTH_SHORT).show();
        }

        if (mapWidth > 41) {
            mapWidth = 41;
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Using maximum width.", Toast.LENGTH_SHORT).show();
        } else if (mapWidth < 11) {
            mapWidth = 11;
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Using minimum width.", Toast.LENGTH_SHORT).show();
        }

        if (crateSpawnProbability > 100) {
            crateSpawnProbability = 100;
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Using maximum crate spawn probability.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame() {
        if (list.size() < 2) {
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "You can not start with one player.", Toast.LENGTH_SHORT).show();
            return;
        }

        getInputs();

        reference.removeEventListener(childEventListener);

        reference.child("gameState").setValue("starting game");
        reference.child("mapHeight").setValue(mapHeight);
        reference.child("mapWidth").setValue(mapWidth);
        reference.child("crateSpawnProbability").setValue(crateSpawnProbability);

        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void getInputs() {
        try {
            String s = mapHeightEdt.getText().toString();
            if (s.equals(""))
                mapHeight = 11;
            else
                mapHeight = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Invalid height. Using default.", Toast.LENGTH_SHORT).show();

            mapHeight = 11;
        }

        try {
            String s = mapWidthEdt.getText().toString();
            if (s.equals(""))
                mapWidth = 11;
            else
                mapWidth = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Invalid width. Using default.", Toast.LENGTH_SHORT).show();

            mapWidth = 11;
        }

        try {
            String s = crateSpawnProbabilityEdt.getText().toString();
            if (s.equals(""))
                crateSpawnProbability = 20;
            else
                crateSpawnProbability = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast.makeText(PlayersDisplayWithStartActivity.this,
                    "Invalid crate spawn probability. Using default.",
                    Toast.LENGTH_SHORT).show();

            crateSpawnProbability = 20;
        }

        verifyInputs();
    }

    @Override
    public void onBackPressed() {
        reference.removeValue();

        reference.removeEventListener(childEventListener);

        super.onBackPressed();

        finish();
    }
}