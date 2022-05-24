package com.example.game;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.Enemy;
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

public class MultiplayerGame extends Game {
    private final ArrayList<Enemy> enemies;

    private final DatabaseReference reference;
    private boolean deleteServer;

    public MultiplayerGame(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context, bundle, gameplayActivity);

        int rowTile = 0;
        int columnTile = 0;
        switch (playerId) {
            case "player1":
                rowTile = 1;
                columnTile = 1;
                break;
            case "player2":
                rowTile = tilemap.getNumberOfRowTiles() - 2;
                columnTile = tilemap.getNumberOfColumnTiles() - 2;
                break;
            case "player3":
                rowTile = 1;
                columnTile = tilemap.getNumberOfColumnTiles() - 2;
                break;
            case "player4":
                rowTile = tilemap.getNumberOfRowTiles() - 2;
                columnTile = 1;
                break;
        }

        Animator animator = null;
        switch (playerId) {
            case "player1":
                animator = new Animator(spriteSheet.getBluePlayerSpriteArray());
                break;
            case "player2":
                animator = new Animator(spriteSheet.getRedPlayerSpriteArray());
                break;
            case "player3":
                animator = new Animator(spriteSheet.getGreenPlayerSpriteArray());
                break;
            case "player4":
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
                2,
                4,
                4,
                1,
                bundle);

        enemies = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(bundle.getString("code"));

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
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();

                ArrayList<String> enemiesIdList = new ArrayList<>(Arrays.
                        asList("player1", "player2", "player3", "player4"));
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
        Enemy enemy = new Enemy();
        enemy.setPlayerId(id);
        enemy.setTilemap(tilemap);
        switch (id) {
            case "player1":
                enemy.setAnimator(new Animator(spriteSheet.
                        getBluePlayerSpriteArray()));
                break;
            case "player2":
                enemy.setAnimator(new Animator(spriteSheet.
                        getRedPlayerSpriteArray()));
                break;
            case "player3":
                enemy.setAnimator(new Animator(spriteSheet.
                        getGreenPlayerSpriteArray()));
                break;
            case "player4":
                enemy.setAnimator(new Animator(spriteSheet.
                        getYellowPlayerSpriteArray()));
                break;
        }
        createListener(enemy);
        enemy.setBombList(bombList);
        enemy.setExplosionList(explosionList);
        enemies.add(enemy);
    }

    private void createListener(Enemy enemy) {
        enemy.setListener(reference.child(enemy.getPlayerId()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        enemy.setPlayerData(dataSnapshot.getValue(PlayerData.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("onCancelled", "failed", databaseError.toException());
                    }
                }));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (Enemy enemy : enemies) {
            enemy.draw(canvas);
        }
    }

    @Override
    public void update() {
        super.update();

        if (!enemies.isEmpty() && player.getLivesCount() > 0) {
            player.update();
            if (player.getLivesCount() < 1)
                playerCountChanged = true;
        }

        for (Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext(); ) {
            Enemy enemy = (Enemy) iterator.next();
            enemy.update();
            if (enemy.getPlayerData().livesCount > 0)
                continue;
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
                endgameMessage("You Won");
                playerCountChanged = false;
            }
        } else {
            switch (enemies.size()) {
                case 0:
                    endgameMessage("Tie");
                    removeListeners();
                    playerCountChanged = false;
                    break;
                case 1:
                    String color;
                    deleteServer = true;
                    switch (enemies.get(0).getPlayerId()) {
                        case "player1":
                            color = "[blue]";
                            break;
                        case "player2":
                            color = "[red]";
                            break;
                        case "player3":
                            color = "[green]";
                            break;
                        case "player4":
                            color = "[yellow]";
                            break;
                        default:
                            color = "";
                            break;
                    }
                    endgameMessage(enemies.get(0).getPlayerData().playerName + " " + color + " Won");
                    removeListeners();
                    playerCountChanged = false;
                    break;
                default:
                    playerCountChanged = false;
                    break;
            }
        }
    }

    private void endgameMessage(String msg) {
        handler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
    }

    private void removeListeners() {
        for (Enemy e : enemies) {
            reference.removeEventListener(e.getListener());
        }
    }

    public void removeServer() {
        if (deleteServer)
            reference.removeValue();
    }
}