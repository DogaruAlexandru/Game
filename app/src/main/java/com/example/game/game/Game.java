package com.example.game.game;

import static com.example.game.Utils.CRATE_SPAWN_PROBABILITY;
import static com.example.game.Utils.MAP_HEIGHT;
import static com.example.game.Utils.MAP_WIDTH;
import static com.example.game.Utils.PLAYER_ID;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.game.Utils;
import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Explosion;
import com.example.game.gameobject.Player;
import com.example.game.gamepanel.Background;
import com.example.game.gamepanel.Button;
import com.example.game.gamepanel.Joystick;
import com.example.game.gamepanel.Performance;
import com.example.game.graphics.SpriteSheet;
import com.example.game.map.Tilemap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Game extends SurfaceView implements SurfaceHolder.Callback {

    protected final static int SPEED_UPS = 1;
    protected final static int RANGE_UPS = 3;
    protected final static int BOMB_UPS = 1;
    protected final static int LIVES = 3;

    private final static String GAME_CLASS_TAG = "Game.java";
    private final static String SURF_CREATED = "surfaceCreated()";
    private final static String SURF_CHANGED = "surfaceChanged()";
    private final static String SURF_DESTROYED = "surfaceDestroyed()";

    protected Context context;
    protected Player player;
    protected final Joystick joystick;
    protected final Tilemap tilemap;
    protected final String playerId;

    protected ArrayList<Bomb> bombList;
    protected ArrayList<Explosion> explosionList;

    protected int joystickPointerId = -1;
    protected int buttonPointerId = -1;

    protected GameLoop gameLoop;
    protected final Button button;
    protected final Performance performance;
    protected final SpriteSheet spriteSheet;
    protected final Background background;

    protected GameplayActivity gameplayActivity;

    protected Handler handler;
    protected boolean playerCountChanged;

    private void initMe() {
        handler = new Handler();
    }

    public Game(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context);
        initMe();
        this.context = context;

        playerCountChanged = false;

        this.gameplayActivity = gameplayActivity;

        this.playerId = bundle.getString(PLAYER_ID);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Utils.screenHeight = displayMetrics.heightPixels;
        Utils.screenWidth = displayMetrics.widthPixels;

        background = new Background(getContext());

        gameLoop = new GameLoop(this, surfaceHolder);

        performance = new Performance(context, gameLoop);

        spriteSheet = new SpriteSheet(context);

        bombList = new ArrayList<>();
        explosionList = new ArrayList<>();

        tilemap = new Tilemap(
                spriteSheet,
                bundle.getInt(MAP_HEIGHT),
                bundle.getInt(MAP_WIDTH),
                bundle.getInt(CRATE_SPAWN_PROBABILITY));

        joystick = new Joystick(
                getContext(),
                Utils.getJoystickCenterX(),
                Utils.getControllersCenterY(),
                Utils.getControllersOuterCircleRadius(),
                Utils.getControllersInnerCircleRadius());

        button = new Button(
                getContext(),
                Utils.getButtonCenterX(),
                Utils.getControllersCenterY(),
                Utils.getControllersOuterCircleRadius());

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        int pointerIdIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointerId = event.getPointerId(pointerIdIndex);
        float x = event.getX(pointerIdIndex);
        float y = event.getY(pointerIdIndex);
        int pointerCount = event.getPointerCount();

        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (joystick.isPressed(x, y)) {
                    joystickPointerId = pointerId;
                    joystick.setIsPressed(true);
                }
                if (button.isPressed(x, y)) {
                    buttonPointerId = pointerId;
                    button.setIsPressed(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (joystickPointerId == pointerId) {
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                    joystickPointerId = -1;
                } else if (buttonPointerId == pointerId) {
                    button.setIsPressed(false);
                    buttonPointerId = -1;
                }
                break;
        }

        for (int iPointerIndex = 0; iPointerIndex < pointerCount; iPointerIndex++) {
            if (joystick.getIsPressed() && event.getPointerId(iPointerIndex) == joystickPointerId) {
                joystick.setActuator(event.getX(iPointerIndex), event.getY(iPointerIndex));
            }
        }

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(GAME_CLASS_TAG, SURF_CREATED);
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            SurfaceHolder surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            gameLoop = new GameLoop(this, surfaceHolder);
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(GAME_CLASS_TAG, SURF_CHANGED);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(GAME_CLASS_TAG, SURF_DESTROYED);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        background.draw(canvas);

        tilemap.draw(canvas);

        // Draw fps and ups on screed
        // performance.draw(canvas);

        joystick.draw(canvas);
        button.draw(canvas);

        if (player.getLivesCount() > 0) {
            player.draw(canvas);
        }
    }

    public void update() {
        List<Bomb> bombRemoveList = new ArrayList<>();
        for (int idx = 0; idx < bombList.size(); idx++) {
            bombList.get(idx).update(bombRemoveList);
        }
        bombList.removeAll(bombRemoveList);

        List<Explosion> explosionRemoveList = new ArrayList<>();
        for (int idx = 0; idx < explosionList.size(); idx++) {
            explosionList.get(idx).update(explosionRemoveList);
        }
        explosionList.removeAll(explosionRemoveList);

        joystick.update();
        button.update();

        if (playerCountChanged) {
            handleGameEnded();
        }
    }

    public void pause() {
        gameLoop.stopLoop();
    }

    protected void endgameMessage(String msg) {
        // End message
        handler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show());

        // Going back message
        new Thread(this::returnToMenu).start();

        try {
            TimeUnit.MILLISECONDS.sleep(9500);
            gameplayActivity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void returnToMenu() {
        for (int i = 3; i > 0; i--) {
            int finalI = i;
            handler.post(() -> Toast.makeText(context,
                    "Game ended - Going to menu in " + finalI + "...",
                    Toast.LENGTH_SHORT).show());
        }
    }

    protected abstract void handleGameEnded();
}