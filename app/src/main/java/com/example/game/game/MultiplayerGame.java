package com.example.game.game;

import static com.example.game.Utils.BLUE_MSG;
import static com.example.game.Utils.CODE;
import static com.example.game.Utils.FIREBASE_TAG;
import static com.example.game.Utils.GREEN_MSG;
import static com.example.game.Utils.NIL;
import static com.example.game.Utils.Players;
import static com.example.game.Utils.RED_MSG;
import static com.example.game.Utils.RETRIEVE_DATA_ERROR;
import static com.example.game.Utils.TIE_END_MSG;
import static com.example.game.Utils.WIN_END_MSG;
import static com.example.game.Utils.YELLOW_MSG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.OnlineEnemy;
import com.example.game.gameobject.OnlinePlayer;
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
    private boolean deleteServer;

    public MultiplayerGame(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context, bundle, gameplayActivity);

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

        enemies = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(bundle.getString(CODE));

        new Thread(this::addEnemiesListeners).start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        deleteServer = false;
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
        OnlineEnemy enemy = new OnlineEnemy();
        enemy.setPlayerId(id);
        enemy.setTilemap(tilemap);
        switch (Players.valueOf(id)) {
            case PLAYER1:
                enemy.setAnimator(new Animator(spriteSheet.getBluePlayerSpriteArray()));
                break;
            case PLAYER2:
                enemy.setAnimator(new Animator(spriteSheet.getRedPlayerSpriteArray()));
                break;
            case PLAYER3:
                enemy.setAnimator(new Animator(spriteSheet.getGreenPlayerSpriteArray()));
                break;
            case PLAYER4:
                enemy.setAnimator(new Animator(spriteSheet.getYellowPlayerSpriteArray()));
                break;
        }
        createListener(enemy);
        enemy.setBombList(bombList);
        enemy.setExplosionList(explosionList);
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

        if (!enemies.isEmpty() && player.getLivesCount() > 0) {
            player.update();
            if (player.getLivesCount() < 1) {
                playerCountChanged = true;
            }
        }

        for (Iterator<OnlineEnemy> iterator = enemies.iterator(); iterator.hasNext(); ) {
            OnlineEnemy enemy = iterator.next();
            enemy.update();
            if (enemy.getPlayerData().livesCount > 0) {
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

    public void handleGameEnded() {
        if (((OnlinePlayer) player).getPlayerData().livesCount > 0) {
            if (enemies.isEmpty()) {
                endgameMessage(WIN_END_MSG);
            }
        } else {
            switch (enemies.size()) {
                case 0:
                    endgameMessage(TIE_END_MSG);
                    removeListeners();
                    break;
                case 1:
                    String color;
                    deleteServer = true;
                    switch (Players.valueOf(enemies.get(0).getPlayerId())) {
                        case PLAYER1:
                            color = BLUE_MSG;
                            break;
                        case PLAYER2:
                            color = RED_MSG;
                            break;
                        case PLAYER3:
                            color = GREEN_MSG;
                            break;
                        case PLAYER4:
                            color = YELLOW_MSG;
                            break;
                        default:
                            color = NIL;
                            break;
                    }
                    endgameMessage(enemies.get(0).getPlayerData().playerName + " " + color + " Won");
                    removeListeners();
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
        if (deleteServer) {
            reference.removeValue();
        }
    }
}