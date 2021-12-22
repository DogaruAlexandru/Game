package com.example.game.map;

import com.example.game.Utils;

public class MapLayout {
    //    private static final float CRATE_SPAWN_PROBABILITY = 0.85f;todo
    private static final float CRATE_SPAWN_PROBABILITY = 0.3f;
    public int numberOfRowTiles;
    public int numberOfColumnTiles;

    private int[][] layout;

    public MapLayout(int numberOfRowTiles, int numberOfColumnTiles) {
        this.numberOfRowTiles = numberOfRowTiles;
        this.numberOfColumnTiles = numberOfColumnTiles;
        initializeLayout();
    }

    public int[][] getLayout() {
        return layout;
    }

    private void initializeLayout() {
        layout = new int[numberOfRowTiles][numberOfColumnTiles];

        //set border
        for (int idx = 0; idx < numberOfRowTiles; ++idx) {
            layout[idx][0] = 2;
            layout[idx][numberOfColumnTiles - 1] = 2;
        }
        for (int idx = 1; idx < numberOfColumnTiles - 1; ++idx) {
            layout[0][idx] = 2;
            layout[numberOfRowTiles - 1][idx] = 2;
        }

        //set content
//        for (int idx = 1; idx < numberOfRowTiles - 1; ++idx) {
//            for (int idy = 1; idy < numberOfColumnTiles - 1; ++idy) {
//                layout[idx][idy] = 1;
//            }
//        }
        for (int idx1 = 1; idx1 < numberOfRowTiles - 1; ++idx1) {
            for (int idx2 = 1; idx2 < numberOfColumnTiles - 1; ++idx2) {
                //add obstacles
                if (idx1 % 2 == 0 && idx2 % 2 == 0)
                    layout[idx1][idx2] = 2;
                    //add crates
                else {
                    if (getRandomBoolean(CRATE_SPAWN_PROBABILITY)) {
                        layout[idx1][idx2] = 3;
                        continue;
                    }
                    layout[idx1][idx2] = 1;
                }
            }
        }

        //free space for players spawn
        layout[1][1] = 1;
        layout[2][1] = 1;
        layout[1][2] = 1;

        layout[1][numberOfColumnTiles - 2] = 1;
        layout[2][numberOfColumnTiles - 2] = 1;
        layout[1][numberOfColumnTiles - 3] = 1;

        layout[numberOfRowTiles - 2][1] = 1;
        layout[numberOfRowTiles - 3][1] = 1;
        layout[numberOfRowTiles - 2][2] = 1;

        layout[numberOfRowTiles - 2][numberOfColumnTiles - 2] = 1;
        layout[numberOfRowTiles - 3][numberOfColumnTiles - 2] = 1;
        layout[numberOfRowTiles - 2][numberOfColumnTiles - 3] = 1;
    }

    public boolean getRandomBoolean(float p) {
        return Utils.RANDOM.nextFloat() < p;
    }
}