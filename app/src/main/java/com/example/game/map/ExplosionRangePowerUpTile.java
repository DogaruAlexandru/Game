package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class ExplosionRangePowerUpTile extends Tile {

    private final Sprite walkSprite;
    private final Sprite explosionRangePowerUpSprite;
    private final Paint paint;

    public ExplosionRangePowerUpTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.RANGE_POWER_UP);

        explosionRangePowerUpSprite = spriteSheet.getExplosionRangePowerUpSprite();
        walkSprite = spriteSheet.getLightWalkSprite();

        paint = new Paint();
        paint.setAlpha(180);
    }

    public ExplosionRangePowerUpTile() {
        super(null, LayoutType.RANGE_POWER_UP);

        explosionRangePowerUpSprite = null;
        walkSprite = null;
        paint = null;
    }

    @Override
    public void draw(Canvas canvas) {
        walkSprite.draw(canvas, mapLocationRect);
        explosionRangePowerUpSprite.draw(canvas, mapLocationRect, paint);
    }
}
