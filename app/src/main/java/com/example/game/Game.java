package com.example.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Explosion;
import com.example.game.gameobject.Player;
import com.example.game.gamepanel.Background;
import com.example.game.gamepanel.Button;
import com.example.game.gamepanel.Joystick;
import com.example.game.gamepanel.Performance;
import com.example.game.graphics.Animator;
import com.example.game.graphics.SpriteSheet;
import com.example.game.map.Tilemap;

import java.util.ArrayList;
import java.util.List;

public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Player player;
    private final Joystick joystick;

    private final Tilemap tilemap;
    private List<Bomb> bombList;
    private List<Explosion> explosionList;

    private int joystickPointerId = -1;
    private int buttonPointerId = -1;

    private Button button;
    private GameLoop gameLoop;
    private Performance performance;
    private SpriteSheet spriteSheet;
    private Background background;

    public Game(Context context) {
        super(context);

//        setBackground(AppCompatResources.getDrawable(context, R.drawable.background));todo

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

        tilemap = new Tilemap(spriteSheet, 11, 15);

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
                Utils.getControllersOuterCircleRadius(),
                spriteSheet.getBombSprite());

        player = new Player(getContext(), joystick, button, 1, 1, tilemap,
                new Animator(spriteSheet.getGreenPlayerSpriteArray()), bombList, explosionList,
                2, 4);

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
//                    numberOfBombsToCast ++;
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
        Log.d("Game.java", "surfaceCreated()");
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            SurfaceHolder surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            gameLoop = new GameLoop(this, surfaceHolder);
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("Game.java", "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("Game.java", "surfaceDestroyed()");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        background.draw(canvas);

        tilemap.draw(canvas);

        performance.draw(canvas);

        joystick.draw(canvas);
        button.draw(canvas);

        player.draw(canvas);
    }

    public void update() {
        joystick.update();
        button.update();
        player.update();

        List<Bomb> bombRemoveList = new ArrayList<>();
        for (int idx = 0; idx < bombList.size(); ++idx) {
            bombList.get(idx).update(bombRemoveList);
        }
        bombList.removeAll(bombRemoveList);

        List<Explosion> explosionRemoveList = new ArrayList<>();
        for (int idx = 0; idx < explosionList.size(); ++idx) {
            explosionList.get(idx).update(explosionRemoveList);
        }
        explosionList.removeAll(explosionRemoveList);
    }

    public void pause() {
        gameLoop.stopLoop();
    }
}
