package com.example.game.activity;

import com.example.game.SingleplayerGame;
import com.example.game.Utils;

import java.util.Random;

public class SingleplayerGameplayActivity extends GameplayActivity {

    @Override
    void instantiateGame() {
        Utils.generator = new Random();

        game = new SingleplayerGame(this, bundle, this);
        setContentView(game);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
