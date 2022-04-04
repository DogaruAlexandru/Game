package com.example.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.Bomb;
import com.example.game.gameobject.Enemy;
import com.example.game.gameobject.Explosion;
import com.example.game.gameobject.Player;
import com.example.game.gamepanel.Background;
import com.example.game.gamepanel.Button;
import com.example.game.gamepanel.Joystick;
import com.example.game.gamepanel.Performance;
import com.example.game.graphics.Animator;
import com.example.game.graphics.SpriteSheet;
import com.example.game.map.Tilemap;
import com.example.game.model.PlayerData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Player player;
    private final Joystick joystick;
    private final Tilemap tilemap;
    private final String playerId;

    private ArrayList<Bomb> bombList;
    private ArrayList<Explosion> explosionList;
    private ArrayList<Enemy> enemies;

    private int joystickPointerId = -1;
    private int buttonPointerId = -1;

    private Button button;
    private GameLoop gameLoop;
    private Performance performance;
    private SpriteSheet spriteSheet;
    private Background background;

    private DatabaseReference reference;
    private GameplayActivity gameplayActivity;

    public Game(Context context, int mapHeight, int mapWidth, int crateSpawnProbability,
                Bundle bundle, GameplayActivity gameplayActivity) {
        super(context);

//        setBackground(AppCompatResources.getDrawable(context, R.drawable.background));todo

        this.gameplayActivity = gameplayActivity;

        this.playerId = bundle.getString("playerId");
        enemies = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(bundle.getString("code"));

        addEnemiesListeners();

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
                mapHeight,
                mapWidth,
                crateSpawnProbability);

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

        int rowTile = 0;
        int columnTile = 0;
        switch (playerId) {
            case "player1":
                rowTile = 1;
                columnTile = 1;
                break;
            case "player2":
                rowTile = tilemap.getNumberOfRowTiles() - 2;
                columnTile = tilemap.getNumberOfColumnTiles() - 2;
                break;
            case "player3":
                rowTile = 1;
                columnTile = tilemap.getNumberOfColumnTiles() - 2;
                break;
            case "player4":
                rowTile = tilemap.getNumberOfRowTiles() - 2;
                columnTile = 1;
                break;
        }

        Animator animator = null;
        switch (playerId) {
            case "player1":
                animator = new Animator(spriteSheet.getBluePlayerSpriteArray());
                break;
            case "player2":
                animator = new Animator(spriteSheet.getRedPlayerSpriteArray());
                break;
            case "player3":
                animator = new Animator(spriteSheet.getGreenPlayerSpriteArray());
                break;
            case "player4":
                animator = new Animator(spriteSheet.getYellowPlayerSpriteArray());
                break;
        }
        player = new Player(
                joystick,
                button,
                rowTile,
                columnTile,
                tilemap,
                animator,
                bombList,
                explosionList,
                2,
                4,
                4,
                1,
                bundle);

        setFocusable(true);
    }

    private void addEnemiesListeners() {
        reference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                DataSnapshot dataSnapshot = task.getResult();

                ArrayList<String> enemiesIdList = new ArrayList<>(Arrays.
                        asList("player1", "player2", "player3", "player4"));
                enemiesIdList.remove(playerId);
                for (String id : enemiesIdList) {
                    if (dataSnapshot.child(id).exists()) {
                        createEnemy(id);
                    }
                }
            }
        });
    }

    private void createEnemy(String id) {
        Enemy enemy = new Enemy();
        enemy.setPlayerId(id);
        enemy.setTilemap(tilemap);
        switch (id) {
            case "player1":
                enemy.setAnimator(new Animator(spriteSheet.
                        getBluePlayerSpriteArray()));
                break;
            case "player2":
                enemy.setAnimator(new Animator(spriteSheet.
                        getRedPlayerSpriteArray()));
                break;
            case "player3":
                enemy.setAnimator(new Animator(spriteSheet.
                        getGreenPlayerSpriteArray()));
                break;
            case "player4":
                enemy.setAnimator(new Animator(spriteSheet.
                        getYellowPlayerSpriteArray()));
                break;
        }
        createListener(enemy);
        enemy.setBombList(bombList);
        enemy.setExplosionList(explosionList);
        enemies.add(enemy);
    }

    private void createListener(Enemy enemy) {
        enemy.setListener(reference.child(enemy.getPlayerId()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        enemy.setPlayerData(dataSnapshot.getValue(PlayerData.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("onCancelled", "failed", databaseError.toException());
                    }
                }));
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

//        performance.draw(canvas);

        joystick.draw(canvas);
        button.draw(canvas);

        if (player.getPlayerData().livesCount > 0)
            player.draw(canvas);

        for (Enemy enemy : enemies) {
            enemy.draw(canvas);
        }
    }

    public void update() {
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

        joystick.update();
        button.update();

        if (player.getPlayerData().livesCount > 0 && !enemies.isEmpty())
            player.update();

        for (Iterator iterator = enemies.iterator(); iterator.hasNext(); ) {
            Enemy enemy = (Enemy) iterator.next();
            enemy.update();
            if (enemy.getPlayerData().livesCount != 0)
                continue;
            reference.removeEventListener(enemy.getListener());
            reference.child(enemy.getPlayerId()).removeValue();
            iterator.remove();
        }

//        handleGameEnded();todo
    }

    public void pause() {
        gameLoop.stopLoop();
    }

    public boolean canLeave() {
        return player.getLivesCount() == 0 || enemies.isEmpty();//todo bug you can leave if ypu are fast
    }

    public void handleGameEnded() {
        if (player.getPlayerData().livesCount > 0) {
            if (enemies.isEmpty()) {
                gameplayActivity.makeShortToast("You Won.");
            }
        } else {
            switch (enemies.size()) {
                case 0:
                    gameplayActivity.makeShortToast("Tie");
                    break;
                case 1:
                    String color = null;
                    switch (enemies.get(0).getPlayerId()) {
                        case "player1":
                            color = "[blue]";
                            break;
                        case "player2":
                            color = "[red]";
                            break;
                        case "player3":
                            color = "[green]";
                            break;
                        case "player4":
                            color = "[yellow]";
                            break;
                    }
                    gameplayActivity.makeShortToast(enemies.get(0).
                            getPlayerData().playerName + " " + color + " Won.");
                    reference.removeEventListener(enemies.get(0).getListener());
                    reference.child(enemies.get(0).getPlayerId()).removeValue();
                    enemies.remove(0);
                    break;
                default:
                    break;
            }
        }
    }

    public void removeServer() {
        if (enemies.size() == 0)
            reference.removeValue();
    }
}
