package com.example.game.game;

import static com.example.game.Utils.CODE;
import static com.example.game.Utils.FIREBASE_TAG;
import static com.example.game.Utils.Players;
import static com.example.game.Utils.RETRIEVE_DATA_ERROR;
import static com.example.game.Utils.TIE_END_MSG;
import static com.example.game.Utils.WIN_END_MSG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.player.OnlinePlayer;
import com.example.game.gameobject.player.enemy.OnlineEnemy;
import com.example.game.graphics.Animator;
import com.example.game.model.PlayerData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@SuppressLint("ViewConstructor")
public class MultiplayerGame extends Game {

    private final static String[] PLAYERS = {"PLAYER1", "PLAYER2", "PLAYER3", "PLAYER4"};
    private final static String CANCELLED_TAG = "onCancelled";
    private final static String FAIL = "Failed.";

    private final ArrayList<OnlineEnemy> enemies;

    private final DatabaseReference reference;

    public MultiplayerGame(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context, bundle, gameplayActivity);

        createPlayer(context, bundle);

        enemies = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(bundle.getString(CODE));

        new Thread(this::addEnemiesListeners).start();

        try {
            TimeUnit.MILLISECONDS.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createPlayer(Context context, Bundle bundle) {
        int rowTile = 0;
        int columnTile = 0;
        Animator animator = null;
        switch (Players.valueOf(playerId)) {
            case PLAYER1:
                rowTile = 1;
                columnTile = 1;
                animator = new Animator(spriteSheet.getBluePlayerSpriteArray());
                break;
            case PLAYER2:
                rowTile = tilemap.getNumberOfRowTiles() - 2;
                columnTile = tilemap.getNumberOfColumnTiles() - 2;
                animator = new Animator(spriteSheet.getRedPlayerSpriteArray());
                break;
            case PLAYER3:
                rowTile = 1;
                columnTile = tilemap.getNumberOfColumnTiles() - 2;
                animator = new Animator(spriteSheet.getGreenPlayerSpriteArray());
                break;
            case PLAYER4:
                rowTile = tilemap.getNumberOfRowTiles() - 2;
                columnTile = 1;
                animator = new Animator(spriteSheet.getYellowPlayerSpriteArray());
                break;
        }

        player = new OnlinePlayer(
                context,
                joystick,
                button,
                rowTile,
                columnTile,
                tilemap,
                animator,
                bombList,
                explosionList,
                SPEED_UPS,
                RANGE_UPS,
                BOMB_UPS,
                LIVES,
                bundle);
    }

    private void addEnemiesListeners() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(FIREBASE_TAG, RETRIEVE_DATA_ERROR, task.getException());
            } else {
                Log.d(FIREBASE_TAG, String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();

                ArrayList<String> enemiesIdList = new ArrayList<>(Arrays.asList(PLAYERS));
                enemiesIdList.remove(playerId);
                for (String id : enemiesIdList) {
                    if (dataSnapshot.child(id).exists()) {
                        createEnemy(id);
                    }
                }
            }
        });
    }

    private void createEnemy(String id) {
        Animator animator = null;
        switch (Players.valueOf(id)) {
            case PLAYER1:
                animator = new Animator(spriteSheet.getBluePlayerSpriteArray());
                break;
            case PLAYER2:
                animator = new Animator(spriteSheet.getRedPlayerSpriteArray());
                break;
            case PLAYER3:
                animator = new Animator(spriteSheet.getGreenPlayerSpriteArray());
                break;
            case PLAYER4:
                animator = new Animator(spriteSheet.getYellowPlayerSpriteArray());
                break;
        }
        OnlineEnemy enemy = new OnlineEnemy(
                context,
                tilemap,
                animator,
                bombList,
                explosionList,
                SPEED_UPS,
                RANGE_UPS,
                BOMB_UPS,
                LIVES);
        enemy.setPlayerId(id);

        createListener(enemy);
        enemies.add(enemy);
    }

    private void createListener(OnlineEnemy enemy) {
        enemy.setListener(reference.child(enemy.getPlayerId()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        enemy.setPlayerData(dataSnapshot.getValue(PlayerData.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(CANCELLED_TAG, FAIL, databaseError.toException());
                    }
                }));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (OnlineEnemy enemy : enemies) {
            enemy.draw(canvas);
        }
    }

    @Override
    public void update() {
        super.update();

        if (gameEnded) {
            return;
        }

        if (!enemies.isEmpty() && player.getLivesCount() > 0) {
            player.update();
            if (player.getLivesCount() < 1) {
                playerCountChanged = true;
            }
        }

        for (Iterator<OnlineEnemy> iterator = enemies.iterator(); iterator.hasNext(); ) {
            OnlineEnemy enemy = iterator.next();
            enemy.update();
            if (enemy.getPlayerData() != null && enemy.getPlayerData().livesCount > 0) {
                continue;
            }
            playerCountChanged = true;
            reference.removeEventListener(enemy.getListener());
            iterator.remove();
        }
    }

    public boolean canLeave() {
        return player.getLivesCount() == 0 || enemies.isEmpty();
    }

    @Override
    public void handleGameEnded() {
        if (((OnlinePlayer) player).getPlayerData().livesCount > 0) {
            if (enemies.isEmpty()) {
                gameEnded = true;
                endgameMessage(WIN_END_MSG);
                removeServer();
            }
        } else {
            switch (enemies.size()) {
                case 0:
                    gameEnded = true;
                    endgameMessage(TIE_END_MSG);
                    removeListeners();
                    removeServer();
                    break;
                case 1:
                    gameEnded = true;
                    String color = getColorString(enemies.get(0).getPlayerId());
                    endgameMessage(enemies.get(0).getPlayerData().playerName + " " + color + " Won");
                    removeListeners();
                    removeServer();
                    break;
                default:
                    break;
            }
        }
        playerCountChanged = false;
    }

    private void removeListeners() {
        for (OnlineEnemy e : enemies) {
            reference.removeEventListener(e.getListener());
        }
    }

    public void removeServer() {
        reference.removeValue();
    }
}