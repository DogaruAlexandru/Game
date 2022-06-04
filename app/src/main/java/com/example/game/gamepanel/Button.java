package com.example.game.gamepanel;

import static com.example.game.Utils.getDistanceBetweenPoints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.game.R;

public class Button {
    private final Paint circlePaint;
    private final Paint pressedCirclePaint;

    private boolean isPressed;
    private Paint usedPaint;

    private final int circleRadius;
    private final int circleCenterPositionX;
    private final int circleCenterPositionY;

    public Button(Context context, int centerPositionX, int centerPositionY, int circleRadius) {
        circleCenterPositionX = centerPositionX;
        circleCenterPositionY = centerPositionY;

        this.circleRadius = circleRadius;

        Paint bombPaint = new Paint();
        bombPaint.setAlpha(150);

        circlePaint = new Paint();
        circlePaint.setColor(ContextCompat.getColor(context, R.color.controller_primary));
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        pressedCirclePaint = new Paint();
        pressedCirclePaint.setColor(ContextCompat.getColor(context, R.color.controller_secondary));
        pressedCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        usedPaint = circlePaint;
    }

    public void update() {
        updateColor();
    }

    private void updateColor() {
        if (isPressed) {
            usedPaint = pressedCirclePaint;
        } else {
            usedPaint = circlePaint;
        }
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {
        double buttonCenterToTouchDistance = getDistanceBetweenPoints(
                circleCenterPositionX, circleCenterPositionY, touchPositionX, touchPositionY);

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
    }
}
