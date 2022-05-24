package com.example.game.gameobject;

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
    public void update() {
        if (livesCount < 1)
            return;

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
