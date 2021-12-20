package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.gameobject.Bomb;
import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class BombTile extends Tile {
    private final Sprite walkSprite;
    private final Sprite bombSprite;
    private Bomb bomb;

    public BombTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.BOMB);
        bombSprite = spriteSheet.getBombSprite();
        walkSprite = spriteSheet.getLightWalkSprite();
        bomb = null;
    }

    @Override
    public void draw(Canvas canvas) {
        walkSprite.draw(canvas, mapLocationRect);
        bombSprite.draw(canvas, mapLocationRect);
    }

    public Bomb getBomb() {
        return bomb;
    }

    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }
}
