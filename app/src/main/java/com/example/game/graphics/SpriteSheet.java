package com.example.game.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.game.R;

public class SpriteSheet {

    public static final int PLAYER_FRAMES = 3;
    public static final int SPRITE_WIDTH_PIXELS = 32;
    public static final int SPRITE_HEIGHT_PIXELS = 32;

    private final Bitmap bitmap;

    public SpriteSheet(Context context) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sprite_sheet, bitmapOptions);
    }

    public Sprite[] getBluePlayerSpriteArray() {
        return getPlayerSpriteArray(0);
    }

    public Sprite[] getRedPlayerSpriteArray() {
        return getPlayerSpriteArray(1);
    }

    public Sprite[] getYellowPlayerSpriteArray() {
        return getPlayerSpriteArray(2);
    }

    public Sprite[] getGreenPlayerSpriteArray() {
        return getPlayerSpriteArray(3);
    }

    public Sprite getLightWallSprite() {
        return getSpriteByIndex(0, 3);
    }

    public Sprite getDarkWallSprite() {
        return getSpriteByIndex(1, 3);
    }

    public Sprite getLightWalkSprite() {
        return getSpriteByIndex(2, 3);
    }

    public Sprite getDarkWalkSprite() {
        return getSpriteByIndex(3, 3);
    }

    public Sprite getLightCrateSprite() {
        return getSpriteByIndex(0, 4);
    }

    public Sprite getDarkCrateSprite() {
        return getSpriteByIndex(1, 4);
    }

    public Sprite getBombSprite() {
        return getSpriteByIndex(2, 4);
    }

    public Sprite getExplosionSprite() {
        return getSpriteByIndex(3, 4);
    }

    public Sprite getBombCountPowerUpSprite() {
        return getSpriteByIndex(4, 0);
    }

    public Sprite getExplosionRangePowerUpSprite() {
        return getSpriteByIndex(4, 1);
    }

    public Sprite getSpeedPowerUpSprite() {
        return getSpriteByIndex(4, 2);
    }

    public Sprite getKickPowerUpSprite() {
        return getSpriteByIndex(4, 3);
    }

    public Sprite getHandPowerUpSprite() {
        return getSpriteByIndex(4, 4);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Sprite getSpriteByIndex(int idxRow, int idxCol) {
        return new Sprite(this, new Rect(
                idxCol * SPRITE_WIDTH_PIXELS,
                idxRow * SPRITE_HEIGHT_PIXELS,
                (idxCol + 1) * SPRITE_WIDTH_PIXELS,
                (idxRow + 1) * SPRITE_HEIGHT_PIXELS
        ));
    }

    private Sprite[] getPlayerSpriteArray(int playerNumber) {
        Sprite[] spriteList = new Sprite[SpriteSheet.PLAYER_FRAMES];
        for (int frame = 0; frame < SpriteSheet.PLAYER_FRAMES; ++frame) {
            spriteList[frame] = getSpriteByIndex(playerNumber, frame);
        }
        return spriteList;
    }
}
