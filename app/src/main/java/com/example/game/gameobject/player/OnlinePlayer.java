package com.example.game.gameobject.player;

import static com.example.game.Utils.CODE;
import static com.example.game.Utils.PLAYER_NAME;

import android.content.Context;

import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Explosion;
import com.example.game.gamepanel.Button;
import com.example.game.gamepanel.Joystick;
import com.example.game.graphics.Animator;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;
import com.example.game.model.PlayerData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OnlinePlayer extends OfflinePlayer {

    private final DatabaseReference reference;

    protected final PlayerData playerData;

    private int timeUsingBomb;

    public OnlinePlayer(Context context,
                        Joystick joystick,
                        Button button,
                        int rowTile,
                        int columnTile,
                        Tilemap tilemap,
                        Animator animator,
                        List<Bomb> bombList,
                        List<Explosion> explosionList,
                        int speedUps,
                        int bombRange,
                        int bombsNumber,
                        int livesCount,
                        android.os.Bundle bundle) {

        super(context,
                joystick,
                button,
                rowTile,
                columnTile,
                tilemap,
                animator,
                bombList,
                explosionList,
                speedUps,
                bombRange,
                bombsNumber,
                livesCount,
                bundle);

        timeUsingBomb = 0;

        playerData = new PlayerData(getRelativePosX(),
                getRelativePosY(),
                rotationAngle,
                livesCount,
                bombRange,
                bombsNumber,
                0,
                0,
                bundle.getString(PLAYER_NAME),
                PlayerState.State.NOT_MOVING.toString());

        reference = FirebaseDatabase.getInstance().getReference(bundle.getString(CODE));
        reference.child(playerId).setValue(playerData);
    }

    @Override
    public void update() {
        if (livesCount < 1) {
            return;
        }
        initRectInTiles();

        selectDirectionFromActuator();

        detectCollisions();

        if (velocityX != 0 || velocityY != 0) {
            movePlayer();

            getOrientation();

            // Update player orientation
            rotationAngle = getAngle();
            playerData.rotationData = rotationAngle;
        }

        // Update player state for animation
        playerState.update();
        playerData.movingState = playerState.getState().toString();

        // Use bomb
        if (button.getIsPressed()) {
            useBomb();
            playerData.bombUsed = ++timeUsingBomb;
        } else {
            playerData.bombUsed = 0;
        }

        // Player death handler
        handleDeath();

        // Player picks power up handler
        handlePowerUpCollision();

        reference.child(playerId).setValue(playerData);
    }

    @Override
    protected void usePowerUp(Tile.LayoutType layoutType) {
        super.usePowerUp(layoutType);
        switch (layoutType) {
            case RANGE_POWER_UP:
                playerData.bombRange++;
                break;
            case BOMB_POWER_UP:
                playerData.bombNumber++;
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleDeath() {
        if (time < 1) {
            if (tileIsLayoutType(bottom, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(bottom, right, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, right, Tile.LayoutType.EXPLOSION)) {
                livesCount--;
                playerData.livesCount--;
                time = INVINCIBILITY_TIME;
                usedPaint = invincibilityPaint;
            }
            return;
        }
        time--;
        int aux = time % 4;
        switch (aux) {
            case 0:
                usedPaint = null;
                break;
            case 2:
                usedPaint = invincibilityPaint;
                break;
        }
        playerData.invincibilityTime = time;
    }

    @Override
    protected void movePlayer() {
        super.movePlayer();

        playerData.posX = getRelativePosX();
        playerData.posY = getRelativePosY();
    }

    private double getRelativePosX() {
        return positionX / tilemap.getMapRect().width();
    }

    private double getRelativePosY() {
        return positionY / tilemap.getMapRect().height();
    }

    public PlayerData getPlayerData() {
        return playerData;
    }
}
