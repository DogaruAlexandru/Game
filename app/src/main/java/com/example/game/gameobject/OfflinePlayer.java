package com.example.game.gameobject;

import static com.example.game.Utils.spriteSizeOnScreen;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;

import com.example.game.gamepanel.Button;
import com.example.game.gamepanel.Joystick;
import com.example.game.graphics.Animator;
import com.example.game.map.Tilemap;

import java.util.List;

public class OfflinePlayer extends Player {

    protected final Joystick joystick;
    protected final Button button;

    public OfflinePlayer(Joystick joystick, Button button, int rowTile, int columnTile,
                         Tilemap tilemap, Animator animator, List<Bomb> bombList,
                         List<Explosion> explosionList, int speedUps, int bombRange, int bombsNumber,
                         int livesCount, Bundle bundle) {
        super(rowTile, columnTile, tilemap, animator, bombList, explosionList, speedUps, bombRange,
                bombsNumber, livesCount);

        this.joystick = joystick;
        this.button = button;

        playerId = bundle.getString("playerId");
    }

    @Override
    public void draw(Canvas canvas) {
        animator.draw(canvas, this, usedPaint);
    }

    @Override
    public void update() {

        selectDirectionFromActuator();

        detectCollisions();

        if (velocityX != 0 || velocityY != 0) {
            movePlayer();

            getOrientation();

            // Update player orientation
            rotationAngle = (int) ((Math.atan2(directionY, directionX) * 180) / Math.PI) - 90;
        }

        // Update player state for animation
        playerState.update();

        // Use bomb
        if (button.getIsPressed())
            useBomb();

        // Player death handler
        handleDeath();
    }

    @Override
    protected void goesDown(Rect newRect) {
        int newRow = (newRect.bottom - 1) / spriteSizeOnScreen;
        int newLeftColumn = newRect.left / spriteSizeOnScreen;
        int newRightColumn = (newRect.right - 1) / spriteSizeOnScreen;

        boolean leftBool = velocityChanging(newRow, newLeftColumn);
        boolean rightBool = velocityChanging(newRow, newRightColumn);

        if (leftBool) {
            if (rightBool) {
                int aux = (spriteSizeOnScreen - playerRect.bottom
                        % spriteSizeOnScreen) % spriteSizeOnScreen;
                velocityY = aux < velocityY ? aux : velocityY;
            } else {
                velocityX = velocityY * SPEED_MINIMIZING;
                velocityY = 0;
                int aux = (spriteSizeOnScreen - playerRect.right %
                        spriteSizeOnScreen) % spriteSizeOnScreen;
                velocityX = aux < velocityX ? aux : velocityX;
            }
        } else if (rightBool) {
            velocityX = -velocityY * SPEED_MINIMIZING;
            velocityY = 0;
            int aux = -playerRect.left % spriteSizeOnScreen;
            velocityX = aux > velocityX ? aux : velocityX;
        }
    }

    @Override
    protected void goesUp(Rect newRect) {
        int newRow = newRect.top / spriteSizeOnScreen;
        int newLeftColumn = newRect.left / spriteSizeOnScreen;
        int newRightColumn = (newRect.right - 1) / spriteSizeOnScreen;

        boolean leftBool = velocityChanging(newRow, newLeftColumn);
        boolean rightBool = velocityChanging(newRow, newRightColumn);

        if (leftBool) {
            if (rightBool) {
                int aux = -playerRect.top % spriteSizeOnScreen;
                velocityY = aux > velocityY ? aux : velocityY;
            } else {
                velocityX = -velocityY * SPEED_MINIMIZING;
                velocityY = 0;
                int aux = playerRect.left % spriteSizeOnScreen;
                velocityX = aux < velocityX ? aux : velocityX;
            }
        } else if (rightBool) {
            velocityX = velocityY * SPEED_MINIMIZING;
            velocityY = 0;
            int aux = -playerRect.right % spriteSizeOnScreen;
            velocityX = aux > velocityX ? aux : velocityX;
        }
    }

    @Override
    protected void goesRight(Rect newRect) {
        int newColumn = (newRect.right - 1) / spriteSizeOnScreen;
        int newTopRow = newRect.top / spriteSizeOnScreen;
        int newBottomRow = (newRect.bottom - 1) / spriteSizeOnScreen;

        boolean topBool = velocityChanging(newTopRow, newColumn);
        boolean bottomBool = velocityChanging(newBottomRow, newColumn);

        if (topBool) {
            if (bottomBool) {
                int aux = (spriteSizeOnScreen - playerRect.right %
                        spriteSizeOnScreen) % spriteSizeOnScreen;
                velocityX = aux < velocityX ? aux : velocityX;
            } else {
                velocityY = velocityX * SPEED_MINIMIZING;
                velocityX = 0;
                int aux = (spriteSizeOnScreen - playerRect.bottom
                        % spriteSizeOnScreen) % spriteSizeOnScreen;
                velocityY = aux < velocityY ? aux : velocityY;
            }
        } else if (bottomBool) {
            velocityY = -velocityX * SPEED_MINIMIZING;
            velocityX = 0;
            int aux = -playerRect.top % spriteSizeOnScreen;
            velocityY = aux > velocityY ? aux : velocityY;
        }
    }

    @Override
    protected void goesLeft(Rect newRect) {
        int newColumn = newRect.left / spriteSizeOnScreen;
        int newTopRow = newRect.top / spriteSizeOnScreen;
        int newBottomRow = (newRect.bottom - 1) / spriteSizeOnScreen;

        boolean topBool = velocityChanging(newTopRow, newColumn);
        boolean bottomBool = velocityChanging(newBottomRow, newColumn);

        if (topBool) {
            if (bottomBool) {
                int aux = -playerRect.left % spriteSizeOnScreen;
                velocityX = aux > velocityX ? aux : velocityX;
            } else {
                velocityY = -velocityX * SPEED_MINIMIZING;
                velocityX = 0;
                int aux = spriteSizeOnScreen - playerRect.top % spriteSizeOnScreen;
                velocityY = aux < velocityY ? aux : velocityY;
            }
        } else if (bottomBool) {
            velocityY = velocityX * SPEED_MINIMIZING;
            velocityX = 0;
            int aux = -playerRect.bottom % spriteSizeOnScreen;
            velocityY = aux > velocityY ? aux : velocityY;
        }
    }

    protected void selectDirectionFromActuator() {
        // Get the joystick orientation
        double actuatorX = joystick.getActuatorX();
        double actuatorY = joystick.getActuatorY();

        // Select direction by actuator value
        if (Math.abs(actuatorX) > Math.abs(actuatorY)) {
            velocityX = actuatorX * defaultMaxSpeed * (1 + INCREASE_IN_SPEED_BY_POWER_UP * speedUps);
            velocityY = 0;
            return;
        }
        if (Math.abs(actuatorX) < Math.abs(actuatorY)) {
            velocityY = actuatorY * defaultMaxSpeed * (1 + INCREASE_IN_SPEED_BY_POWER_UP * speedUps);
            velocityX = 0;
            return;
        }
        velocityX = 0;
        velocityY = 0;
    }
}
