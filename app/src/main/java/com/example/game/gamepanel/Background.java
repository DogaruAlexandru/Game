package com.example.game.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.R;
import com.example.game.Utils;

public class Background {
    Bitmap backgroundBitmap;
    Rect rect;

    public Background(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.background, bitmapOptions);

        rect = new Rect(0, 0, Utils.screenWidth, Utils.screenHeight);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(backgroundBitmap, rect, rect, null);
    }
}
