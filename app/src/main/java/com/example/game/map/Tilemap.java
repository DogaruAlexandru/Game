package com.example.game.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.Utils;
import com.example.game.graphics.SpriteSheet;

public class Tilemap {

    private final SpriteSheet spriteSheet;
    private final int spriteSizeOnScreen;
    private final int mapHeightOffset;
    private final int mapWidthOffset;

    private MapLayout mapLayout;

    private Tile[][] tilemap;

    private boolean tilemapChanged;
    private Bitmap mapBitmap;

    Rect mapRect;
    private int numberOfRowTiles;
    private int numberOfColumnTiles;

    public Tilemap(SpriteSheet spriteSheet, int numberOfRowTiles, int numberOfColumnTiles) {

        this.spriteSheet = spriteSheet;
        this.numberOfRowTiles = numberOfRowTiles;
        this.numberOfColumnTiles = numberOfColumnTiles;
        tilemapChanged = false;

        Utils.spriteSizeOnScreen = Utils.screenHeight / numberOfRowTiles;
        spriteSizeOnScreen = Utils.spriteSizeOnScreen;

        mapLayout = new MapLayout(numberOfRowTiles, numberOfColumnTiles);

        mapHeightOffset = (spriteSizeOnScreen * numberOfRowTiles) >> 1;
        mapWidthOffset = (spriteSizeOnScreen * numberOfColumnTiles) >> 1;
        int screenCenterX = Utils.getScreenCenterX();
        int screenCenterY = Utils.getScreenCenterY();
        mapRect = new Rect(
                screenCenterX - mapWidthOffset,
                screenCenterY - mapHeightOffset,
                screenCenterX + mapWidthOffset,
                screenCenterY + mapHeightOffset);

        initializeTilemap();
    }

    private void initializeTilemap() {
        int[][] layout = mapLayout.getLayout();
        tilemap = new Tile[numberOfRowTiles][numberOfColumnTiles];

        for (int idx1 = 0; idx1 < numberOfRowTiles; ++idx1) {
            for (int idx2 = 0; idx2 < numberOfColumnTiles; ++idx2) {
                tilemap[idx1][idx2] = Tile.getTile(
                        layout[idx1][idx2],
                        spriteSheet,
                        getRectByIndex(idx1, idx2)
                );
            }
        }

        drawTilemapInBitmap();
    }

    public void drawTilemapInBitmap() {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;

        mapBitmap = Bitmap.createBitmap(
                numberOfColumnTiles * (int) spriteSizeOnScreen,
                numberOfRowTiles * (int) spriteSizeOnScreen,
                config
        );

        Canvas mapCanvas = new Canvas(mapBitmap);

        for (int idx1 = 0; idx1 < numberOfRowTiles; ++idx1) {
            for (int idx2 = 0; idx2 < numberOfColumnTiles; ++idx2) {
                tilemap[idx1][idx2].draw(mapCanvas);
            }
        }
    }

    public void changeTile(int idxRow, int idxCol, int layoutTypeIdx) {
        tilemap[idxRow][idxCol] = tilemap[idxRow][idxCol] = Tile.getTile(
                layoutTypeIdx,
                spriteSheet,
                tilemap[idxRow][idxCol].mapLocationRect
        );
    }

    private Rect getRectByIndex(int idxRow, int idxCol) {
        return new Rect(
                (idxCol * spriteSizeOnScreen),
                (idxRow * spriteSizeOnScreen),
                ((idxCol + 1) * spriteSizeOnScreen),
                ((idxRow + 1) * spriteSizeOnScreen)
        );
    }

    public void draw(Canvas canvas) {//todo
        if (tilemapChanged) {
            drawTilemapInBitmap();
            tilemapChanged = false;
        }
        canvas.drawBitmap(mapBitmap, null, mapRect, null);
    }

    public Tile[][] getTilemap() {
        return tilemap;
    }

    public Rect getMapRect() {
        return mapRect;
    }

    public int insideMapLeft() {
        return tilemap[1][1].getMapLocationRect().left;
    }

    public int insideMapRight() {
        return tilemap[numberOfRowTiles - 2][numberOfColumnTiles - 2].
                getMapLocationRect().right;
    }

    public int insideMapTop() {
        return tilemap[1][1].getMapLocationRect().top;
    }

    public int insideMapBottom() {
        return tilemap[numberOfRowTiles - 2][numberOfColumnTiles - 2].
                getMapLocationRect().bottom;
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public boolean isTilemapChanged() {
        return tilemapChanged;
    }

    public void setTilemapChanged(boolean tilemapChanged) {
        this.tilemapChanged = tilemapChanged;
    }
}
