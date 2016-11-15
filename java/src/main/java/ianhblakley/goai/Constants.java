package ianhblakley.goai;

/**
 * Holds constants for the entire program
 *
 * Created by ian on 10/17/16.
 */
public class Constants {

    /**
     * Size of each side of the board
     */
    public static final int BOARD_SIZE = 19;

    /**
     * Total number of threads to run
     */
    public static final int THREAD_COUNT = 3;

    /**
     * Address of the neural network server
     */
    public static final String SERVER_ADDRESS = "localhost";

    /**
     * Port of the neural network server
     */
    public static final int SERVER_PORT = 50051;
    
    /**
     * Time in seconds that MCTS has to find the best move
     */
    public static final int COMPUTE_THRESHOLD = 10;


    /**
     * Whether or not to verify the state of the game between moves
     */
    public static final boolean VERIFY_STATES = true;


}
