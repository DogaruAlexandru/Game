package com.example.game.gameobject;

import static com.example.game.GameLoop.MAX_UPS;

import com.example.game.map.BombTile;
import com.example.game.map.ExplosionTile;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.List;

public class Explosion {
    private final int DURATION = (int) (MAX_UPS * 0.8);
    private int row;
    private int column;
    private int updatesBeforeDisappear;
    private List<Explosion> explosionList;
    private Tilemap tilemap;

    public Explosion(int row, int column, List<Explosion> explosionList, Tilemap tilemap) {
        this.row = row;
        this.column = column;
        this.explosionList = explosionList;
        this.tilemap = tilemap;

        updatesBeforeDisappear = DURATION;

        if (tilemap.getTilemap()[row][column].getLayoutType() == Tile.LayoutType.EXPLOSION) {
            explosionList.remove(((ExplosionTile) tilemap.getTilemap()[row][column]).getExplosion());
        } else {
            tilemap.changeTile(row, column, 5);
            tilemap.setTilemapChanged(true);
        }
        ((ExplosionTile) tilemap.getTilemap()[row][column]).setExplosion(this);
    }

    public void update(List<Explosion> explosionRemoveList) {
        --updatesBeforeDisappear;

        if (updatesBeforeDisappear == 0) {

            tilemap.changeTile(row, column, 1);

            tilemap.setTilemapChanged(true);

            explosionRemoveList.add(this);
        }
    }
}
