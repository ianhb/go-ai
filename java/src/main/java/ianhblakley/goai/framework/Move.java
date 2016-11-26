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

    public Move(Position position, PositionState color) {
        assert !color.equals(PositionState.EMPTY);
        this.position = position;
        this.color = color;
        pass = false;
    }

    /**
     * Move representing a pass
     */
    public Move(PositionState color) {
        pass = true;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public PositionState getColor() {
        return color;
    }

    public boolean isNotPass() {
        return !pass;
    }
}
