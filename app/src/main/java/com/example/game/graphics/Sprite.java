package com.example.game.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.game.Utils;

public class Sprite {
    private final SpriteSheet spriteSheet;
    private final Rect spriteRect;

    public Sprite(SpriteSheet spriteSheet, Rect spriteRect) {
        this.spriteSheet = spriteSheet;
        this.spriteRect = spriteRect;
    }

    public void draw(Canvas canvas, Rect displayRect) {
        draw(canvas, displayRect, null);
    }

    public void draw(Canvas canvas, Rect displayRect, Paint paint) {
        canvas.drawBitmap(spriteSheet.getBitmap(), spriteRect, displayRect, paint);
    }

    public void draw(Canvas canvas, Rect displayRect, float rotationAngle, Paint paint) {
        canvas.save();
        canvas.rotate(rotationAngle, displayRect.exactCenterX(), displayRect.exactCenterY());
        canvas.drawBitmap(spriteSheet.getBitmap(), spriteRect, displayRect, paint);
        canvas.restore();
    }
}
