package ianhblakley.goai.mcts;

import ianhblakley.goai.framework.Board;
import ianhblakley.goai.framework.Move;
import ianhblakley.goai.framework.PositionState;

/**
 * Created by ian on 10/17/16.
 */
public class MCTS {

    private static final int ITERATOR = 10;

    private Selector selector;
    private Expander expander;
    private Simulator simulator;
    private MonteCarloTree tree;

    public MCTS(Selector selector, Expander expander, Simulator simulator) {
        this.selector = selector;
        this.expander = expander;
        this.simulator = simulator;
        tree = new MonteCarloTree();
    }

    Move getMove(Board board, PositionState color, int turnNumber) {
        assert tree.checkRootIsBoard(board);
        for (int i=0;i<ITERATOR;i++) {
            runSimulation(board.deepCopy(), color);
        }
        return selectMove(color, turnNumber);
    }

    private void runSimulation(Board board, PositionState color) {
        State selectedState = selector.select(tree, board);
        State expandedState = expander.expand(tree, selectedState);
        PositionState winner = simulator.simulate(tree, expandedState);
        backTrace(expandedState, (winner.equals(color)));
    }

    private void backTrace(State expansionState, boolean won) {
        while (expansionState != null) {
            if (won) { expansionState.logWin(); }
            else { expansionState.logLoss(); }
            expansionState = expansionState.getParent();
        }
    }

    private Move selectMove(PositionState color, int turnNumber) {
        return null;
    }
}
