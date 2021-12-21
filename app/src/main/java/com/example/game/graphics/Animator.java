package com.example.game.graphics;

import static com.example.game.GameLoop.MAX_UPS;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.example.game.Utils;
import com.example.game.gameobject.Player;

public class Animator {
    private final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = (int) (MAX_UPS * 0.1);
    private final Sprite[] playerSpriteArray;

    private int idxMovingFrame = 1;
    private boolean oddTimeAction = true;
    private int updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;

    public Animator(Sprite[] playerSpriteArray) {
        this.playerSpriteArray = playerSpriteArray;
    }

    public void draw(Canvas canvas, Player player) {
        int idxNotMovingFrame = 0;
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
                playerSpriteArray[idxNotMovingFrame].draw(canvas, player.getPlayerRect(),
                        player.getRotationAngle());
                break;
            case STARTED_MOVING:
                updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
                playerSpriteArray[idxMovingFrame].draw(canvas, player.getPlayerRect(),
                        player.getRotationAngle());
                break;
            case IS_MOVING:
                --updatesBeforeNextMoveFrame;
                if (updatesBeforeNextMoveFrame == 0) {
                    updatesBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
                    toggleIdxMovingFrame();
                }
                playerSpriteArray[idxMovingFrame].draw(canvas, player.getPlayerRect(),
                        player.getRotationAngle());
                break;
            default:
                break;
        }
    }

    private void toggleIdxMovingFrame() {
        switch (idxMovingFrame) {
            case 0:
                if (oddTimeAction) {
                    idxMovingFrame = 2;
                    oddTimeAction = false;
                } else {
                    idxMovingFrame = 1;
                    oddTimeAction = true;
                }
                break;
            case 1:
            case 2:
                idxMovingFrame = 0;
                break;
        }
    }
}