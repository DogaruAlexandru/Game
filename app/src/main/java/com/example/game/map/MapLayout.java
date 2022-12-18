package com.example.game.map;

import com.example.game.Utils;

public class MapLayout {

    static final int WALK_TILE_LAYOUT_ID = 1;
    static final int CRATE_TILE_LAYOUT_ID = 3;
    static final int WALL_TILE_LAYOUT_ID = 2;

    private final float crateSpawnProbability;
    private final int numberOfRowTiles;
    private final int numberOfColumnTiles;

    private int[][] layout;

    public MapLayout(int numberOfRowTiles, int numberOfColumnTiles, int crateSpawnProbability) {
        this.numberOfRowTiles = numberOfRowTiles;
        this.numberOfColumnTiles = numberOfColumnTiles;
        this.crateSpawnProbability = crateSpawnProbability * 0.01f;
        initializeLayout();
    }

    public int[][] getLayout() {
        return layout;
    }

    private void initializeLayout() {
        layout = new int[numberOfRowTiles][numberOfColumnTiles];

        //set border
        for (int idx = 0; idx < numberOfRowTiles; idx++) {
            layout[idx][0] = WALL_TILE_LAYOUT_ID;
            layout[idx][numberOfColumnTiles - 1] = WALL_TILE_LAYOUT_ID;
        }
        for (int idx = 1; idx < numberOfColumnTiles - 1; idx++) {
            layout[0][idx] = WALL_TILE_LAYOUT_ID;
            layout[numberOfRowTiles - 1][idx] = WALL_TILE_LAYOUT_ID;
        }

        //set content
        for (int idx1 = 1; idx1 < numberOfRowTiles - 1; idx1++) {
            for (int idx2 = 1; idx2 < numberOfColumnTiles - 1; idx2++) {
                //add obstacles
                if (idx1 % 2 == 0 && idx2 % 2 == 0) {
                    layout[idx1][idx2] = WALL_TILE_LAYOUT_ID;
                }
                //add crates
                else {
                    if (getRandomBoolean(crateSpawnProbability)) {
                        layout[idx1][idx2] = CRATE_TILE_LAYOUT_ID;
                        continue;
                    }
                    layout[idx1][idx2] = WALK_TILE_LAYOUT_ID;
                }
            }
        }

        //free space for players spawn
        layout[1][1] = WALK_TILE_LAYOUT_ID;
        layout[2][1] = WALK_TILE_LAYOUT_ID;
        layout[1][2] = WALK_TILE_LAYOUT_ID;

        layout[1][numberOfColumnTiles - 2] = WALK_TILE_LAYOUT_ID;
        layout[2][numberOfColumnTiles - 2] = WALK_TILE_LAYOUT_ID;
        layout[1][numberOfColumnTiles - 3] = WALK_TILE_LAYOUT_ID;

        layout[numberOfRowTiles - 2][1] = WALK_TILE_LAYOUT_ID;
        layout[numberOfRowTiles - 3][1] = WALK_TILE_LAYOUT_ID;
        layout[numberOfRowTiles - 2][2] = WALK_TILE_LAYOUT_ID;

        layout[numberOfRowTiles - 2][numberOfColumnTiles - 2] = WALK_TILE_LAYOUT_ID;
        layout[numberOfRowTiles - 3][numberOfColumnTiles - 2] = WALK_TILE_LAYOUT_ID;
        layout[numberOfRowTiles - 2][numberOfColumnTiles - 3] = WALK_TILE_LAYOUT_ID;
    }

    public boolean getRandomBoolean(float p) {
        return Utils.generator.nextFloat() < p;
    }
}