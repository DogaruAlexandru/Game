package com.example.game.activity;

import static com.example.game.Utils.CODE;
import static com.example.game.Utils.CRATE_SPAWN_PROBABILITY;
import static com.example.game.Utils.GAME_STATE;
import static com.example.game.Utils.MAP_HEIGHT;
import static com.example.game.Utils.MAP_WIDTH;
import static com.example.game.Utils.PLAYER_NAME;
import static com.example.game.Utils.SEED;
import static com.example.game.Utils.STARTING;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private final static String TEXT_TYPE = "text/plain";
    private final static String CHOOSER_TITLE = "Share via: ";
    private final static String START_PLAYER_COUNT_ERROR = "You can not start with one player.";

    private ArrayList<String> list;
    private DatabaseReference reference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_multiplayer_start_setup);

        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(bundle.getString(CODE));

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
                    case GAME_STATE:
                    case SEED:
                        return;
                }

                list.add(dataSnapshot.child(PLAYER_NAME).getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                switch (Objects.requireNonNull(dataSnapshot.getKey())) {
                    case GAME_STATE:
                    case SEED:
                        return;
                }

                list.remove(dataSnapshot.child(PLAYER_NAME).getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Button shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> share());
    }

    private void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, bundle.getString(CODE));
        shareIntent.setType(TEXT_TYPE);
        shareIntent = Intent.createChooser(shareIntent, CHOOSER_TITLE);
        startActivity(shareIntent);
    }

    protected void startGame() {
        if (list.size() < 2) {
            Toast.makeText(MultiplayerStartSetupActivity.this,
                    START_PLAYER_COUNT_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }

        getInputs();

        reference.removeEventListener(childEventListener);

        reference.child(GAME_STATE).setValue(STARTING);
        reference.child(MAP_HEIGHT).setValue(mapHeight);
        reference.child(MAP_WIDTH).setValue(mapWidth);
        reference.child(CRATE_SPAWN_PROBABILITY).setValue(crateSpawnProbability);

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