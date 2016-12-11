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
    public static final int THREAD_COUNT = 4;

    /**
     * Address of the neural network server
     */
    public static final String NEURAL_SERVER_ADDRESS = "localhost";

    /**
     * Port of the value neural network server
     */
    public static final int VALUE_PORT = 9000;

    /**
     * Port of the move neural network server
     */
    public static final int MOVE_PORT = 9001;

    /**
     * Address of the neural network server
     */
    public static final String LOGGER_SERVER_ADDRESS = "localhost";

    /**
     * Port of the neural network server
     */
    public static final int LOGGER_SERVER_PORT = 50050;

    /**
     * Time in seconds that MCTS has to find the best move
     */
    public static final int COMPUTE_THRESHOLD = 15;

    /**
     * Whether or not to verify the state of the game between moves
     */
    public static final boolean VERIFY_STATES = true;
    public static final int ALLOW_PASS_COUNT = 200;
    public static final int RESIGN_THRESHOLD = 250;
    static final boolean LOG_GAMES = false;
}
