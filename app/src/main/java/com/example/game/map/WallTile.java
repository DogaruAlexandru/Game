package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class WallTile extends Tile {
    private final Sprite sprite;

    public WallTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.WALL);
        sprite = spriteSheet.getDarkWallSprite();
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas, mapLocationRect);
    }
}
