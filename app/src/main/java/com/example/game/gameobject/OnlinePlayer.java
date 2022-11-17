package com.example.game.gameobject;

import static com.example.game.Utils.CODE;
import static com.example.game.Utils.PLAYER_NAME;
import static com.example.game.Utils.spriteSizeOnScreen;

import com.example.game.gamepanel.Joystick;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;
import com.example.game.model.PlayerData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OnlinePlayer extends OfflinePlayer {

    private final DatabaseReference reference;

    protected final PlayerData playerData;

    public OnlinePlayer(Joystick joystick,
                        com.example.game.gamepanel.Button button,
                        int rowTile,
                        int columnTile,
                        Tilemap tilemap,
                        com.example.game.graphics.Animator animator,
                        List<Bomb> bombList,
                        List<Explosion> explosionList,
                        int speedUps,
                        int bombRange,
                        int bombsNumber,
                        int livesCount,
                        android.os.Bundle bundle) {

        super(joystick,
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

        playerData = new PlayerData(getRelativePoxX(),
                getRelativePoxY(),
                rotationAngle,
                livesCount,
                bombRange,
                bombsNumber,
                false,
                0,
                bundle.getString(PLAYER_NAME),
                PlayerState.State.NOT_MOVING.toString());

        reference = FirebaseDatabase.getInstance().getReference(bundle.getString(CODE));

        reference.child(playerId).setValue(playerData);
    }

    @Override
    public void update() {
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
            playerData.bombUsed = useBomb();
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
                playerData.bombCount++;
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleDeath() {
        if (time == 0) {
            int safe = spriteSizeOnScreen / 6;
            int bottom = (playerRect.bottom - 1 - safe) / spriteSizeOnScreen;
            int left = (playerRect.left + safe) / spriteSizeOnScreen;
            int right = (playerRect.right - 1 - safe) / spriteSizeOnScreen;
            int top = (playerRect.top + safe) / spriteSizeOnScreen;

            if (tileIsLayoutType(bottom, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(bottom, right, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, right, Tile.LayoutType.EXPLOSION)) {
                livesCount--;
                playerData.livesCount--;
                time = INVINCIBILITY_TIME;
                usedPaint = INVINCIBILITY_PAINT;
            }
        } else {
            time--;
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
        playerData.died = time;
    }

    @Override
    protected void movePlayer() {
        super.movePlayer();

        playerData.posX = getRelativePoxX();
        playerData.posY = getRelativePoxY();
    }

    private double getRelativePoxX() {
        return positionX / tilemap.getMapRect().width();
    }

    private double getRelativePoxY() {
        return positionY / tilemap.getMapRect().height();
    }

    public PlayerData getPlayerData() {
        return playerData;
    }
}
