package com.example.game;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;

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

public class MultiplayerGame extends Game {
    private final ArrayList<Enemy> enemies;

    private final DatabaseReference reference;

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

        addEnemiesListeners();

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
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        enemy.setPlayerData(dataSnapshot.getValue(PlayerData.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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

        if (player.getPlayerData().livesCount > 0 && !enemies.isEmpty())
            player.update();

        for (Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext(); ) {
            Enemy enemy = (Enemy) iterator.next();
            enemy.update();
            if (enemy.getPlayerData().livesCount != 0)
                continue;
            reference.removeEventListener(enemy.getListener());
            reference.child(enemy.getPlayerId()).removeValue();
            iterator.remove();
        }
    }

    public boolean canLeave() {
        return player.getLivesCount() == 0 || enemies.isEmpty();//todo bug you can leave if ypu are fast
    }

    public void handleGameEnded() {
        if (player.getPlayerData().livesCount > 0) {
            if (enemies.isEmpty()) {
                gameplayActivity.makeShortToast("You Won.");
            }
        } else {
            switch (enemies.size()) {
                case 0:
                    gameplayActivity.makeShortToast("Tie");
                    break;
                case 1:
                    String color = null;
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
                    }
                    gameplayActivity.makeShortToast(enemies.get(0).
                            getPlayerData().playerName + " " + color + " Won.");
                    reference.removeEventListener(enemies.get(0).getListener());
                    reference.child(enemies.get(0).getPlayerId()).removeValue();
                    enemies.remove(0);
                    break;
                default:
                    break;
            }
        }
    }

    public void removeServer() {
        if (enemies.size() == 0)
            reference.removeValue();
    }
}