package com.example.game.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import com.example.game.R;
import com.example.game.Utils;
import com.example.game.gameobject.Player;

public class StatsBar {
    public static final int HEART_WIDTH_PIXELS = 512;
    public static final int HEART_HEIGHT_PIXELS = 512;

    private final Player player;
    private final Paint borderPaint;
    private final Bitmap heartBitmap;

    private final Rect srcRect;
    private final Paint textPaint;

    public StatsBar(Context context, Player player) {
        this.player = player;

        srcRect = new Rect(0, 0, HEART_WIDTH_PIXELS, HEART_HEIGHT_PIXELS);

        this.borderPaint = new Paint();
        int borderColor = ContextCompat.getColor(context, R.color.stats_bar_border);
        borderPaint.setColor(borderColor);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;

        heartBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart, bitmapOptions);

        textPaint = new Paint();
        int color = ContextCompat.getColor(context, R.color.white);
        textPaint.setColor(color);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextSize(player.getPlayerRect().height() * 0.66f);
    }

    public void draw(Canvas canvas) {
        // Draw border
        Rect playerRect = player.getPlayerRect();
        Rect borderRect = new Rect(
                playerRect.left,
                playerRect.top,
                playerRect.right,
                playerRect.bottom - playerRect.height() / 2);
        borderRect.offset(Utils.mapOffsetX, Utils.mapOffsetY - borderRect.height() / 2);
        canvas.drawRect(borderRect, borderPaint);

        // Draw heart
        Rect heartRect = new Rect(
                borderRect.left,
                borderRect.top,
                borderRect.right - borderRect.width() / 2,
                borderRect.bottom);
        canvas.drawBitmap(heartBitmap, srcRect, heartRect, null);

        // Draw number
        canvas.drawText(Integer.toString(player.getLivesCount()),
                heartRect.right, heartRect.bottom, textPaint);
    }
}
