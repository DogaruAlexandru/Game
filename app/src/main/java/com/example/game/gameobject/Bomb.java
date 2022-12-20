package com.example.game.gameobject;

import static com.example.game.Utils.COLUMNS;
import static com.example.game.Utils.ROWS;
import static com.example.game.game.GameLoop.MAX_UPS;

import com.example.game.map.BombTile;
import com.example.game.map.Tilemap;

import java.util.List;

public class Bomb {

    private static final int BOMB_TILE_LAYOUT_ID = 4;

    private final String playerId;
    private final int range;
    private final int row;
    private final int column;
    private final List<Explosion> explosionList;
    private final Tilemap tilemap;

    private int updatesBeforeExplosion;

    public Bomb(int range, int row, int column, String playerId,
                List<Explosion> explosionList, Tilemap tilemap) {

        this.range = range;
        this.row = row;
        this.column = column;
        this.explosionList = explosionList;
        this.tilemap = tilemap;
        this.playerId = playerId;

        updatesBeforeExplosion = (int) (MAX_UPS * 2.5);

        tilemap.changeTile(row, column, BOMB_TILE_LAYOUT_ID);
        ((BombTile) tilemap.getTilemap()[row][column]).setBomb(this);
    }

    public Bomb(Bomb bomb) {
        range = bomb.range;
        row = bomb.row;
        column = bomb.column;
        playerId = null;
        explosionList = null;
        tilemap = null;
        updatesBeforeExplosion = -1;
    }

    public void update(List<Bomb> bombRemoveList) {
        updatesBeforeExplosion--;

        if (updatesBeforeExplosion == 0) {
            triggerExplosion(bombRemoveList);
        }
    }

    public void triggerExplosion(List<Bomb> bombRemoveList) {
        explosionList.add(new Explosion(row, column, explosionList, tilemap));

        for (int i = 0; i < 4; i++) {
            explodeLength(bombRemoveList, ROWS[i], COLUMNS[i]);
        }

        if (!bombRemoveList.contains(this)) {
            bombRemoveList.add(this);
        }
    }

    private void explodeLength(List<Bomb> bombRemoveList, int idxRow, int idxColumn) {
        for (int idx = 1; idx < range; idx++) {
            switch (tilemap.getTilemap()[row + idxRow * idx][column + idxColumn * idx].getLayoutType()) {
                case BOMB:
                    ((BombTile) tilemap.getTilemap()[row + idxRow * idx][column + idxColumn * idx])
                            .getBomb().triggerExplosion(bombRemoveList);
                    break;
                case EXPLOSION:
                    break;
                case CRATE:
                    explosionList.add(new Explosion(row + idxRow * idx,
                            column + idxColumn * idx, explosionList, tilemap));
                    return;
                case WALK:
                case BOMB_POWER_UP:
                case RANGE_POWER_UP:
                case SPEED_POWER_UP:
                    explosionList.add(new Explosion(row + idxRow * idx,
                            column + idxColumn * idx, explosionList, tilemap));
                    break;
                case WALL:
                default:
                    return;
            }
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public int getRange() {
        return range;
    }
}
