package com.example.game.gameobject;

import static com.example.game.GameLoop.MAX_UPS;

import com.example.game.map.BombTile;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.List;

public class Bomb {
    private final int TIME_TILL_EXPLOSION = (int) (MAX_UPS * 2);
    private final Player player;
    private final int range;
    private final int row;
    private final int column;
    private final List<Bomb> bombList;
    private final List<Explosion> explosionList;
    private final Tilemap tilemap;

    private int updatesBeforeExplosion;

    public Bomb(int range, int row, int column, Player player, List<Bomb> bombList,
                List<Explosion> explosionList, Tilemap tilemap) {
        this.range = range;
        this.row = row;
        this.column = column;
        this.bombList = bombList;
        this.explosionList = explosionList;
        this.tilemap = tilemap;
        this.player = player;

        updatesBeforeExplosion = TIME_TILL_EXPLOSION;

        tilemap.changeTile(row, column, 4);
        ((BombTile) tilemap.getTilemap()[row][column]).setBomb(this);

        tilemap.setTilemapChanged(true);
    }

    public void update(List<Bomb> bombRemoveList) {
        --updatesBeforeExplosion;

        if (updatesBeforeExplosion == 0) {
            triggerExplosion(bombRemoveList);
        }
    }

    public void triggerExplosion(List<Bomb> bombRemoveList) {
        explosionList.add(new Explosion(row, column, explosionList, tilemap));

        explodeLength(bombRemoveList, -1, 0);
        explodeLength(bombRemoveList, 0, -1);
        explodeLength(bombRemoveList, 1, 0);
        explodeLength(bombRemoveList, 0, 1);

        if (!bombRemoveList.contains(this))
            bombRemoveList.add(this);

        tilemap.setTilemapChanged(true);
    }

    private void explodeLength(List<Bomb> bombRemoveList, int idxRow, int idxColumn) {
        for (int idx = 1; idx < range; ++idx) {
            switch (tilemap.getTilemap()[row + idxRow * idx][column + idxColumn * idx].
                    getLayoutType()) {
                case BOMB:
                    ((BombTile) tilemap.getTilemap()[row + idxRow * idx][column + idxColumn * idx])
                            .getBomb().triggerExplosion(bombRemoveList);
                case WALL:
                    return;
                case CRATE:
                    //todo
//                    explosionList.add(new Explosion(row + idxRow * idx,
//                            column + idxColumn * idx, explosionList, tilemap));
                    return;
                default:
                    explosionList.add(new Explosion(row + idxRow * idx,
                            column + idxColumn * idx, explosionList, tilemap));
            }
        }
    }

    public Player getPlayer() {
        return player;
    }
}
