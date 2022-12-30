package com.example.game.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.game.R;
import com.example.game.Utils;

public class Joystick {

    private final Paint innerCirclePaint;
    private final Paint outerCirclePaint;

    private final int outerCircleRadius;
    private final int innerCircleRadius;

    private final int outerCircleCenterPosX;
    private final int outerCircleCenterPosY;
    private int innerCircleCenterPosX;
    private int innerCircleCenterPosY;

    private boolean isPressed;
    private double actuatorX;
    private double actuatorY;

    public Joystick(Context context, int centerPositionX, int centerPositionY,
                    int outerCircleRadius, int innerCircleRadius) {

        outerCircleCenterPosX = centerPositionX;
        outerCircleCenterPosY = centerPositionY;
        innerCircleCenterPosX = centerPositionX;
        innerCircleCenterPosY = centerPositionY;

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
                outerCircleCenterPosX,
                outerCircleCenterPosY,
                outerCircleRadius,
                outerCirclePaint);

        canvas.drawCircle(
                innerCircleCenterPosX,
                innerCircleCenterPosY,
                innerCircleRadius,
                innerCirclePaint);
    }

    public void update() {
        updateInnerCirclePosition();
    }

    private void updateInnerCirclePosition() {
        innerCircleCenterPosX = (int) (outerCircleCenterPosX + actuatorX * outerCircleRadius);
        innerCircleCenterPosY = (int) (outerCircleCenterPosY + actuatorY * outerCircleRadius);
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {
        double joystickCenterToTouchDistance = Utils.getDistanceBetweenPoints(
                outerCircleCenterPosX,
                outerCircleCenterPosY,
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
        double deltaX = touchPositionX - outerCircleCenterPosX;
        double deltaY = touchPositionY - outerCircleCenterPosY;
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
