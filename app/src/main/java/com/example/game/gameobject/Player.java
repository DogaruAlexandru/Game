package com.example.game.gameobject;

import static com.example.game.GameLoop.MAX_UPS;
import static com.example.game.Utils.spriteSizeOnScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.Utils;
import com.example.game.gamepanel.Button;
import com.example.game.gamepanel.Joystick;
import com.example.game.graphics.Animator;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.List;

public class Player {

    private final float INCREASE_IN_SPEED_BY_POWER_UP = .4f;
    private final float SPEED_MINIMIZING = .8f;
    private final int INVINCIBILITY_TIME = (int) MAX_UPS * 2;
    private final Paint INVINCIBILITY_PAINT;

    private final Joystick joystick;
    private final Button button;
    private final Tilemap tilemap;

    private double velocityX, velocityY;
    private double directionX, directionY;
    private double positionX, positionY;
    private Paint usedPaint;

    private Rect playerRect;
    private double defaultMaxSpeed;
    private Animator animator;
    private PlayerState playerState;
    private int rotationAngle;

    private List<Bomb> bombList;
    private List<Explosion> explosionList;

    private int livesCount;
    private int bombRange;
    private int speedUps;
    private int bombsNumber;
    private int time = 0;
    //    private boolean canThrow;
    //    private boolean canKick;

    public Player(Context context, Joystick joystick, Button button, int rowTile, int columnTile,
                  Tilemap tilemap, Animator animator, List<Bomb> bombList,
                  List<Explosion> explosionList, int speedUps, int bombRange, int bombsNumber,
                  int livesCount) {

        this.joystick = joystick;
        this.button = button;
        this.tilemap = tilemap;
        this.bombList = bombList;
        this.explosionList = explosionList;

        this.animator = animator;
        this.playerState = new PlayerState(this);

        this.livesCount = livesCount;
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

        INVINCIBILITY_PAINT = new Paint();
        INVINCIBILITY_PAINT.setAlpha(80);
        usedPaint = null;
    }

    public void draw(Canvas canvas) {
        animator.draw(canvas, this, usedPaint);
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

        // Use bomb
        if (button.getIsPressed()) {
            useBomb();
        }

        // Player death handler
        handleDeath();
    }

    private void handleDeath() {
        if (time == 0) {
            int safe = spriteSizeOnScreen / 5;//todo
            int bottom = (playerRect.bottom - 1 - safe) / spriteSizeOnScreen;
            int left = (playerRect.left + safe) / spriteSizeOnScreen;
            int right = (playerRect.right - 1 - safe) / spriteSizeOnScreen;
            int top = (playerRect.top + safe) / spriteSizeOnScreen;

            if (tileIsLayoutType(bottom, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(bottom, right, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, right, Tile.LayoutType.EXPLOSION)) {
                --livesCount;
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

    private boolean tileIsLayoutType(int row, int column, Tile.LayoutType explosion) {
        return tilemap.getTilemap()[row][column].getLayoutType() == explosion;
    }

    private void useBomb() {
        int rowIdx = playerRect.centerY() / playerRect.width();
        int columnIdx = playerRect.centerX() / playerRect.height();
        if (tileIsLayoutType(rowIdx, columnIdx, Tile.LayoutType.WALK)) {
            int count = 0;
            for (int i = 0; i < bombList.size(); i++) {
                if (bombList.get(i).getPlayer() == this)
                    ++count;
            }
            if (count != bombsNumber)
                bombList.add(new Bomb(bombRange, rowIdx, columnIdx, this,
                        bombList, explosionList, tilemap));
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

        Rect newRect = new Rect(playerRect);
        newRect.offsetTo((int) (positionX + velocityX), (int) (positionY + velocityY));

        if (velocityX != 0)
            if (velocityX < 0) {
                goesLeft(newRect);
            } else {
                goesRight(newRect);
            }
        else if (velocityY < 0) {
            goesUp(newRect);
        } else {
            goesDown(newRect);
        }
    }

    private void goesDown(Rect newRect) {
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

    private void goesUp(Rect newRect) {
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

    private void goesRight(Rect newRect) {
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

    private void goesLeft(Rect newRect) {
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

    private boolean velocityChanging(int row, int column) {
        switch (tilemap.getTilemap()[row][column].getLayoutType()) {
            case WALL:
            case CRATE:
            case BOMB:
                return true;
            default:
                return false;
        }
    }

    private void selectDirectionFromActuator() {
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
