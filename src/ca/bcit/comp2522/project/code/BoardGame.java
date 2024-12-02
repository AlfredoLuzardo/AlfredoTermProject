import javafx.scene.control.Button;

/**
 * Abstract BoardGame class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 *
 * @param <T> is the value used in isValidPlacement
 */
public abstract class BoardGame<T> implements Game
{
    /**
     * Initializes the boardGame board
     */
    public abstract void initializeBoard();

    /**
     * Handles a click on a button
     *
     * @param row is the row
     * @param col is the col
     * @param button is the button
     */
    public abstract void handleButtonClick(final int row,
                                           final int col,
                                           final Button button);

    /**
     * Returns whether the coordinates are a valid placement
     *
     * @param row is the row
     * @param col is the col
     * @return true
     */
    public boolean isValidPlacement(final int row, final int col)
    {
        return true;
    }

    /**
     * Returns whether the coordinates are a valid placement
     *
     * @param row is the row
     * @param col is the col
     * @param value is the value
     * @return isValidPlacement(row, col)
     */
    public boolean isValidPlacement(final int row,
                                    final int col,
                                    final T value)
    {
        return isValidPlacement(row, col);
    }
}
