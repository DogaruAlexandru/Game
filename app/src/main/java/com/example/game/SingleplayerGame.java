package com.example.game;

import android.content.Context;
import android.os.Bundle;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.Player;
import com.example.game.graphics.Animator;

public class SingleplayerGame extends Game {

    public SingleplayerGame(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context, bundle, gameplayActivity);

        player = new Player(
                joystick,
                button,
                1,
                1,
                tilemap,
                new Animator(spriteSheet.getBluePlayerSpriteArray()),
                bombList,
                explosionList,
                2,
                4,
                4,
                1,
                bundle);
    }

    @Override
    protected void handleGameEnded() {
        //todo
    }

    @Override
    public void update() {
        super.update();

//        if (player.getPlayerData().livesCount > 0 && !enemies.isEmpty())
        player.update();
    }
}