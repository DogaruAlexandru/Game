package com.example.game.game;

import static com.example.game.Utils.ENEMY_COUNT;
import static com.example.game.Utils.Players;
import static com.example.game.Utils.TIE_END_MSG;
import static com.example.game.Utils.WIN_END_MSG;
import static com.example.game.Utils.getPlayerColumn;
import static com.example.game.Utils.getPlayerRow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.player.OfflinePlayer;
import com.example.game.gameobject.player.Player;
import com.example.game.gameobject.player.enemy.OfflineEnemy;
import com.example.game.graphics.Animator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@SuppressLint("ViewConstructor")
public class SingleplayerGame extends Game {

    private final static int PLAYER_START_ROW = 1;
    private final static int PLAYER_START_COLUMN = 1;

    private final Map<String, OfflineEnemy> enemies;
    private final Map<String, Pair<Integer, Integer>> enemiesPos;

    public SingleplayerGame(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context, bundle, gameplayActivity);

        player = new OfflinePlayer(
                context,
                joystick,
                button,
                PLAYER_START_ROW,
                PLAYER_START_COLUMN,
                tilemap,
                new Animator(spriteSheet.getBluePlayerSpriteArray()),
                bombList,
                explosionList,
                SPEED_UPS,
                RANGE_UPS,
                BOMB_UPS,
                LIVES,
                bundle);

        enemies = new HashMap<>();
        enemiesPos = new HashMap<>();

        addEnemies(bundle);
        addEnemiesPos();
    }

    private void addEnemies(Bundle bundle) {
        int count = bundle.getInt(ENEMY_COUNT);
        // add player2
        if (count > 0) {
            createEnemy(tilemap.getNumberOfRowTiles() - 2,
                    tilemap.getNumberOfColumnTiles() - 2,
                    new Animator(spriteSheet.getRedPlayerSpriteArray()),
                    Players.PLAYER2.toString());
        }

        // add player3
        if (count > 1) {
            createEnemy(PLAYER_START_ROW,
                    tilemap.getNumberOfColumnTiles() - 2,
                    new Animator(spriteSheet.getYellowPlayerSpriteArray()),
                    Players.PLAYER3.toString());
        }

        // add player4
        if (count > 2) {
            createEnemy(tilemap.getNumberOfRowTiles() - 2,
                    PLAYER_START_COLUMN,
                    new Animator(spriteSheet.getGreenPlayerSpriteArray()),
                    Players.PLAYER4.toString());
        }
    }

    private void addEnemiesPos() {
        enemiesPos.put(Players.PLAYER1.toString(), getPLayerPos(player));
        for (Map.Entry<String, OfflineEnemy> enemy : enemies.entrySet()) {
            enemiesPos.put(enemy.getKey(), getPLayerPos(enemy.getValue()));
        }
    }

    @NonNull
    private Pair<Integer, Integer> getPLayerPos(Player p) {
        return new Pair<>(getPlayerRow(p), getPlayerColumn(p));
    }

    protected void createEnemy(int rowTile, int columnTile, Animator animator, String key) {
        enemies.put(key, new OfflineEnemy(
                context,
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
                enemiesPos
        ));
        Objects.requireNonNull(enemies.get(key)).setPlayerId(key);
    }

    @Override
    public void handleGameEnded() {
        if (player.getLivesCount() > 0) {
            if (enemies.isEmpty()) {
                gameEnded = true;
                endgameMessage(WIN_END_MSG);
            }
        } else {
            switch (enemies.size()) {
                case 0:
                    gameEnded = true;
                    endgameMessage(TIE_END_MSG);
                    break;
                case 1:
                    gameEnded = true;
                    String enemyId = enemies.entrySet().iterator().next().getKey();
                    String color = getColorString(enemyId);
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

        if (gameEnded) {
            return;
        }

        if (!enemies.isEmpty() && player.getLivesCount() > 0) {
            player.update();
            enemiesPos.put(player.getPlayerId(), getPLayerPos(player));

            if (player.getLivesCount() < 1) {
                playerCountChanged = true;
                enemiesPos.remove(player.getPlayerId());
            }
        }

        Iterator<Map.Entry<String, OfflineEnemy>> iterator = enemies.entrySet().iterator();
        while (iterator.hasNext()) {
            OfflineEnemy enemy = iterator.next().getValue();
            enemy.update();
            enemiesPos.put(enemy.getPlayerId(), getPLayerPos(enemy));
            if (enemy.getLivesCount() > 0) {
                continue;
            }
            //remove enemy
            playerCountChanged = true;
            enemiesPos.remove(enemy.getPlayerId());
            iterator.remove();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (OfflineEnemy enemy : enemies.values()) {
            enemy.draw(canvas);
        }
    }
}