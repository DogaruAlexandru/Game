package com.example.game;

import android.content.Context;
import android.os.Bundle;

import com.example.game.activity.GameplayActivity;
import com.example.game.gameobject.OfflinePlayer;
import com.example.game.graphics.Animator;

public class SingleplayerGame extends Game {

    public SingleplayerGame(Context context, Bundle bundle, GameplayActivity gameplayActivity) {
        super(context, bundle, gameplayActivity);

        player = new OfflinePlayer(
                joystick,
                button,
                1,
                1,
                tilemap,
                new Animator(spriteSheet.getBluePlayerSpriteArray()),
                bombList,
                explosionList,
                1,
                3,
                2,
                3,
                bundle);

        //todo create enemies by the bundle info
    }

    @Override
    protected void handleGameEnded() {
        //todo
    }

    @Override
    public void update() {
        super.update();

//        if (player.getLivesCount() > 0 /*&& !enemies.isEmpty()*/)
            player.update();
    }
}