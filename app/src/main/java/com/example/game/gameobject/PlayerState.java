package com.example.game.gameobject;

public class PlayerState {
    public enum State {
        NOT_MOVING,
        STARTED_MOVING,
        IS_MOVING
    }

    private Player player;
    private State state;

    public PlayerState(Player player) {
        this.player = player;
        this.state = State.NOT_MOVING;
    }

    public State getState() {
        return state;
    }

    public void update() {
        switch (state) {
            case NOT_MOVING:
                if (player.getVelocityX() != 0 || player.getVelocityY() != 0)
                    state = State.STARTED_MOVING;
                break;
            case STARTED_MOVING:
                if (player.getVelocityX() != 0 || player.getVelocityY() != 0)
                    state = State.IS_MOVING;
                break;
            case IS_MOVING:
                if (player.getVelocityX() == 0 && player.getVelocityY() == 0)
                    state = State.NOT_MOVING;
                break;
            default:
                break;
        }
    }

    public static State getStringToEnum(String s) {
        if (s == null)
            return PlayerState.State.NOT_MOVING;
        switch (s) {
            case "STARTED_MOVING":
                return PlayerState.State.STARTED_MOVING;
            case "IS_MOVING":
                return PlayerState.State.IS_MOVING;
            default:
                return PlayerState.State.NOT_MOVING;
        }
    }

    public static String getEnumToString(State s) {
        switch (s) {
            case NOT_MOVING:
                return "NOT_MOVING";
            case IS_MOVING:
                return "IS_MOVING";
            case STARTED_MOVING:
                return "STARTED_MOVING";
        }
        return null;
    }
}
