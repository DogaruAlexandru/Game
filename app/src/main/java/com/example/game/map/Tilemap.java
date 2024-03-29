package com.example.game.map;

import static com.example.game.Utils.spriteSizeOnScreen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.game.Utils;
import com.example.game.graphics.SpriteSheet;

public class Tilemap {

    private final SpriteSheet spriteSheet;
    private final int numberOfRowTiles;
    private final int numberOfColumnTiles;

    private Tile[][] tilemap;

    private Bitmap mapBitmap;
    private Canvas mapCanvas;

    Rect mapRect;

    public Tilemap(SpriteSheet spriteSheet, int numberOfRowTiles, int numberOfColumnTiles,
                   int crateSpawnProbability) {

        this.spriteSheet = spriteSheet;
        this.numberOfRowTiles = numberOfRowTiles;
        this.numberOfColumnTiles = numberOfColumnTiles;

        setSpriteSizeOnScreen();

        MapLayout mapLayout = new MapLayout(numberOfRowTiles, numberOfColumnTiles, crateSpawnProbability);

        int mapHeightOffset = (spriteSizeOnScreen * numberOfRowTiles) >> 1;
        int mapWidthOffset = (spriteSizeOnScreen * numberOfColumnTiles) >> 1;
        int screenCenterX = Utils.getScreenCenterX();
        int screenCenterY = Utils.getScreenCenterY();
        mapRect = new Rect(
                screenCenterX - mapWidthOffset,
                screenCenterY - mapHeightOffset,
                screenCenterX + mapWidthOffset,
                screenCenterY + mapHeightOffset);
        Utils.mapOffsetX = mapRect.left;
        Utils.mapOffsetY = mapRect.top;

        initializeTilemap(mapLayout.getLayout());
    }

    private void setSpriteSizeOnScreen() {
        int height = Utils.screenHeight / numberOfRowTiles;
        int width = (int) (Utils.screenWidth * 0.9 / numberOfColumnTiles);

        spriteSizeOnScreen = Math.min(height, width);
    }

    private void initializeTilemap(int[][] layout) {
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
                numberOfColumnTiles * spriteSizeOnScreen,
                numberOfRowTiles * spriteSizeOnScreen,
                config
        );

        mapCanvas = new Canvas(mapBitmap);

        for (int idx1 = 0; idx1 < numberOfRowTiles; idx1++) {
            for (int idx2 = 0; idx2 < numberOfColumnTiles; idx2++) {
                tilemap[idx1][idx2].draw(mapCanvas);
            }
        }
    }

    public void changeTile(int idxRow, int idxCol, int layoutTypeIdx) {
        tilemap[idxRow][idxCol] = Tile.getTile(
                layoutTypeIdx,
                spriteSheet,
                tilemap[idxRow][idxCol].mapLocationRect
        );

        tilemap[idxRow][idxCol].draw(mapCanvas);
    }

    private Rect getRectByIndex(int idxRow, int idxCol) {
        return new Rect(
                (idxCol * spriteSizeOnScreen),
                (idxRow * spriteSizeOnScreen),
                ((idxCol + 1) * spriteSizeOnScreen),
                ((idxRow + 1) * spriteSizeOnScreen)
        );
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mapBitmap, null, mapRect, null);
    }

    public Tile[][] getTilemap() {
        return tilemap;
    }

    public Rect getMapRect() {
        return mapRect;
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public int getNumberOfRowTiles() {
        return numberOfRowTiles;
    }

    public int getNumberOfColumnTiles() {
        return numberOfColumnTiles;
    }
}
