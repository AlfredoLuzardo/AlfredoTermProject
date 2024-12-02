import java.util.concurrent.CountDownLatch;

/**
 * Interface for game
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public interface Game
{
    /**
     * Play method
     *
     * @param latch is the CountDownLatch
     */
    void play(final CountDownLatch latch);

    /**
     * Initializes the game
     */
    void initializeGame();

    /**
     * Ends the game
     */
    void endGame();
}
