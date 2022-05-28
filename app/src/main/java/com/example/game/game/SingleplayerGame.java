package com.example.game.game;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.OfflineEnemy;
import com.example.game.gameobject.OfflinePlayer;
import com.example.game.graphics.Animator;

import java.util.ArrayList;
import java.util.Iterator;

public class SingleplayerGame extends Game {

    private final ArrayList<OfflineEnemy> enemies;

    public SingleplayerGame(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context, bundle, gameplayActivity);

        player = new OfflinePlayer(
                joystick,
                button,
                1,
                1,
                tilemap,
                new Animator(spriteSheet.getBluePlayerSpriteArray()),
                bombList,
                explosionList,
                1,
                3,
                2,
                3,
                bundle);


        enemies = new ArrayList<>();

        // add red enemy
        if (bundle.getInt("enemyCount") > 0) {
            createEnemy(tilemap.getNumberOfRowTiles() - 2,
                    tilemap.getNumberOfColumnTiles() - 2,
                    new Animator(spriteSheet.getRedPlayerSpriteArray()));
            enemies.get(0).setPlayerId("Player2");
        }

        // add yellow enemy
        if (bundle.getInt("enemyCount") > 1) {
            createEnemy(1,
                    tilemap.getNumberOfColumnTiles() - 2,
                    new Animator(spriteSheet.getYellowPlayerSpriteArray()));
            enemies.get(0).setPlayerId("Player3");
        }

        // add green enemy
        if (bundle.getInt("enemyCount") > 2) {
            createEnemy(tilemap.getNumberOfRowTiles() - 2,
                    1,
                    new Animator(spriteSheet.getGreenPlayerSpriteArray()));
            enemies.get(0).setPlayerId("Player4");
        }
    }

    protected void createEnemy(int rowTile, int columnTile, Animator animator) {
        enemies.add(new OfflineEnemy(
                rowTile,
                columnTile,
                tilemap,
                animator,
                bombList,
                explosionList,
                1,
                3,
                2,
                3
        ));
    }

    @Override
    public void handleGameEnded() {
        if (((OfflinePlayer) player).getLivesCount() > 0) {
            if (enemies.isEmpty())
                endgameMessage("You Won");

        } else {
            switch (enemies.size()) {
                case 0:
                    endgameMessage("Tie");
                    break;
                case 1:
                    String color;
                    switch (enemies.get(0).getPlayerId()) {
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
                    endgameMessage(color + " Won");
                    break;
                default:
                    break;
            }
        }
        playerCountChanged = false;
    }

    @Override
    public void update() {
        super.update();

        if (!enemies.isEmpty() && player.getLivesCount() > 0) {
            player.update();
            if (player.getLivesCount() < 1)
                playerCountChanged = true;
        }

        for (Iterator<OfflineEnemy> iterator = enemies.iterator(); iterator.hasNext(); ) {
            OfflineEnemy enemy = (OfflineEnemy) iterator.next();
            enemy.update();
            if (enemy.getLivesCount() > 0)
                continue;
            playerCountChanged = true;
            iterator.remove();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (OfflineEnemy enemy : enemies) {
            enemy.draw(canvas);
        }
    }
}