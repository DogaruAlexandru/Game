package com.example.game.map;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.gameobject.Explosion;
import com.example.game.graphics.Sprite;
import com.example.game.graphics.SpriteSheet;

public class ExplosionTile extends Tile {
    private final Sprite walkSprite;
    private final Sprite explosionSprite;
    private Explosion explosion;

    public ExplosionTile(SpriteSheet spriteSheet, Rect mapLocationRect) {
        super(mapLocationRect, LayoutType.EXPLOSION);
        explosionSprite = spriteSheet.getExplosionSprite();
        walkSprite = spriteSheet.getLightWalkSprite();
        explosion = null;
    }

    @Override
    public void draw(Canvas canvas) {
        walkSprite.draw(canvas, mapLocationRect);
        explosionSprite.draw(canvas, mapLocationRect);
    }

    public Explosion getExplosion() {
        return explosion;
    }

    public void setExplosion(Explosion explosion) {
        this.explosion = explosion;
    }
}
