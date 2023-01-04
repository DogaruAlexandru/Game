package com.example.game.gameobject.player.enemy;

import static com.example.game.Utils.COLUMNS;
import static com.example.game.Utils.ROWS;
import static com.example.game.Utils.getPlayerColumn;
import static com.example.game.Utils.getPlayerRow;
import static com.example.game.map.Tile.LayoutType;

import android.content.Context;

import androidx.core.util.Pair;

import com.example.game.Utils;
import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Explosion;
import com.example.game.gameobject.player.Player;
import com.example.game.graphics.Animator;
import com.example.game.map.BombTile;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class OfflineEnemy extends Player {

    private enum OriginDirection {
        NONE,
        LEFT,
        UP,
        RIGHT,
        DOWN
    }

    private enum SearchType {
        stepOnNoExplosion,
        stepOnNoCurrentExplosion,
        stepOnAllExplosion
    }

    private static final int POWER_UP_SCORE = 30;
    private static final int CRATE_SCORE = 20;
    private static final int ENEMY_SCORE = 50;

    private final Map<String, Pair<Integer, Integer>> enemiesPos;

    private LayoutType[][] allExplosionMap;
    private OriginDirection[][] directions;
    private int playerRow, playerColumn;
    private int endPosScore;

    public OfflineEnemy(Context context,
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
                        Map<String, Pair<Integer, Integer>> enemiesPos) {

        super(context,
                rowTile,
                columnTile,
                tilemap,
                animator,
                bombList,
                explosionList,
                speedUps,
                bombRange,
                bombsNumber,
                livesCount);

        this.enemiesPos = enemiesPos;
    }

    @Override
    public void update() {
        initRectInTiles();

        makeMoves();

        detectCollisions();

        if (velocityX != 0 || velocityY != 0) {
            movePlayer();

            getOrientation();

            // Update player orientation
            rotationAngle = getAngle();
        }

        // Update player state for animation
        playerState.update();

        // Player death handler
        handleDeath();

        // Player picks power up handler
        handlePowerUpCollision();
    }

    private void makeMoves() {
        Pair<Integer, Integer> endPos = findDestination();
        if (endPos == null) {
            return;
        }
        ArrayList<OriginDirection> path = getPath(endPos);

        if (endPosScore > 0 && path.isEmpty()) {
            useBomb();
            return;
        }

        velocityX = 0;
        velocityY = 0;
        switch (path.get(path.size() - 1)) {
            case NONE:
                break;
            case LEFT:
                velocityX = -getMaxSpeed();
                break;
            case UP:
                velocityY = -getMaxSpeed();
                break;
            case RIGHT:
                velocityX = getMaxSpeed();
                break;
            case DOWN:
                velocityY = getMaxSpeed();
                break;
        }
        if (Utils.generator.nextFloat() < .3) {
            velocityX /= 2;
            velocityY /= 2;
        }
    }

    private ArrayList<OriginDirection> getPath(Pair<Integer, Integer> pos) {
        int row = pos.first;
        int column = pos.second;
        OriginDirection direction = directions[row][column];

        ArrayList<OriginDirection> path = new ArrayList<>();
        while (direction != null && direction != OriginDirection.NONE) {
            path.add(direction);
            switch (direction) {
                case LEFT:
                    column++;
                    break;
                case UP:
                    row++;
                    break;
                case RIGHT:
                    column--;
                    break;
                case DOWN:
                    row--;
                    break;
            }
            direction = directions[row][column];
        }

        return path;
    }

    private Pair<Integer, Integer> findDestination() {
        allExplosionMap = findFutureExplosions();
        endPosScore = 0;

        playerRow = getPlayerRow(this);
        playerColumn = getPlayerColumn(this);

        boolean allHasExpTile = tileIsDangerous(allExplosionMap[playerRow][playerColumn]);
        boolean nowHasExpTile = tileIsDangerous(tilemap.getTilemap()[playerRow][playerColumn]);

        if (nowHasExpTile) {
            //search in allExplosionMap first safe space
            return search(SearchType.stepOnAllExplosion);
        }
        if (allHasExpTile) {
            //search in allExplosionMap first safe space, but cant step on current danger
            return search(SearchType.stepOnNoCurrentExplosion);
        }
        //search in allExplosionMap for place to use bomb
        return search(SearchType.stepOnNoExplosion);
    }

    private Pair<Integer, Integer> search(SearchType searchType) {
        int rows = tilemap.getNumberOfRowTiles();
        int columns = tilemap.getNumberOfColumnTiles();

        directions = new OriginDirection[rows][columns];
        directions[playerRow][playerColumn] = OriginDirection.NONE;

        Queue<Pair<Integer, Integer>> searchQueue = new LinkedList<>();

        searchQueue.add(new Pair<>(playerRow, playerColumn));

        switch (searchType) {
            case stepOnNoExplosion:
                return searchNoExplosion(searchQueue);
            case stepOnNoCurrentExplosion:
            case stepOnAllExplosion:
                return searchExplosions(searchQueue, searchType);
            default:
                throw new IllegalStateException("Unexpected value: " + searchType);
        }
    }

    private Pair<Integer, Integer> searchNoExplosion(Queue<Pair<Integer, Integer>> searchQueue) {
        int rows = tilemap.getNumberOfRowTiles();
        int columns = tilemap.getNumberOfColumnTiles();
        int[][] scores = new int[rows][columns];

        int row, column;

        for (Map.Entry<String, Pair<Integer, Integer>> entry : enemiesPos.entrySet()) {
            row = entry.getValue().first;
            column = entry.getValue().second;
            if (entry.getKey().equals(playerId)) {
                continue;
            }
            scores[row][column] += ENEMY_SCORE;
        }
        Pair<Integer, Integer> endPos = null;

        while (!searchQueue.isEmpty()) {
            Pair<Integer, Integer> pair = searchQueue.remove();

            for (int i = 0; i < 4; i++) {
                row = pair.first + ROWS[i];
                column = pair.second + COLUMNS[i];

                if (directions[row][column] != null) {
                    continue;
                }

                switch (allExplosionMap[row][column]) {
                    case WALK:
                        searchQueue.add(new Pair<>(row, column));
                        directions[row][column] = getDirection(pair.first, pair.second, row, column);
                        break;
                    case BOMB_POWER_UP:
                    case RANGE_POWER_UP:
                    case SPEED_POWER_UP:
                        searchQueue.add(new Pair<>(row, column));
                        directions[row][column] = getDirection(pair.first, pair.second, row, column);
                        scores[row][column] += POWER_UP_SCORE;
                        break;
                    case CRATE:
                        scores[pair.first][pair.second] += CRATE_SCORE;
                        break;
                    case EXPLOSION:
                    case BOMB:
                    case WALL:
                    default:
                        break;
                }
            }
            if (scores[pair.first][pair.second] > endPosScore) {
                endPosScore = scores[pair.first][pair.second];
                endPos = pair;
            }
        }
        return endPos;
    }

    private Pair<Integer, Integer> searchExplosions(Queue<Pair<Integer, Integer>> searchQueue,
                                                    SearchType searchType) {
        while (!searchQueue.isEmpty()) {
            int row, column;
            Pair<Integer, Integer> pair = searchQueue.remove();

            for (int i = 0; i < 4; i++) {
                row = pair.first + ROWS[i];
                column = pair.second + COLUMNS[i];

                if (directions[row][column] != null) {
                    continue;
                }

                switch (allExplosionMap[row][column]) {
                    case WALK:
                    case BOMB_POWER_UP:
                    case RANGE_POWER_UP:
                    case SPEED_POWER_UP:
                        directions[row][column] = getDirection(pair.first, pair.second, row, column);
                        return new Pair<>(row, column);
                    case EXPLOSION:
                        if (searchType == SearchType.stepOnNoCurrentExplosion &&
                                tileIsDangerous(tilemap.getTilemap()[row][column])) {
                            break;
                        }
                        searchQueue.add(new Pair<>(row, column));
                        directions[row][column] = getDirection(pair.first, pair.second, row, column);
                        break;
                    case CRATE:
                    case BOMB:
                    case WALL:
                    default:
                        break;
                }
            }
        }
        return null;
    }

    private OriginDirection getDirection(int rowStart, int columnStart, int rowEnd, int columnEnd) {
        switch (rowStart - rowEnd) {
            case -1:
                return OriginDirection.DOWN;
            case 0:
                break;
            case 1:
                return OriginDirection.UP;
        }

        switch (columnStart - columnEnd) {
            case -1:
                return OriginDirection.RIGHT;
            case 1:
                return OriginDirection.LEFT;
        }

        return null;
    }

    private boolean tileIsDangerous(Tile tile) {
        return (tile.getLayoutType() == LayoutType.EXPLOSION);
    }

    private boolean tileIsDangerous(LayoutType layoutType) {
        return (layoutType == LayoutType.EXPLOSION);
    }

    //region Obtain map future explosions
    private LayoutType[][] findFutureExplosions() {
        LayoutType[][] map = new LayoutType[tilemap.getNumberOfRowTiles()][tilemap.getNumberOfColumnTiles()];
        copyOldTilemap(map);
        triggerAllBombs(map);
        return map;
    }

    private void triggerAllBombs(LayoutType[][] map) {
        int rows = tilemap.getNumberOfRowTiles();
        int columns = tilemap.getNumberOfColumnTiles();
        for (int idx = 1; idx < rows - 1; idx++) {
            for (int idy = 1; idy < columns - 1; idy++) {
                if (map[idx][idy] == LayoutType.BOMB) {
                    triggerBomb(((BombTile) tilemap.getTilemap()[idx][idy]).getBomb(), map);
                    map[idx][idy] = LayoutType.EXPLOSION;
                }
            }
        }
    }

    private void triggerBomb(Bomb bomb, LayoutType[][] map) {
        for (int i = 0; i < 4; i++) {
            explodeLength(bomb, ROWS[i], COLUMNS[i], map);
        }
    }

    private void explodeLength(Bomb bomb, int idxRow, int idxColumn, LayoutType[][] map) {
        int row = bomb.getRow();
        int column = bomb.getColumn();
        int range = bomb.getRange();

        for (int idx = 1; idx < range; idx++) {
            switch (map[row + idxRow * idx][column + idxColumn * idx]) {
                case WALK:
                case BOMB_POWER_UP:
                case RANGE_POWER_UP:
                case SPEED_POWER_UP:
                    map[row + idxRow * idx][column + idxColumn * idx] = LayoutType.EXPLOSION;
                    break;
                case EXPLOSION:
                case BOMB:
                    break;
                case CRATE:
                case WALL:
                default:
                    return;
            }
        }
    }

    private void copyOldTilemap(LayoutType[][] map) {
        int rows = tilemap.getNumberOfRowTiles();
        int columns = tilemap.getNumberOfColumnTiles();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                map[row][column] = tilemap.getTilemap()[row][column].getLayoutType();
            }
        }
    }
    //endregion
}
