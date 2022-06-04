package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class WalkTile extends Tile {
    private final Sprite sprite;

    public WalkTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.WALK);
        sprite = spriteSheet.getLightWalkSprite();
    }

    public WalkTile() {
        super(null, LayoutType.WALK);
        sprite = null;
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas, mapLocationRect);
    }
}
