package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class SpeedPowerUpTile extends Tile {

    private final Sprite walkSprite;
    private final Sprite speedPowerIUpSprite;
    private final Paint paint;

    public SpeedPowerUpTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.SPEED_POWER_UP);

        speedPowerIUpSprite = spriteSheet.getSpeedPowerUpSprite();
        walkSprite = spriteSheet.getLightWalkSprite();

        paint = new Paint();
        paint.setAlpha(180);
    }

    public SpeedPowerUpTile() {
        super(null, LayoutType.SPEED_POWER_UP);

        speedPowerIUpSprite = null;
        walkSprite = null;
        paint = null;
    }

    @Override
    public void draw(Canvas canvas) {
        walkSprite.draw(canvas, mapLocationRect);
        speedPowerIUpSprite.draw(canvas, mapLocationRect, paint);
    }
}
