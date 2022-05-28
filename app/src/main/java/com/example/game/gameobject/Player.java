package com.example.game.gameobject;

import static com.example.game.GameLoop.MAX_UPS;
import static com.example.game.Utils.spriteSizeOnScreen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.Utils;
import com.example.game.graphics.Animator;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {

    protected final float INCREASE_IN_SPEED_BY_POWER_UP = .4f;
    protected final float SPEED_MINIMIZING = .8f;
    protected final int INVINCIBILITY_TIME = (int) MAX_UPS * 2;
    protected final Paint INVINCIBILITY_PAINT;

    protected final Tilemap tilemap;
    protected String playerId;

    protected double velocityX, velocityY;
    protected double directionX, directionY;
    protected double positionX, positionY;
    protected Paint usedPaint;

    protected final Rect playerRect;
    protected final double defaultMaxSpeed;
    protected final Animator animator;
    protected final PlayerState playerState;
    protected int rotationAngle;

    protected final List<Bomb> bombList;
    protected final List<Explosion> explosionList;

    protected int livesCount;
    protected int bombRange;
    protected int speedUps;
    protected int bombsNumber;
    protected int time = 0;
    //    private boolean canThrow;
    //    private boolean canKick;

    protected final ArrayList<Tile.LayoutType> powerUpsLayoutTypes;

    public Player(int rowTile, int columnTile,
                  Tilemap tilemap, Animator animator, List<Bomb> bombList,
                  List<Explosion> explosionList, int speedUps, int bombRange, int bombsNumber,
                  int livesCount) {

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

        powerUpsLayoutTypes = new ArrayList<>();
        powerUpsLayoutTypes.add(Tile.LayoutType.BOMB_POWER_UP);
        powerUpsLayoutTypes.add(Tile.LayoutType.RANGE_POWER_UP);
        powerUpsLayoutTypes.add(Tile.LayoutType.SPEED_POWER_UP);
    }

//    protected void getRectangle(Integer bottom, Integer left, Integer right, Integer top) {
//        int safe = spriteSizeOnScreen / 6;
//        bottom = (playerRect.bottom - 1 - safe) / spriteSizeOnScreen;
//        left = (playerRect.left + safe) / spriteSizeOnScreen;
//        right = (playerRect.right - 1 - safe) / spriteSizeOnScreen;
//        top = (playerRect.top + safe) / spriteSizeOnScreen;
//    }

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

    protected void handlePowerUpCollision() {

        int walkTileIdx = 1;
        int safe = spriteSizeOnScreen / 6;
        int bottom = (playerRect.bottom - 1 - safe) / spriteSizeOnScreen;
        int left = (playerRect.left + safe) / spriteSizeOnScreen;
        int right = (playerRect.right - 1 - safe) / spriteSizeOnScreen;
        int top = (playerRect.top + safe) / spriteSizeOnScreen;

        for (Tile.LayoutType layoutType : powerUpsLayoutTypes) {
            if (tileIsLayoutType(bottom, left, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(bottom, left, walkTileIdx);
                tilemap.setTilemapChanged(true);

            } else if (tileIsLayoutType(bottom, right, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(bottom, right, walkTileIdx);
                tilemap.setTilemapChanged(true);

            } else if (tileIsLayoutType(top, left, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(top, left, walkTileIdx);
                tilemap.setTilemapChanged(true);

            } else if (tileIsLayoutType(top, right, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(top, right, walkTileIdx);
                tilemap.setTilemapChanged(true);
            }
        }
    }

    protected void usePowerUp(Tile.LayoutType layoutType) {
        switch (layoutType) {
            case BOMB_POWER_UP:
                ++bombsNumber;
                break;
            case RANGE_POWER_UP:
                ++bombRange;
                break;
            case SPEED_POWER_UP:
                ++speedUps;
                break;
            default:
                break;
        }
    }

    protected boolean tileIsLayoutType(int row, int column, Tile.LayoutType layoutType) {
        return tilemap.getTilemap()[row][column].getLayoutType() == layoutType;
    }

    protected boolean useBomb() {
        int rowIdx = playerRect.centerY() / playerRect.width();
        int columnIdx = playerRect.centerX() / playerRect.height();
        if (!tileIsLayoutType(rowIdx, columnIdx, Tile.LayoutType.WALK))
            return false;

        int count = 0;
        for (int i = 0; i < bombList.size(); i++) {
            if (bombList.get(i).getPlayerId().equals(playerId))
                ++count;
        }
        if (count >= bombsNumber)
            return false;

        bombList.add(new Bomb(bombRange, rowIdx, columnIdx, playerId,
                bombList, explosionList, tilemap));
        return true;
    }

    protected void getOrientation() {
        // Update direction
        // Normalize velocity to get direction (unit vector of velocity)
        double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);
        directionX = velocityX / distance;
        directionY = velocityY / distance;
    }

    protected void movePlayer() {
        positionX += velocityX;
        positionY += velocityY;
        playerRect.offsetTo((int) positionX, (int) positionY);
    }

    protected void detectCollisions() {
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

    protected boolean velocityChanging(int row, int column) {
        switch (tilemap.getTilemap()[row][column].getLayoutType()) {
            case WALL:
            case CRATE:
            case BOMB:
                return true;
            default:
                return false;
        }
    }

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

    public PlayerState getPlayerState() {
        return playerState;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public Rect getPlayerRect() {
        return playerRect;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public int getLivesCount() {
        return livesCount;
    }

    public void draw(Canvas canvas) {
        animator.draw(canvas, this, usedPaint);
    }

    public abstract void update();
}
