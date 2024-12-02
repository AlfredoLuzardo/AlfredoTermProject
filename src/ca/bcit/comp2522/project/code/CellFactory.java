import java.util.Random;

/**
 * CellFactory class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class CellFactory
{
    private static final int MINE_PROBABILITY_DIV = 5;
    private static final int MINE_PROBABILITY_THRESHOLD = 0;
    private static final Random rand = new Random();

    /**
     * Creates a cell
     *
     * @return a mine cell or empty cell
     */
    public static Cell createCell()
    {
        if(rand.nextInt(MINE_PROBABILITY_DIV) == MINE_PROBABILITY_THRESHOLD)
        {
            return new MineCell();
        }
        else
        {
            return new EmptyCell();
        }
    }
}
