package ianhblakley.goai.mcts;

/**
 * Interface to select nodes for MCTS
 *
 * Created by ian on 10/17/16.
 */
interface TreePolicy {

    /**
     * Selects a Node from a given node for MCTS
     * Returned node is a newly expanded node based on the expansion and selection implementation
     *
     * @param root root node to select from
     * @return new node to simulate from
     */
    Node select(Node root);

    /**
     * Gets the best immediate child of node n
     *
     * @param n  parent node
     * @param cP expansion factor for UCT
     * @return best child node of n
     */
    Node getBestMove(Node n, double cP);
}
