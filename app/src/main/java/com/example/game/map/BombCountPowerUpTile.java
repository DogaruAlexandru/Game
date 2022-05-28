package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class BombCountPowerUpTile extends Tile {
    private final Sprite walkSprite;
    private final Sprite bombCountPowerUpSprite;
    private final Paint paint;

    public BombCountPowerUpTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.BOMB_POWER_UP);
        bombCountPowerUpSprite = spriteSheet.getBombCountPowerUpSprite();
        walkSprite = spriteSheet.getLightWalkSprite();

        paint = new Paint();
        paint.setAlpha(180);
    }

    @Override
    public void draw(Canvas canvas) {
        walkSprite.draw(canvas, mapLocationRect);
        bombCountPowerUpSprite.draw(canvas, mapLocationRect, paint);
    }
}
