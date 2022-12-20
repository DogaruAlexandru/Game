package com.example.game.gameobject;

import static com.example.game.game.GameLoop.MAX_UPS;

import com.example.game.Utils;
import com.example.game.map.ExplosionTile;
import com.example.game.map.Tilemap;

import java.util.List;

public class Explosion {

    private static final int EXPLOSION_TILE_LAYOUT_ID = 5;

    private final int row;
    private final int column;
    private int updatesBeforeDisappear;
    private final Tilemap tilemap;

    private int tileBehindExplosion;

    public Explosion(int row, int column, List<Explosion> explosionList, Tilemap tilemap) {
        this.row = row;
        this.column = column;
        this.tilemap = tilemap;

        tileBehindExplosion = 1;

        updatesBeforeDisappear = (int) (MAX_UPS * 0.8);

        switch (tilemap.getTilemap()[row][column].getLayoutType()) {
            case EXPLOSION:
                Explosion explosion = ((ExplosionTile) tilemap.getTilemap()[row][column]).getExplosion();
                tileBehindExplosion = explosion.getTileBehindExplosion();
                explosionList.remove(explosion);
                break;
            case CRATE:
                if (Utils.generator.nextFloat() < .3) {
                    tileBehindExplosion = getPowerUpBehindExplosion();
                }
                tilemap.changeTile(row, column, EXPLOSION_TILE_LAYOUT_ID);
                break;
            default:
                tilemap.changeTile(row, column, EXPLOSION_TILE_LAYOUT_ID);
                break;
        }

        ((ExplosionTile) tilemap.getTilemap()[row][column]).setExplosion(this);
    }

    private int getPowerUpBehindExplosion() {
        return Utils.generator.nextInt(90) % 3 + 6;
    }

    public int getTileBehindExplosion() {
        return tileBehindExplosion;
    }

    public void update(List<Explosion> explosionRemoveList) {
        updatesBeforeDisappear--;

        if (updatesBeforeDisappear == 0) {
            tilemap.changeTile(row, column, tileBehindExplosion);

            explosionRemoveList.add(this);
        }
    }
}
