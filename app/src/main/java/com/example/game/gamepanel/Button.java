package com.example.game.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;

import com.example.game.R;
import com.example.game.graphics.Sprite;

public class Button {
    private final Paint circlePaint;
    private final Paint pressedCirclePaint;
    private final Paint bombPaint;
    private final Rect bombRect;

    private boolean isPressed;
    private Paint usedPaint;

    private final int circleRadius;
    private final int circleCenterPositionX;
    private final int circleCenterPositionY;
    private final Sprite bombSprite;

    public Button(Context context, int centerPositionX, int centerPositionY, int circleRadius,
                  Sprite sprite) {
        circleCenterPositionX = centerPositionX;
        circleCenterPositionY = centerPositionY;

        this.circleRadius = circleRadius;
        bombRect = new Rect(centerPositionX - circleRadius, centerPositionY - circleRadius,
                centerPositionX + circleRadius, centerPositionY + circleRadius);

        bombPaint = new Paint();
        bombPaint.setAlpha(150);

        circlePaint = new Paint();
        circlePaint.setColor(ContextCompat.getColor(context, R.color.controller_primary));
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        pressedCirclePaint = new Paint();
        pressedCirclePaint.setColor(ContextCompat.getColor(context, R.color.controller_secondary));
        pressedCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        usedPaint = circlePaint;

        bombSprite = sprite;
    }

    public void update() {
        updateColor();
    }

    private void updateColor() {
        if (isPressed)
            usedPaint = pressedCirclePaint;
        else
            usedPaint = circlePaint;
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {
        double buttonCenterToTouchDistance = Math.sqrt(
                Math.pow(circleCenterPositionX - touchPositionX, 2) +
                        Math.pow(circleCenterPositionY - touchPositionY, 2)
        );
        return buttonCenterToTouchDistance < circleRadius;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(
                circleCenterPositionX,
                circleCenterPositionY,
                circleRadius,
                usedPaint);

//        bombSprite.draw(canvas, bombRect, bombPaint);//todo
    }
}
