package com.example.game.gameobject;

import static com.example.game.game.GameLoop.MAX_UPS;

import com.example.game.Utils;
import com.example.game.map.ExplosionTile;
import com.example.game.map.Tilemap;

import java.util.List;

public class Explosion {
    private final int DURATION = (int) (MAX_UPS * 0.8);
    private int row;
    private int column;
    private int updatesBeforeDisappear;
    private List<Explosion> explosionList;
    private Tilemap tilemap;

    private int tileBehindExplosion;

    public Explosion(int row, int column, List<Explosion> explosionList, Tilemap tilemap) {
        this.row = row;
        this.column = column;
        this.explosionList = explosionList;
        this.tilemap = tilemap;

        tileBehindExplosion = 1;

        updatesBeforeDisappear = DURATION;

        switch (tilemap.getTilemap()[row][column].getLayoutType()) {
            case EXPLOSION:
                Explosion explosion = ((ExplosionTile) tilemap.getTilemap()[row][column]).getExplosion();
                tileBehindExplosion = explosion.getTileBehindExplosion();
                explosionList.remove(explosion);
                break;
            case CRATE:
                if (Utils.generator.nextFloat() < .5) {
                    tileBehindExplosion = Utils.generator.nextInt(90) % 3 + 6;
                }
                tilemap.changeTile(row, column, 5);
                tilemap.setTilemapChanged(true);
                break;
            default:
                tilemap.changeTile(row, column, 5);
                tilemap.setTilemapChanged(true);
                break;
        }

        ((ExplosionTile) tilemap.getTilemap()[row][column]).setExplosion(this);
    }

    public int getTileBehindExplosion() {
        return tileBehindExplosion;
    }

    public void update(List<Explosion> explosionRemoveList) {
        --updatesBeforeDisappear;

        if (updatesBeforeDisappear == 0) {

            tilemap.changeTile(row, column, tileBehindExplosion);

            tilemap.setTilemapChanged(true);

            explosionRemoveList.add(this);
        }
    }
}
