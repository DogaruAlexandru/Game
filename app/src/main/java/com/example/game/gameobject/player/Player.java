package com.example.game.gameobject.player;

import static com.example.game.Utils.spriteSizeOnScreen;
import static com.example.game.game.GameLoop.MAX_UPS;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.Utils;
import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Explosion;
import com.example.game.gamepanel.StatsBar;
import com.example.game.graphics.Animator;
import com.example.game.map.MapLayout;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {

    protected static final float INCREASE_IN_SPEED_BY_POWER_UP = .3f;
    protected static final float SPEED_MINIMIZING = .8f;
    protected static final int INVINCIBILITY_TIME = (int) MAX_UPS * 2;

    protected final Paint invincibilityPaint;
    protected final Tilemap tilemap;
    protected final Context context;
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

    protected final ArrayList<Tile.LayoutType> powerUpsLayoutTypes;
    protected final StatsBar statsBar;
    protected final int safe;

    protected int bottom, left, right, top;

    public Player(Context context,
                  int rowTile,
                  int columnTile,
                  Tilemap tilemap,
                  Animator animator,
                  List<Bomb> bombList,
                  List<Explosion> explosionList,
                  int speedUps,
                  int bombRange,
                  int bombsNumber,
                  int livesCount) {

        this.context = context;

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

        safe = spriteSizeOnScreen / 6;

        invincibilityPaint = new Paint();
        invincibilityPaint.setAlpha(80);
        usedPaint = null;

        powerUpsLayoutTypes = new ArrayList<>();
        powerUpsLayoutTypes.add(Tile.LayoutType.BOMB_POWER_UP);
        powerUpsLayoutTypes.add(Tile.LayoutType.RANGE_POWER_UP);
        powerUpsLayoutTypes.add(Tile.LayoutType.SPEED_POWER_UP);

        statsBar = new StatsBar(context, this);
    }

    protected void initRectInTiles() {
        bottom = (playerRect.bottom - 1 - safe) / spriteSizeOnScreen;
        left = (playerRect.left + safe) / spriteSizeOnScreen;
        right = (playerRect.right - 1 - safe) / spriteSizeOnScreen;
        top = (playerRect.top + safe) / spriteSizeOnScreen;
    }

    protected void handleDeath() {
        if (time < 1) {
            if (tileIsLayoutType(bottom, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(bottom, right, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, left, Tile.LayoutType.EXPLOSION) ||
                    tileIsLayoutType(top, right, Tile.LayoutType.EXPLOSION)) {
                livesCount--;
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
    }

    protected void handlePowerUpCollision() {
        for (Tile.LayoutType layoutType : powerUpsLayoutTypes) {
            if (tileIsLayoutType(bottom, left, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(bottom, left, MapLayout.WALK_TILE_LAYOUT_ID);

            } else if (tileIsLayoutType(bottom, right, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(bottom, right, MapLayout.WALK_TILE_LAYOUT_ID);

            } else if (tileIsLayoutType(top, left, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(top, left, MapLayout.WALK_TILE_LAYOUT_ID);

            } else if (tileIsLayoutType(top, right, layoutType)) {
                usePowerUp(layoutType);
                tilemap.changeTile(top, right, MapLayout.WALK_TILE_LAYOUT_ID);
            }
        }
    }

    protected void usePowerUp(Tile.LayoutType layoutType) {
        switch (layoutType) {
            case BOMB_POWER_UP:
                bombsNumber++;
                break;
            case RANGE_POWER_UP:
                bombRange++;
                break;
            case SPEED_POWER_UP:
                speedUps++;
                break;
            default:
                break;
        }
    }

    protected double getMaxSpeed() {
        return defaultMaxSpeed * (1 + INCREASE_IN_SPEED_BY_POWER_UP * speedUps);
    }

    protected boolean tileIsLayoutType(int row, int column, Tile.LayoutType layoutType) {
        return tilemap.getTilemap()[row][column].getLayoutType() == layoutType;
    }

    protected boolean useBomb() {
        int count = 0;
        for (int i = 0; i < bombList.size(); i++) {
            if (bombList.get(i).getPlayerId().equals(playerId)) {
                ++count;
            }
        }
        if (count >= bombsNumber) {
            return false;
        }

        int rowIdx = Utils.getPlayerRow(this);
        int columnIdx = Utils.getPlayerColumn(this);

        if (tileIsLayoutType(rowIdx, columnIdx, Tile.LayoutType.WALK)) {
            bombList.add(new Bomb(bombRange, rowIdx, columnIdx, playerId, explosionList, tilemap));
        }

        return true;
    }

    protected void getOrientation() {
        double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);
        directionX = velocityX / distance;
        directionY = velocityY / distance;
    }

    protected int getAngle() {
        return (int) Math.toDegrees(Math.atan2(directionY, directionX)) - 90;
    }

    protected void movePlayer() {
        positionX += velocityX;
        positionY += velocityY;
        playerRect.offsetTo((int) positionX, (int) positionY);
    }

    protected void detectCollisions() {
        if (velocityX == 0 && velocityY == 0) {
            return;
        }

        Rect newRect = new Rect(playerRect);
        newRect.offsetTo((int) (positionX + velocityX), (int) (positionY + velocityY));

        if (velocityX != 0) {
            if (velocityX < 0) {
                goesLeft(newRect);
            } else {
                goesRight(newRect);
            }
        } else if (velocityY < 0) {
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
        // Calculate the column and row indices of the given Rect
        int newRow = (newRect.bottom - 1) / spriteSizeOnScreen;
        int newLeftColumn = newRect.left / spriteSizeOnScreen;
        int newRightColumn = (newRect.right - 1) / spriteSizeOnScreen;

        // Check if the left or right of the player's Rect is colliding with the given Rect
        boolean leftCollision = velocityChanging(newRow, newLeftColumn);
        boolean rightCollision = velocityChanging(newRow, newRightColumn);

        // Update velocity based on collision status
        if (leftCollision) {
            if (rightCollision) {
                // Both the left and right of the player's Rect are colliding with the given Rect
                // Calculate the minimum distance needed to avoid the collision
                int minVelocity = (spriteSizeOnScreen - playerRect.bottom % spriteSizeOnScreen) %
                        spriteSizeOnScreen;
                velocityY = Math.min(velocityY, minVelocity);
            } else {
                // Only left column is occupied by an obstacle, set velocityX and velocityY to 0
                velocityX = velocityY * SPEED_MINIMIZING;
                velocityY = 0;
                int minVelocity = (spriteSizeOnScreen - playerRect.right % spriteSizeOnScreen) %
                        spriteSizeOnScreen;
                velocityX = Math.min(velocityX, minVelocity);
            }
        } else if (rightCollision) {
            // Only right column is occupied by an obstacle, set velocityX and velocityY to 0
            velocityX = -velocityY * SPEED_MINIMIZING;
            velocityY = 0;
            int minVelocity = -playerRect.left % spriteSizeOnScreen;
            velocityX = Math.max(velocityX, minVelocity);
        }
    }

    protected void goesUp(Rect newRect) {
        // Calculate the column and row indices of the given Rect
        int newRow = newRect.top / spriteSizeOnScreen;
        int newLeftColumn = newRect.left / spriteSizeOnScreen;
        int newRightColumn = (newRect.right - 1) / spriteSizeOnScreen;

        // Check if the left or right of the player's Rect is colliding with the given Rect
        boolean leftCollision = velocityChanging(newRow, newLeftColumn);
        boolean rightCollision = velocityChanging(newRow, newRightColumn);

        // Update velocity based on collision status
        if (leftCollision) {
            if (rightCollision) {
                // Both the left and right of the player's Rect are colliding with the given Rect
                // Calculate the minimum distance needed to avoid the collision
                int minVelocity = -playerRect.top % spriteSizeOnScreen;
                velocityY = Math.max(velocityY, minVelocity);
            } else {
                // Only left column is occupied by an obstacle, set velocityX and velocityY to 0
                velocityX = -velocityY * SPEED_MINIMIZING;
                velocityY = 0;
                int minVelocity = playerRect.left % spriteSizeOnScreen;
                velocityX = Math.min(velocityX, minVelocity);
            }
        } else if (rightCollision) {
            // Only right column is occupied by an obstacle, set velocityX and velocityY to 0
            velocityX = velocityY * SPEED_MINIMIZING;
            velocityY = 0;
            int minVelocity = -playerRect.right % spriteSizeOnScreen;
            velocityX = Math.max(velocityX, minVelocity);
        }
    }

    protected void goesRight(Rect newRect) {
        // Calculate the column and row indices of the given Rect
        int newColumn = (newRect.right - 1) / spriteSizeOnScreen;
        int newTopRow = newRect.top / spriteSizeOnScreen;
        int newBottomRow = (newRect.bottom - 1) / spriteSizeOnScreen;

        // Check if the top or bottom of the player's Rect is colliding with the given Rect
        boolean topCollision = velocityChanging(newTopRow, newColumn);
        boolean bottomCollision = velocityChanging(newBottomRow, newColumn);

        if (topCollision) {
            if (bottomCollision) {
                // Both the top and bottom of the player's Rect are colliding with the given Rect
                // Calculate the minimum distance needed to avoid the collision
                int minVelocity = (spriteSizeOnScreen - playerRect.right % spriteSizeOnScreen) %
                        spriteSizeOnScreen;
                velocityX = Math.min(minVelocity, velocityX);
            } else {
                // Only the top of the player's Rect is colliding with the given Rect
                velocityY = velocityX * SPEED_MINIMIZING;
                velocityX = 0;
                int minVelocity = (spriteSizeOnScreen - playerRect.bottom % spriteSizeOnScreen) %
                        spriteSizeOnScreen;
                velocityY = Math.min(minVelocity, velocityY);
            }
        } else if (bottomCollision) {
            // Only the bottom of the player's Rect is colliding with the given Rect
            velocityY = -velocityX * SPEED_MINIMIZING;
            velocityX = 0;
            int minVelocity = -playerRect.top % spriteSizeOnScreen;
            velocityY = Math.max(minVelocity, velocityY);
        }
    }

    protected void goesLeft(Rect newRect) {
        // Calculate the column and row indices of the given Rect
        int newColumn = newRect.left / spriteSizeOnScreen;
        int newTopRow = newRect.top / spriteSizeOnScreen;
        int newBottomRow = (newRect.bottom - 1) / spriteSizeOnScreen;

        // Check if the top or bottom of the player's Rect is colliding with the given Rect
        boolean topCollision = velocityChanging(newTopRow, newColumn);
        boolean bottomCollision = velocityChanging(newBottomRow, newColumn);

        if (topCollision) {
            if (bottomCollision) {
                // Both the top and bottom of the player's Rect are colliding with the given Rect
                // Calculate the minimum distance needed to avoid the collision
                int minVelocity = -playerRect.left % spriteSizeOnScreen;
                velocityX = Math.max(minVelocity, velocityX);
            } else {
                // Only the top of the player's Rect is colliding with the given Rect
                velocityY = -velocityX * SPEED_MINIMIZING;
                velocityX = 0;
                int minVelocity = spriteSizeOnScreen - playerRect.top % spriteSizeOnScreen;
                velocityY = Math.min(minVelocity, velocityY);
            }
        } else if (bottomCollision) {
            // Only the bottom of the player's Rect is colliding with the given Rect
            velocityY = velocityX * SPEED_MINIMIZING;
            velocityX = 0;
            int minVelocity = -playerRect.bottom % spriteSizeOnScreen;
            velocityY = Math.max(minVelocity, velocityY);
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

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void draw(Canvas canvas) {
        statsBar.draw(canvas);
        animator.draw(canvas, this, usedPaint);
    }

    public abstract void update();
}
