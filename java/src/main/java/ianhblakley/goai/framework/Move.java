package ianhblakley.goai.framework;

import java.io.Serializable;

/**
 * Models a move by a player
 * <p>
 * Created by ian on 10/12/16.
 */
public class Move implements Serializable {

    private Position position;
    private PositionState color;
    private boolean pass;

    public Move(Position position, PositionState color, int turn) {
        assert !color.equals(PositionState.EMPTY);
        this.position = position;
        this.color = color;
        pass = false;
    }

    public Move(Position position, PositionState color) {
        this(position, color, 0);
    }

    public Move() {
        pass = true;
    }

    public Position getPosition() {
        return position;
    }

    public PositionState getColor() {
        return color;
    }

    boolean isNotPass() {
        return !pass;
    }
}
