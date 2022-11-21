package com.example.game;

import com.example.game.game.GameLoop;
import com.example.game.gameobject.Player;

import java.util.Random;

public class Utils {

    public enum Players {
        PLAYER1,
        PLAYER2,
        PLAYER3,
        PLAYER4
    }

    public static int[] ROWS = {0, -1, 0, 1};
    public static int[] COLUMNS = {1, 0, -1, 0};

    //region Strings
    public final static String NIL = "";
    public final static String FIREBASE_TAG = "Firebase";
    public final static String RETRIEVE_DATA_ERROR = "Error getting data";
    public final static String GAME_STATE = "gameState";
    public final static String STARTING = "starting game";
    public final static String SEED = "seed";
    public final static String PLAYER_ID = "playerId";
    public final static String PLAYER_NAME = "playerName";
    public final static String CODE = "code";
    public final static String MAP_HEIGHT = "mapHeight";
    public final static String MAP_WIDTH = "mapWidth";
    public final static String CRATE_SPAWN_PROBABILITY = "crateSpawnProbability";
    public final static String ENEMY_COUNT = "enemyCount";
    public final static String WIN_END_MSG = "You Won";
    public final static String TIE_END_MSG = "Tie";
    public final static String BLUE_MSG = "[Blue]";
    public final static String RED_MSG = "[Red]";
    public final static String GREEN_MSG = "[Green]";
    public final static String YELLOW_MSG = "[Yellow]";
    //endregion

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
        return (int) (screenHeight * 0.75);
    }

    public static int getJoystickCenterX() {
        return (int) (screenHeight * 0.25);
    }

    public static int getButtonCenterX() {
        return (int) (screenWidth - screenHeight * 0.25);
    }

    public static int getControllersOuterCircleRadius() {
        return (int) (screenHeight * 0.15);
    }

    public static int getControllersInnerCircleRadius() {
        return (int) (screenHeight * 0.07);
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

    public static int getPlayerRow(Player p) {
        return p.getPlayerRect().centerY() / p.getPlayerRect().width();
    }

    public static int getPlayerColumn(Player p) {
        return p.getPlayerRect().centerX() / p.getPlayerRect().height();
    }
}
