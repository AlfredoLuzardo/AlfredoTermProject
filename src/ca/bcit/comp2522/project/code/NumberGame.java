import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class NumberGame
{
    private static final int ROWS = 4;
    private static final int COLS = 5;
    private static final int INITIAL = 0;
    private static final int TOTAL_NUMBERS = 20;

    private final int[][] board;
    private final Random rand;
    private int currentNumber;
    private int numbersPlaced;

    public NumberGame()
    {
        board = new int[ROWS][COLS];
        rand = new Random();
        currentNumber = generateNextNumber();
        numbersPlaced = INITIAL;

    }

    private int generateNextNumber()
    {
        return rand.nextInt(1000) + 1;
    }

    private void initializeBoard()
    {
        for (int r = 0; r < ROWS; r++)
        {
            for (int c = 0; c < COLS; c++)
            {
                board[r][c] = INITIAL;
            }
        }
    }

    public List<Integer> getElementsBefore(int row, int col)
    {
        final List<Integer> valuesBefore;

        valuesBefore = new ArrayList<>();

        for (int r = 0; r <= row; r++)
        {
            for (int c = 0; c < (r == row ? col : 5); c++)
            {
                final int value;
                value = board[r][c];
                valuesBefore.add(value);
            }
        }

        return valuesBefore;
    }

    public List<Integer> getElementsAfter(int row, int col)
    {
        final List<Integer> valuesAfter;

        valuesAfter = new ArrayList<>();

        for (int r = row; r < 4; r++)
        {
            for (int c = (r == row ? col + 1 : 0); c < 5; c++)
            {
                final int value;
                value = board[r][c];
                valuesAfter.add(value);
            }
        }

        return valuesAfter;
    }

    private void handlePlacement(final int row, final int col)
    {
        if(isValidPlacement(row, col, currentNumber))
        {
            board[row][col] = currentNumber;
            numbersPlaced++;
//            if(numbersPlaced == TOTAL_NUMBERS || isGameOver())
//            {
//                System.out.println("Game over! You " +
//                        (numbersPlaced == TOTAL_NUMBERS ? "win!" : "lose."));
//            }
            generateNextNumber();
        }
    }

    private boolean hasValidPlacement(final int number)
    {
        for(int r = 0; r < ROWS; r++)
        {
            for(int c = 0; c < COLS; c++)
            {
                if(isValidPlacement(r, c, number))
                    return true;
            }
        }

        return false;
    }

    private boolean isValidPlacement(final int row, final int col, final int number)
    {
        if(board[row][col] == INITIAL)
        {
            final List<Integer> elementsBefore;
            final List<Integer> elementsAfter;
            final int maxBefore;
            final int minAfter;

            elementsBefore = getElementsBefore(row, col);
            elementsAfter = getElementsAfter(row, col);

            maxBefore = elementsBefore.stream()
                    .filter(e -> e != INITIAL)
                    .max(Integer::compareTo)
                    .orElse(Integer.MIN_VALUE);

            minAfter = elementsAfter.stream()
                    .filter(e -> e != INITIAL)
                    .max(Integer::compareTo)
                    .orElse(Integer.MAX_VALUE);

            return number > maxBefore && number < minAfter;
        }

        return false;
    }

    private void isGameOver()
    {
        if(numbersPlaced == TOTAL_NUMBERS)
        {

        }
    }
}
