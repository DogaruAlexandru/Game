package com.example.game.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.game.R;
import com.example.game.Utils;

public class Joystick {

    private Paint innerCirclePaint;
    private Paint outerCirclePaint;

    private int outerCircleRadius;
    private int innerCircleRadius;

    private int outerCircleCenterPositionX;
    private int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;

    private double joystickCenterToTouchDistance;
    private boolean isPressed;
    private double actuatorX;
    private double actuatorY;

    public Joystick(Context context, int centerPositionX, int centerPositionY,
                    int outerCircleRadius, int innerCircleRadius) {

        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPositionX;
        innerCircleCenterPositionY = centerPositionY;

        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;

        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(ContextCompat.getColor(context, R.color.controller_secondary));
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(ContextCompat.getColor(context, R.color.controller_primary));
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                outerCircleRadius,
                outerCirclePaint);

        canvas.drawCircle(
                innerCircleCenterPositionX,
                innerCircleCenterPositionY,
                innerCircleRadius,
                innerCirclePaint);
    }

    public void update() {
        updateInnerCirclePosition();
    }

    private void updateInnerCirclePosition() {
        innerCircleCenterPositionX =
                (int) (outerCircleCenterPositionX + actuatorX * outerCircleRadius);
        innerCircleCenterPositionY =
                (int) (outerCircleCenterPositionY + actuatorY * outerCircleRadius);
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {
        joystickCenterToTouchDistance = Utils.getDistanceBetweenPoints(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                touchPositionX,
                touchPositionY
        );
        return joystickCenterToTouchDistance < outerCircleRadius;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public void setActuator(double touchPositionX, double touchPositionY) {
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Utils.getDistanceBetweenPoints(0, 0, deltaX, deltaY);

        if (deltaDistance < outerCircleRadius) {
            actuatorX = deltaX / outerCircleRadius;
            actuatorY = deltaY / outerCircleRadius;
        } else {
            actuatorX = deltaX / deltaDistance;
            actuatorY = deltaY / deltaDistance;
        }
    }

    public void resetActuator() {
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    public double getActuatorX() {
        return actuatorX;
    }

    public double getActuatorY() {
        return actuatorY;
    }
}
