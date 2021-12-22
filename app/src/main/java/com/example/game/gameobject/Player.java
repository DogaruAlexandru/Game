package com.example.game.gameobject;

import static com.example.game.Utils.spriteSizeOnScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.Utils;
import com.example.game.gamepanel.Button;
import com.example.game.gamepanel.Joystick;
import com.example.game.graphics.Animator;
import com.example.game.map.BombTile;
import com.example.game.map.ExplosionTile;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final float INCREASE_IN_SPEED_BY_POWER_UP = .4f;

    private final Joystick joystick;
    private final Button button;
    private final Tilemap tilemap;

    private double velocityX, velocityY;
    private double directionX, directionY;
    private double positionX, positionY;

    private Rect playerRect;
    private double defaultMaxSpeed;
    private Animator animator;
    private PlayerState playerState;
    private int rotationAngle;

    private List<Bomb> bombList;
    private List<Explosion> explosionList;

    private int bombRange;
    private int speedUps;
    private int bombsNumber;
    //    private boolean canThrow;
    //    private boolean canKick;

    public Player(Context context, Joystick joystick, Button button, int rowTile, int columnTile,
                  Tilemap tilemap, Animator animator, List<Bomb> bombList,
                  List<Explosion> explosionList, int speedUps, int bombRange, int bombsNumber) {

        this.joystick = joystick;
        this.button = button;
        this.tilemap = tilemap;
        this.bombList = bombList;
        this.explosionList = explosionList;

        this.animator = animator;
        this.playerState = new PlayerState(this);

        this.speedUps = speedUps;
        this.bombRange = bombRange;
        this.bombsNumber = bombsNumber;
        defaultMaxSpeed = Utils.getPlayerDefaultMaxSpeed();

        Rect tileRect = tilemap.getTilemap()[rowTile][columnTile].getMapLocationRect();
        playerRect = new Rect(tileRect);

        positionX = tileRect.left;
        positionY = tileRect.top;
        velocityX = 0;
        velocityY = 0;
        directionX = 0.0;
        directionY = 1.0;
    }

    public void draw(Canvas canvas) {
        animator.draw(canvas, this);
    }

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

        if (button.getIsPressed()) {
            int rowIdx = playerRect.centerY() / playerRect.width();
            int columnIdx = playerRect.centerX() / playerRect.height();
            if (tilemap.getTilemap()[rowIdx][columnIdx].getLayoutType() == Tile.LayoutType.WALK) {
                bombList.add(new Bomb(bombRange, rowIdx, columnIdx, bombList, explosionList, tilemap));
            }
        }
    }

    private void getOrientation() {
        // Update direction
        // Normalize velocity to get direction (unit vector of velocity)
        double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);
        directionX = velocityX / distance;
        directionY = velocityY / distance;
    }

    private void movePlayer() {
        positionX += velocityX;
        positionY += velocityY;
        playerRect.offsetTo((int) positionX, (int) positionY);
    }

    private void detectCollisions() {
        if (velocityX == 0 && velocityY == 0)
            return;

        //todo collision detections
        //todo if player close too the edge, help him get into the alley

        Rect newRect = new Rect(playerRect);
        newRect.offset((int) velocityX, (int) velocityY);

        if (velocityX != 0)
            if (velocityX < 0) {
                //goes left

                float aux = newRect.left % spriteSizeOnScreen - spriteSizeOnScreen;
                if (aux > velocityX)
                    velocityX = aux;

                //map wall
                if (tilemap.insideMapLeft() >= newRect.left) {
                    velocityX = 0;
                }

                //tile collision
                int newColumn = newRect.left / spriteSizeOnScreen;
                int newTopRow = newRect.top / spriteSizeOnScreen;
                int newBottomRow = (newRect.left - 1) / spriteSizeOnScreen;

                tileDetectionX(newTopRow, newColumn);
                tileDetectionX(newBottomRow, newColumn);

            } else {
                //goes right

                float aux = (newRect.right - 1) % spriteSizeOnScreen;
                if (aux < velocityX)
                    velocityX = aux;

                //map wall
                if (tilemap.insideMapRight() <= newRect.right) {
                    velocityX = 0;
                }

                //tile collision

                int newColumn = (newRect.right - 1) / spriteSizeOnScreen;
                int newTopRow = newRect.top / spriteSizeOnScreen;
                int newBottomRow = (newRect.left - 1) / spriteSizeOnScreen;

                tileDetectionX(newTopRow, newColumn);
                tileDetectionX(newBottomRow, newColumn);

            }
        else if (velocityY < 0) {
            //goes up

            float aux = newRect.top % spriteSizeOnScreen - spriteSizeOnScreen;
            if (aux > velocityX)
                velocityX = aux;

            //map wall
            if (tilemap.insideMapTop() >= newRect.top) {
                velocityY = 0;
            }

            //tile collision

            int newRow = newRect.top / spriteSizeOnScreen;
            int newLeftColumn = newRect.left / spriteSizeOnScreen;
            int newRightColumn = (newRect.right - 1) / spriteSizeOnScreen;

            tileDetectionY(newRow, newLeftColumn);
            tileDetectionY(newRow, newRightColumn);

        } else {
            //goes down

            float aux = (newRect.bottom - 1) % spriteSizeOnScreen;
            if (aux < velocityX)
                velocityX = aux;

            //map wall
            if (tilemap.insideMapBottom() <= newRect.bottom) {
                velocityY = 0;
            }

            //tile collision

            int newRow = (newRect.bottom - 1) / spriteSizeOnScreen;
            int newLeftColumn = newRect.left / spriteSizeOnScreen;
            int newRightColumn = (newRect.right - 1) / spriteSizeOnScreen;

            tileDetectionY(newRow, newLeftColumn);
            tileDetectionY(newRow, newRightColumn);

        }
    }

    private void tileDetectionX(int row, int column) {
        switch (tilemap.getTilemap()[row][column].getLayoutType()) {
            case WALL:
            case CRATE:
            case BOMB:
                velocityX = 0;
                break;
            case EXPLOSION:
                //todo die
                break;
            default:
                break;
        }
    }

    private void tileDetectionY(int row, int column) {
        switch (tilemap.getTilemap()[row][column].getLayoutType()) {
            case WALL:
            case CRATE:
            case BOMB:
                velocityY = 0;
                break;
            case EXPLOSION:
                //todo die
                break;
            default:
                break;
        }
    }

    private void selectDirectionFromActuator() {
        // Get the joystick orientation
        double actuatorX = joystick.getActuatorX();
        double actuatorY = joystick.getActuatorY();

        // Select direction by actuator value
        if (Math.abs(actuatorX) > Math.abs(actuatorY)) {
            velocityX = (actuatorX / Math.abs(actuatorX)) * defaultMaxSpeed *
                    (1 + INCREASE_IN_SPEED_BY_POWER_UP * speedUps);
            velocityY = 0;
            return;
        }
        if (Math.abs(actuatorX) < Math.abs(actuatorY)) {
            velocityY = (actuatorY / Math.abs(actuatorY)) * defaultMaxSpeed *
                    (1 + INCREASE_IN_SPEED_BY_POWER_UP * speedUps);
            velocityX = 0;
            return;
        }
        velocityX = 0;
        velocityY = 0;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public Rect getPlayerRect() {
        return playerRect;
    }

    public void setPlayerRect(Rect playerRect) {
        this.playerRect = playerRect;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }
}
