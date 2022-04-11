package com.example.game.gameobject;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.graphics.Animator;
import com.example.game.map.Tilemap;

import java.util.List;

public class AIPlayer extends Player{

    public AIPlayer(int rowTile, int columnTile, Tilemap tilemap, Animator animator,
                    List<Bomb> bombList, List<Explosion> explosionList, int speedUps,
                    int bombRange, int bombsNumber, int livesCount) {
        super(rowTile, columnTile, tilemap, animator, bombList, explosionList,
                speedUps, bombRange, bombsNumber, livesCount);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void update() {

    }

    @Override
    protected void goesDown(Rect newRect) {

    }

    @Override
    protected void goesUp(Rect newRect) {

    }

    @Override
    protected void goesLeft(Rect newRect) {

    }

    @Override
    protected void goesRight(Rect newRect) {

    }
}
