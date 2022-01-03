package com.example.game.gameobject;

import static com.example.game.GameLoop.MAX_UPS;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.Utils;
import com.example.game.graphics.Animator;
import com.example.game.map.Tilemap;
import com.example.game.model.PlayerData;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Enemy {
    private final Paint INVINCIBILITY_PAINT;
    private final int INVINCIBILITY_TIME;
    private ArrayList<Bomb> bombList;
    private ArrayList<Explosion> explosionList;

    private String playerId;
    private ValueEventListener listener;
    private PlayerData playerData;
    private Animator animator;

    private Paint usedPaint;
    private Tilemap tilemap;
    private int time;
    private PlayerState.State state;
    private Rect enemyRect;

    public Enemy() {
        enemyRect = new Rect(0, 0, Utils.spriteSizeOnScreen, Utils.spriteSizeOnScreen);

        state = PlayerState.State.NOT_MOVING;

        INVINCIBILITY_TIME = (int) MAX_UPS * 2;

        INVINCIBILITY_PAINT = new Paint();
        INVINCIBILITY_PAINT.setAlpha(80);
        usedPaint = null;

        playerData = new PlayerData();
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

    public Animator getAnimator() {
        return animator;
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }

    public Paint getUsedPaint() {
        return usedPaint;
    }

    public Tilemap getTilemap() {
        return tilemap;
    }

    public void setTilemap(Tilemap tilemap) {
        this.tilemap = tilemap;
    }

    public ArrayList<Bomb> getBombList() {
        return bombList;
    }

    public void setBombList(ArrayList<Bomb> bombList) {
        this.bombList = bombList;
    }

    public ArrayList<Explosion> getExplosionList() {
        return explosionList;
    }

    public void setExplosionList(ArrayList<Explosion> explosionList) {
        this.explosionList = explosionList;
    }

    public void draw(Canvas canvas) {
        animator.draw(canvas, this, usedPaint);
    }

    public Rect getEnemyRect() {
        return enemyRect;
    }

    public PlayerState.State getState() {
        return state;
    }

    public void update() {
        enemyRect.offsetTo((int) (playerData.posX * tilemap.getMapRect().width()),
                (int) (playerData.posY * tilemap.getMapRect().height()));
        state = PlayerState.getStringToEnum(playerData.movingState);

        if (playerData.bombUsed) {
            int rowIdx = enemyRect.centerY() / enemyRect.width();
            int columnIdx = enemyRect.centerX() / enemyRect.height();//todo

            bombList.add(new Bomb(playerData.bombRange, rowIdx, columnIdx, playerId,
                    bombList, explosionList, tilemap));
        }

        handleDeath();
    }

    private void handleDeath() {
        if (time == 0) {
            if (playerData.died) {
                time = INVINCIBILITY_TIME;
                usedPaint = INVINCIBILITY_PAINT;

            }
        } else {
            --time;
            int aux = time % 4;
            switch (aux) {
                case 0:
                    usedPaint = null;
                    break;
                case 2:
                    usedPaint = INVINCIBILITY_PAINT;
                    break;
            }
        }
    }
}
