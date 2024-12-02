import javafx.application.Platform;
import javafx.scene.control.Button;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TestClass
 */
public class UltimateTicTacSweeperTest
{
    private UltimateTicTacSweeper game1;
    private UltimateTicTacSweeper game2;

    @BeforeAll
    static void initJavaFX() throws Exception
    {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void setUp()
    {
        game1 = new UltimateTicTacSweeper();
        game1.initializeGame();

        game2 = new UltimateTicTacSweeper();
        game2.initializeGame();
    }

    @AfterEach
    void tearDown()
    {
        game1 = null;
        game2 = null;
    }

    @Test
    public void testCheckWinOnEmptyBoard()
    {
        assertNull(game1.checkWin());
    }

    @Test
    public void testHorizontalWinCondition()
    {
        game1.getBoard()[0][0] = "X";
        game1.getBoard()[0][1] = "X";
        game1.getBoard()[0][2] = "X";

        assertNotNull(game1.checkWin());
    }

    @Test
    public void testVerticalWinCondition()
    {
        game1.getBoard()[0][0] = "X";
        game1.getBoard()[1][0] = "X";
        game1.getBoard()[2][0] = "X";

        assertNotNull(game1.checkWin());
    }

    @Test
    public void testDiagonalWinCondition()
    {
        game1.getBoard()[0][0] = "X";
        game1.getBoard()[1][1] = "X";
        game1.getBoard()[2][2] = "X";

        assertEquals("X", game1.checkWin());
    }

    @Test
    public void testAntiDiagonalWinCondition()
    {
        game1.getBoard()[0][2] = "O";
        game1.getBoard()[1][1] = "O";
        game1.getBoard()[2][0] = "O";

        assertEquals("O", game1.checkWin());
    }

    @Test
    public void testDrawCondition()
    {
        game1.getBoard()[0][0] = "X";
        game1.getBoard()[0][1] = "O";
        game1.getBoard()[0][2] = "O";
        game1.getBoard()[1][0] = "O";
        game1.getBoard()[1][1] = "X";
        game1.getBoard()[1][2] = "X";
        game1.getBoard()[2][0] = "X";
        game1.getBoard()[2][1] = "X";
        game1.getBoard()[2][2] = "O";

        assertNull(game1.checkWin());
        assertTrue(game1.isBoardFull());
    }

    @Test
    public void testInitializeBoard()
    {
        final String[][] board;

        board = game1.getBoard();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertNull(board[i][j]);
            }
        }
    }

    @Test
    public void testEmptyBoardIsNotFull()
    {
        final String[][] board;
        int nonNullCells;

        board = game1.getBoard();
        nonNullCells = 0;

        for (int r = 0; r < 3; r++)
        {
            for (int c = 0; c < 3; c++)
            {
                if (board[r][c] != null)
                {
                    nonNullCells++;
                }
            }
        }
        assertEquals(0, nonNullCells);
    }

    @Test
    public void testPlayerSwitch()
    {
        assertEquals("X", game1.getCurrentPlayer());

        game1.switchPlayer();
        assertEquals("O", game1.getCurrentPlayer());

        game1.switchPlayer();
        assertEquals("X", game1.getCurrentPlayer());
    }

    @Test
    public void testSwitchPlayerMultipleTimes()
    {
        assertEquals("X", game1.getCurrentPlayer());

        for (int i = 0; i < 5; i++)
        {
            game1.switchPlayer();
        }

        assertEquals("O", game1.getCurrentPlayer());
    }

    @Test
    public void testOutOfBoundsMoveThrowsException()
    {
        assertThrows(IndexOutOfBoundsException.class, () ->
        {
            final Button button;
            button = new Button();
            game1.handleButtonClick(4, 4, button); // Invalid index
        });

        assertThrows(IndexOutOfBoundsException.class, () ->
        {
            final Button button;
            button = new Button();
            game2.handleButtonClick(-1, 4, button); // Invalid index
        });
    }
}