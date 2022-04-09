package com.example.game.gameobject;

import com.example.game.gamepanel.Joystick;
import com.example.game.map.Tilemap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OnlinePlayer extends Player {

    private final DatabaseReference reference;

    public OnlinePlayer(Joystick joystick, com.example.game.gamepanel.Button button, int rowTile,
                        int columnTile, Tilemap tilemap, com.example.game.graphics.Animator animator,
                        List<Bomb> bombList, List<Explosion> explosionList, int speedUps, int bombRange,
                        int bombsNumber, int livesCount, android.os.Bundle bundle) {
        super(joystick, button, rowTile, columnTile, tilemap, animator, bombList, explosionList,
                speedUps, bombRange, bombsNumber, livesCount, bundle);

        reference = FirebaseDatabase.getInstance().getReference(bundle.getString("code"));

        reference.child(playerId).setValue(playerData);
    }

    @Override
    public void update() {
        super.update();

        reference.child(playerId).setValue(playerData);
    }
}
