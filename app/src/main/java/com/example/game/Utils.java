package com.example.game;

import com.example.game.game.GameLoop;

import java.util.Random;

public class Utils {
    public static int screenHeight = 0;
    public static int screenWidth = 0;
    public static int spriteSizeOnScreen = 0;
    public static int mapOffsetX = 0;
    public static int mapOffsetY = 0;
    public static Random generator;

    public static double getDistanceBetweenPoints(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p1x - p2x, 2) + Math.pow(p1y - p2y, 2));
    }

    public static int getControllersCenterY() {
        return (int) (screenHeight * 0.8);
    }

    public static int getJoystickCenterX() {
        return (int) (screenHeight * 0.2);
    }

    public static int getButtonCenterX() {
        return (int) (screenWidth - screenHeight * 0.2);
    }

    public static int getControllersOuterCircleRadius() {
        return (int) (screenHeight * 0.1);
    }

    public static int getControllersInnerCircleRadius() {
        return (int) (screenHeight * 0.05);
    }

    public static int getScreenCenterX() {
        return screenWidth >> 1;
    }

    public static int getScreenCenterY() {
        return screenHeight >> 1;
    }

    public static double getPlayerDefaultMaxSpeed() {
        return 2 * spriteSizeOnScreen / GameLoop.MAX_UPS;
    }
}
