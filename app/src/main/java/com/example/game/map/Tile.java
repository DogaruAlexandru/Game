package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.graphics.SpriteSheet;

public abstract class Tile {

    public enum LayoutType {
        EMPTY,
        WALK,
        WALL,
        CRATE,
        BOMB,
        EXPLOSION,
//        KIC_POWER_UPK,
//        BOMB_POWER_UP,
//        RANGE_POWER_UP,
//        SPEED_POWER_UP,
//        HAND_POWER_UP
    }

    protected final Rect mapLocationRect;
    protected final LayoutType layoutType;

    public Tile(Rect mapLocationRect, LayoutType layoutType) {
        this.mapLocationRect = mapLocationRect;
        this.layoutType = layoutType;
    }

    public static Tile getTile(int idxTileType, SpriteSheet spriteSheet, Rect mapLocationRect) {
        switch (LayoutType.values()[idxTileType]) {
            case WALK:
                return new WalkTile(spriteSheet, mapLocationRect);
            case WALL:
                return new WallTile(spriteSheet, mapLocationRect);
            case CRATE:
                return new CrateTile(spriteSheet, mapLocationRect);
            case BOMB:
                return new BombTile(spriteSheet, mapLocationRect);
            case EXPLOSION:
                return new ExplosionTile(spriteSheet, mapLocationRect);
            case EMPTY:
            default:
                return null;
        }
    }

    public abstract void draw(Canvas canvas);

    public Rect getMapLocationRect() {
        return mapLocationRect;
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }
}
