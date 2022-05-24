package com.example.game.gameobject;

import com.example.game.graphics.Animator;
import com.example.game.map.Tilemap;

import java.util.List;

public class AIPlayer extends Player {

    private boolean goesLeft;
    private boolean goesRight;
    private boolean goesUp;
    private boolean goesDown;
    private boolean usesBomb;

    public AIPlayer(int rowTile, int columnTile, Tilemap tilemap, Animator animator,
                    List<Bomb> bombList, List<Explosion> explosionList, int speedUps,
                    int bombRange, int bombsNumber, int livesCount) {
        super(rowTile, columnTile, tilemap, animator, bombList, explosionList,
                speedUps, bombRange, bombsNumber, livesCount);
    }

    @Override
    public void update() {

        getModelOutputs();

        detectCollisions();

        if (velocityX != 0 || velocityY != 0) {
            movePlayer();

            getOrientation();

            // Update player orientation
            rotationAngle = (int) ((Math.atan2(directionY, directionX) * 180) / Math.PI) - 90;
        }

        // Update player state for animation
        playerState.update();

        // Player death handler
        handleDeath();
    }

    private void getModelOutputs() {
        //todo from the model gets the velocities and boolean for bomb uses
    }
}
