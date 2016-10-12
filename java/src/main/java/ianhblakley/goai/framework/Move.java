package ianhblakley.goai.framework;

/**
 * Models a move by a player
 * <p>
 * Created by ian on 10/12/16.
 */
public class Move {

    private Position position;
    private PositionState color;
    private int turn;
    private boolean pass;

    public Move(Position position, PositionState color, int turn) {
        assert !color.equals(PositionState.EMPTY);
        this.position = position;
        this.color = color;
        this.turn = turn;
        pass = false;
    }

    public Move() {
        pass = true;
    }

    Position getPosition() {
        return position;
    }

    PositionState getColor() {
        return color;
    }

    boolean isPass() {
        return pass;
    }
}
