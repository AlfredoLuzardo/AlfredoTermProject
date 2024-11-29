import javafx.scene.control.Button;

public abstract class BoardGame<T> implements Game
{
    protected abstract void initializeBoard();
    protected abstract void handleButtonClick(final int row, final int col, final Button button);

    protected boolean isValidPlacement(final int row, final int col)
    {
        return true;
    }

    protected boolean isValidPlacement(final int row,
                                       final int col,
                                       final T value)
    {
        return isValidPlacement(row, col);
    }
}
