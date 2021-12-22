package com.example.game.gameobject;

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
    private int bombRange;

    private List<Bomb> bombList;
    private List<Explosion> explosionList;

    //    private int bombsNumber;
    //    private boolean canThrow;
    //    private boolean canKick;
    private int speedUps;

    public Player(Context context, Joystick joystick, Button button, int rowTile, int columnTile,
                  Tilemap tilemap, Animator animator, List<Bomb> bombList, List<Explosion> explosionList,
                  int speedUps, int bombRange) {

        this.joystick = joystick;
        this.button = button;
        this.tilemap = tilemap;
        this.bombList = bombList;
        this.explosionList = explosionList;

        this.animator = animator;
        this.playerState = new PlayerState(this);

        this.speedUps = speedUps;
        this.bombRange = bombRange;
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
            if (tilemap.getTilemap()[rowIdx][columnIdx].getLayoutType() != Tile.LayoutType.BOMB &&
                    tilemap.getTilemap()[rowIdx][columnIdx].getLayoutType() != Tile.LayoutType.EXPLOSION)
                bombList.add(new Bomb(bombRange, rowIdx, columnIdx, bombList, explosionList, tilemap));
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

        Rect auxRect = new Rect(
                playerRect.left - tilemap.getMapRect().left,
                playerRect.top - tilemap.getMapRect().top,
                playerRect.right - tilemap.getMapRect().left,
                playerRect.bottom - tilemap.getMapRect().top);
        Rect newRect = new Rect(auxRect);
        newRect.offset((int) velocityX, (int) velocityY);

        if (velocityX != 0)
            if (velocityX < 0) {
                //goes left

                //map wall
                if (tilemap.insideMapLeft() >= newRect.left) {
                    velocityX = 0;
                }

                //tile collision
//                    if (newRect.left / newRect.height() != auxRect.left / auxRect.height()) {
//                        int column = newRect.left % newRect.height();
//                        int topRow = newRect.top / newRect.height();
//                        int bottomRow = newRect.bottom / newRect.height();
//                        if(tilemap.getTilemap()[topRow][column] instanceof WallTile){
//
//                        }
//                    }

            } else {
                //goes right

                //map wall
                if (tilemap.insideMapRight() <= newRect.right) {
                    velocityX = 0;
                }

                //tile collision


            }
        else if (velocityY < 0) {
            //goes up

            //map wall
            if (tilemap.insideMapTop() >= newRect.top) {
                velocityY = 0;
            }

            //tile collision


        } else {
            //goes down

            //map wall
            if (tilemap.insideMapBottom() <= newRect.bottom) {
                velocityY = 0;
            }

            //tile collision


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
