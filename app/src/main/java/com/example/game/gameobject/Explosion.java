package com.example.game.gameobject;

import static com.example.game.game.GameLoop.MAX_UPS;

import com.example.game.Utils;
import com.example.game.map.ExplosionTile;
import com.example.game.map.MapLayout;
import com.example.game.map.Tile;
import com.example.game.map.Tilemap;

import java.util.List;

public class Explosion {

    private static final int EXPLOSION_TILE_LAYOUT_ID = 5;

    public static final int EXPLOSION_DURATION = (int) (MAX_UPS * 0.8);

    private final int row;
    private final int column;
    private int updatesBeforeDisappear;
    private final Tilemap tilemap;

    private int tileBehindExplosion;

    public Explosion(int row, int column, Tilemap tilemap) {
        this.row = row;
        this.column = column;
        this.tilemap = tilemap;

        tileBehindExplosion = MapLayout.WALK_TILE_LAYOUT_ID;

        updatesBeforeDisappear = EXPLOSION_DURATION;

        if (tilemap.getTilemap()[row][column].getLayoutType() == Tile.LayoutType.CRATE) {
            if (Utils.generator.nextFloat() < .3) {
                tileBehindExplosion = getPowerUpBehindExplosion();
            }
        }
        tilemap.changeTile(row, column, EXPLOSION_TILE_LAYOUT_ID);

        ((ExplosionTile) tilemap.getTilemap()[row][column]).setExplosion(this);
    }

    private int getPowerUpBehindExplosion() {
        return Utils.generator.nextInt(90) % 3 + 6;
    }

    public void update(List<Explosion> explosionRemoveList) {
        updatesBeforeDisappear--;

        if (updatesBeforeDisappear < 1) {
            tilemap.changeTile(row, column, tileBehindExplosion);

            explosionRemoveList.add(this);
        }
    }

    public void setUpdatesBeforeDisappear(int updatesBeforeDisappear) {
        this.updatesBeforeDisappear = updatesBeforeDisappear;
    }
}
