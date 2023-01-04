package com.example.game.gameobject.player.enemy;

import android.content.Context;

import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Explosion;
import com.example.game.gameobject.player.Player;
import com.example.game.gameobject.player.PlayerState;
import com.example.game.graphics.Animator;
import com.example.game.map.MapLayout;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;
import com.example.game.model.PlayerData;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OnlineEnemy extends Player {

    private ValueEventListener listener;
    private PlayerData playerData;

    public OnlineEnemy(Context context,
                       Tilemap tilemap,
                       Animator animator,
                       List<Bomb> bombList,
                       List<Explosion> explosionList,
                       int speedUps,
                       int bombRange,
                       int bombsNumber,
                       int livesCount) {

        super(context,
                0,
                0,
                tilemap,
                animator,
                bombList,
                explosionList,
                speedUps,
                bombRange,
                bombsNumber,
                livesCount);

        playerData = new PlayerData();
        playerData.livesCount = 1;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public ValueEventListener getListener() {
        return listener;
    }

    public void setListener(ValueEventListener listener) {
        this.listener = listener;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    public Tilemap getTilemap() {
        return tilemap;
    }

    private int getPosX() {
        return (int) (playerData.posX * tilemap.getMapRect().width());
    }

    private int getPosY() {
        return (int) (playerData.posY * tilemap.getMapRect().height());
    }

    @Override
    public void update() {
        initRectInTiles();

        playerRect.offsetTo(getPosX(), getPosY());
        playerState.setState(PlayerState.State.valueOf(playerData.movingState));
        rotationAngle = playerData.rotationData;
        livesCount = playerData.livesCount;
        bombsNumber = playerData.bombNumber;
        bombRange = playerData.bombRange;

        if (playerData.bombUsed > 0) {
            useBomb();
        }

        handleDeath();

        handlePowerUpCollision();
    }

    @Override
    protected void handlePowerUpCollision() {
        for (Tile.LayoutType layoutType : powerUpsLayoutTypes) {
            if (tileIsLayoutType(bottom, left, layoutType)) {
                tilemap.changeTile(bottom, left, MapLayout.WALK_TILE_LAYOUT_ID);

            } else if (tileIsLayoutType(bottom, right, layoutType)) {
                tilemap.changeTile(bottom, right, MapLayout.WALK_TILE_LAYOUT_ID);

            } else if (tileIsLayoutType(top, left, layoutType)) {
                tilemap.changeTile(top, left, MapLayout.WALK_TILE_LAYOUT_ID);

            } else if (tileIsLayoutType(top, right, layoutType)) {
                tilemap.changeTile(top, right, MapLayout.WALK_TILE_LAYOUT_ID);
            }
        }
    }

    @Override
    protected void handleDeath() {
        int aux = playerData.invincibilityTime % 4;
        switch (aux) {
            case 0:
                usedPaint = null;
                break;
            case 2:
                usedPaint = invincibilityPaint;
                break;
        }
    }
}
