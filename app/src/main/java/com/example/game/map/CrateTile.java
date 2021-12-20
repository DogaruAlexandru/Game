package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class CrateTile extends Tile {
    private final Sprite sprite;

    public CrateTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.CRATE);
        sprite = spriteSheet.getDarkCrateSprite();
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas, mapLocationRect);
    }
}
