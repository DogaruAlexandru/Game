package com.example.game.gameobject.player.enemy;

import static com.example.game.Utils.COLUMNS;
import static com.example.game.Utils.ROWS;
import static com.example.game.Utils.getPlayerColumn;
import static com.example.game.Utils.getPlayerRow;

import android.content.Context;

import androidx.core.util.Pair;

import com.example.game.Utils;
import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Explosion;
import com.example.game.gameobject.player.Player;
import com.example.game.graphics.Animator;
import com.example.game.map.BombCountPowerUpTile;
import com.example.game.map.BombTile;
import com.example.game.map.CrateTile;
import com.example.game.map.ExplosionRangePowerUpTile;
import com.example.game.map.ExplosionTile;
import com.example.game.map.SpeedPowerUpTile;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;
import com.example.game.map.WalkTile;
import com.example.game.map.WallTile;

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

    private static final int POWER_UP_SCORE = 30;
    private static final int CRATE_SCORE = 10;
    private static final int ENEMY_SCORE = 50;
    private static final int EXPLOSION_SCORE = -1000;

    private Tile[][] auxMap;
    private OriginDirection[][] directions;
    private int[][] scores;
    private final Map<String, Pair<Integer, Integer>> enemiesPos;

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
        Pair<Integer, Integer> endPos = findDirectionsAndEndPos();
        ArrayList<OriginDirection> path = getPath(endPos);

        if (path.size() == 0 || Utils.generator.nextFloat() < .01) {
            useBomb();
        } else {
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
    }

    private ArrayList<OriginDirection> getPath(Pair<Integer, Integer> pos) {
        int row = pos.first;
        int column = pos.second;
        OriginDirection direction = directions[row][column];

        ArrayList<OriginDirection> path = new ArrayList<>();
        while (direction != OriginDirection.NONE && direction != null) {
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

    private Pair<Integer, Integer> findDirectionsAndEndPos() {
        auxMap = findFutureExplosions();

        int rows = tilemap.getNumberOfRowTiles();
        int columns = tilemap.getNumberOfColumnTiles();

        scores = new int[rows][columns];
        directions = new OriginDirection[rows][columns];

        breadthFirstSearch();

        auxMap = tilemap.getTilemap();
        directions = new OriginDirection[rows][columns];
        Pair<Integer, Integer> endPos = breadthFirstSearch();

        // add enemies position score
        int row, column;
        for (Map.Entry<String, Pair<Integer, Integer>> entry : enemiesPos.entrySet()) {
            row = entry.getValue().first;
            column = entry.getValue().second;
            if (!entry.getKey().equals(playerId) && directions[row][column] != null) {
                scores[row][column] += ENEMY_SCORE;
            }
            if (scores[row][column] > scores[endPos.first][endPos.second]) {
                endPos = new Pair<>(row, column);
            }
        }

        return endPos;
    }

    private Pair<Integer, Integer> breadthFirstSearch() {

        int row = getPlayerRow(this);
        int column = getPlayerColumn(this);
        boolean startedWithExplosion = false;
        Queue<Pair<Integer, Integer>> searchQueue = new LinkedList<>();

        searchQueue.add(new Pair<>(row, column));
        directions[row][column] = OriginDirection.NONE;
        switch (auxMap[row][column].getLayoutType()) {
            case EXPLOSION:
            case BOMB:
                scores[row][column] += EXPLOSION_SCORE;
                startedWithExplosion = true;
                break;
        }

        int crateCount;
        int maxScore = -10;
        Pair<Integer, Integer> endPos = searchQueue.peek();

        while (!searchQueue.isEmpty()) {
            Pair<Integer, Integer> pair = searchQueue.remove();
            crateCount = 0;

            for (int i = 0; i < 4; i++) {
                row = pair.first + ROWS[i];
                column = pair.second + COLUMNS[i];

                if (directions[row][column] != null) {
                    continue;
                }

                switch (auxMap[row][column].getLayoutType()) {
                    case WALK:
                        if (startedWithExplosion) {
                            startedWithExplosion = false;
                            searchQueue.clear();
                        }
                        searchQueue.add(new Pair<>(row, column));
                        directions[row][column] = getDirection(pair.first, pair.second, row, column);
                        break;
                    case EXPLOSION:
                        if (!startedWithExplosion) {
                            break;
                        }
                        searchQueue.add(new Pair<>(row, column));
                        directions[row][column] = getDirection(pair.first, pair.second, row, column);
                        scores[row][column] += EXPLOSION_SCORE;
                        break;
                    case BOMB_POWER_UP:
                    case RANGE_POWER_UP:
                    case SPEED_POWER_UP:
                        searchQueue.add(new Pair<>(row, column));
                        directions[row][column] = getDirection(pair.first, pair.second, row, column);
                        scores[row][column] += POWER_UP_SCORE;
                        break;
                    case CRATE:
                        crateCount++;
                        break;
                    case BOMB:
                    case WALL:
                    default:
                        break;
                }
            }
            scores[pair.first][pair.second] += (CRATE_SCORE * crateCount);

            if (scores[pair.first][pair.second] > maxScore) {
                maxScore = scores[pair.first][pair.second];
                endPos = pair;
            }
        }

        return endPos;
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

    //region Obtain map future explosions
    private Tile[][] findFutureExplosions() {
        Tile[][] map = new Tile[tilemap.getNumberOfRowTiles()][tilemap.getNumberOfColumnTiles()];
        copyOldTilemap(map);
        triggerAllBombs(map);
        return map;
    }

    private void triggerAllBombs(Tile[][] map) {
        int rows = tilemap.getNumberOfRowTiles();
        int columns = tilemap.getNumberOfColumnTiles();
        for (int idx = 1; idx < rows - 1; idx++) {
            for (int idy = 1; idy < columns - 1; idy++) {
                if (map[idx][idy].getLayoutType() == Tile.LayoutType.BOMB) {
                    triggerBomb(((BombTile) map[idx][idy]).getBomb(), map);
                }
            }
        }
    }

    private void triggerBomb(Bomb bomb, Tile[][] map) {
        for (int i = 0; i < 4; i++) {
            explodeLength(bomb, ROWS[i], COLUMNS[i], map);
        }
    }

    private void explodeLength(Bomb bomb, int idxRow, int idxColumn, Tile[][] map) {
        int row = bomb.getRow();
        int column = bomb.getColumn();
        int range = bomb.getRange();

        for (int idx = 1; idx < range; idx++) {
            switch (map[row + idxRow * idx][column + idxColumn * idx].getLayoutType()) {
                case EXPLOSION:
                case BOMB:
                    break;
                case WALK:
                case BOMB_POWER_UP:
                case RANGE_POWER_UP:
                case SPEED_POWER_UP:
                    map[row + idxRow * idx][column + idxColumn * idx] = new ExplosionTile();
                    break;
                case CRATE:
                case WALL:
                default:
                    return;
            }
        }
    }

    private void copyOldTilemap(Tile[][] map) {
        int rows = tilemap.getNumberOfRowTiles();
        int columns = tilemap.getNumberOfColumnTiles();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Tile tile = tilemap.getTilemap()[row][column];
                switch (tile.getLayoutType()) {
                    case WALK:
                        map[row][column] = new WalkTile();
                        break;
                    case WALL:
                        map[row][column] = new WallTile();
                        break;
                    case CRATE:
                        map[row][column] = new CrateTile();
                        break;
                    case BOMB:
                        map[row][column] = new BombTile();
                        ((BombTile) map[row][column]).
                                setBomb(new Bomb(((BombTile) tile).getBomb()));
                        break;
                    case EXPLOSION:
                        map[row][column] = new ExplosionTile();
                        break;
                    case BOMB_POWER_UP:
                        map[row][column] = new BombCountPowerUpTile();
                        break;
                    case RANGE_POWER_UP:
                        map[row][column] = new ExplosionRangePowerUpTile();
                        break;
                    case SPEED_POWER_UP:
                        map[row][column] = new SpeedPowerUpTile();
                        break;
                    case EMPTY:
                        break;
                }
            }
        }
    }
    //endregion
}
