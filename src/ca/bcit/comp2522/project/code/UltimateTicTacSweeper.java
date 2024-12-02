import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * UltimateTicTacSweeper class
 *
 * @author Alfredo Luzardo
 * @version 1,0
 */
public class UltimateTicTacSweeper extends BoardGame<Void>
{
    private static final String STYLE_SHEET_PATH            = "tictacsweeper.css";
    private static final String PLAYER_ONE                  = "X";
    private static final String PLAYER_TWO                  = "O";
    private static final String GAME_TITLE                  = "Ultimate TicTac Sweeper";
    private static final String BTN_INITIAL_TXT             = "Start Game";
    private static final String BTN_DRAW_TXT                = "Draw";
    private static final String PLAYER_TURN_LABEL           = "Player Turn: ";
    private static final String END_ALERT_TITLE             = "Game Over";
    private static final String END_ALERT_DRAW_TXT          = "Draw!";
    private static final String BTN_PLAY_AGAIN_TXT          = "Play Again";
    private static final String BTN_QUIT_TXT                = "Quit";
    private static final String ROWS_COLS_EXCEPTION_MSG     = "Row or column is out of bounds";
    private static final int ROWS_LOWER_BOUND   = 0;
    private static final int COLS_LOWER_BOUND   = 0;
    private static final int ROWS               = 3;
    private static final int COLS               = 3;
    private static final int SCENE_WIDTH_PX     = 800;
    private static final int SCENE_HEIGHT_PX    = 600;
    private static final int LINE_COUNT         = 3;
    private static final int FIRST_POSITION     = 0;
    private static final int SECOND_POSITION    = 1;
    private static final int THIRD_POSITION     = 2;
    private static final GridPane grid;
    private static final String[][] board;
    private static final Label playerTurnLabel;
    private static Stage primaryStage;
    private static String currentPlayer;

    private CountDownLatch latch;

    static
    {
        grid            = new GridPane();
        currentPlayer   = PLAYER_ONE;
        playerTurnLabel = new Label(PLAYER_TURN_LABEL + currentPlayer);
        board           = new String[ROWS][COLS];
    }

    /**
     * Getter for board
     *
     * @return board
     */
    public String[][] getBoard()
    {
        return board;
    }

    /**
     * Getter for currentPlayer
     *
     * @return currentPlayer
     */
    public String getCurrentPlayer()
    {
        return currentPlayer;
    }

    /**
     * Play method
     *
     * @param latch is the CountDownLatch
     */
    public void play(final CountDownLatch latch)
    {
        final Scene scene;
        final BorderPane root;

        this.latch   = latch;
        primaryStage = new Stage();

        grid.setMaxWidth(Double.MAX_VALUE);

        root = new BorderPane();
        root.setTop(playerTurnLabel);
        root.setCenter(grid);

        scene = new Scene(root, SCENE_WIDTH_PX, SCENE_HEIGHT_PX);
        scene.getStylesheets().add(getClass().getResource(STYLE_SHEET_PATH).toExternalForm());

        initializeGame();

        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle(GAME_TITLE);
        primaryStage.showAndWait();
    }

    /**
     * initializeGame method
     * <p>
     * Initializes UltimateTicTacSweeper
     */
    @Override
    public void initializeGame()
    {
        initializeBoard();
        grid.getChildren().clear();
        initializeGrid();
        currentPlayer = PLAYER_ONE;
        playerTurnLabel.setText(PLAYER_TURN_LABEL + currentPlayer);
    }

    /**
     * initializeBoard method
     * <p>
     * initializes the game board
     */
    @Override
    public void initializeBoard()
    {
        Arrays.stream(board).forEach(row -> Arrays.fill(row, null));
    }

    /*
     * Closes the game
     */
    private void closeGame()
    {
        latch.countDown();
    }

    /*
     * initializeGrid method
     * <p>
     * Initializes the grid
     */
    private void initializeGrid()
    {
        for (int r = 0; r < ROWS; r++)
        {
            for (int c = 0; c < COLS; c++)
            {
                final Button b;
                final int row;
                final int col;

                row = r;
                col = c;

                b = new Button(BTN_INITIAL_TXT);

                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(e -> handleButtonClick(row, col, b));
                GridPane.setHgrow(b, Priority.ALWAYS);

                grid.add(b, c, r);
            }
        }
    }

    /**
     * handleButtonClick method
     * <p>
     * Handles the logic behind a button being clicked
     *
     * @param row is the row
     * @param col is the col
     * @param b is the button
     */
    @Override
    public void handleButtonClick(final int row,
                                  final int col,
                                  final Button b)
    {
        final TicTacSweeper game;
        final String winner;
        final String otherPlayer;

        validateRowsAndCols(row, col);

        disableBoard();
        primaryStage.setAlwaysOnTop(false);

        if(currentPlayer.equals(PLAYER_ONE))
        {
            otherPlayer = PLAYER_TWO;
        }
        else
        {
            otherPlayer = PLAYER_ONE;
        }

        game = new TicTacSweeper(currentPlayer, otherPlayer);
        game.play(latch);
        winner = game.getWinner();

        primaryStage.setAlwaysOnTop(true);
        b.setText(winner != null ? winner : BTN_DRAW_TXT);

        board[row][col] = winner;

        if(checkWin() != null)
        {
            endGame();
        }
        else if(isBoardFull())
        {
            endGame();
        }
        else
        {
            switchPlayer();
            playerTurnLabel.setText(PLAYER_TURN_LABEL + currentPlayer);
            enableBoard();
        }
    }

    /**
     * Ends the game
     */
    @Override
    public void endGame()
    {
        final Alert alert;
        final ButtonType playAgain;
        final ButtonType quit;
        final Optional<ButtonType> result;

        primaryStage.setAlwaysOnTop(false);

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(END_ALERT_TITLE);

        if(checkWin() != null)
        {
            alert.setContentText("Player " + checkWin() + " won!");
        }
        else
        {
            alert.setContentText(END_ALERT_DRAW_TXT);
        }

        playAgain = new ButtonType(BTN_PLAY_AGAIN_TXT);
        quit      = new ButtonType(BTN_QUIT_TXT);
        alert.getButtonTypes().setAll(playAgain, quit);

        result = alert.showAndWait();

        if(result.isPresent() && result.get() == playAgain)
        {
            primaryStage.setAlwaysOnTop(true);
            initializeGame();
        }
        else
        {
            final Stage stage;
            stage = (Stage) playerTurnLabel.getScene().getWindow();
            stage.close();
            closeGame();
        }
    }

    /*
     * Disables the entire board
     */
    private void disableBoard()
    {
        if(!grid.getChildren().isEmpty())
        {
            for(final Node node : grid.getChildren())
            {
                if(node != null)
                {
                    if(node instanceof Button button)
                    {
                        button.setDisable(true);
                    }
                }
            }
        }
    }

    /*
     * Enables the entire board
     */
    private void enableBoard()
    {
        if(!grid.getChildren().isEmpty())
        {
            for(final Node node : grid.getChildren())
            {
                if(node != null)
                {
                    if(node instanceof Button button)
                    {
                        if(button.getText().equals(BTN_INITIAL_TXT))
                        {
                            button.setDisable(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Switches the current player
     */
    public void switchPlayer()
    {
        currentPlayer = currentPlayer.equals(PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;
    }

    /**
     * Checks if there is a win
     */
    public String checkWin()
    {

        for (int i = 0; i < LINE_COUNT; i++)
        {
            if (checkLine(board[i][FIRST_POSITION], board[i][SECOND_POSITION], board[i][THIRD_POSITION]))
            {
                return board[i][FIRST_POSITION];
            }
            if (checkLine(board[FIRST_POSITION][i], board[SECOND_POSITION][i], board[THIRD_POSITION][i]))
            {
                return board[FIRST_POSITION][i];
            }
        }


        if(checkLine(board[FIRST_POSITION][FIRST_POSITION],
                      board[SECOND_POSITION][SECOND_POSITION],
                      board[THIRD_POSITION][THIRD_POSITION]))
        {
            return board[FIRST_POSITION][FIRST_POSITION];
        }
        else if(checkLine(board[FIRST_POSITION][THIRD_POSITION],
                board[SECOND_POSITION][SECOND_POSITION],
                board[THIRD_POSITION][FIRST_POSITION]))
        {
            return board[FIRST_POSITION][THIRD_POSITION];
        }

        return null;
    }

    /*
     * Checks a line to see if it is of the same symbol
     *
     * @param a is the first position
     * @param b is the second position
     * @param c is the third position
     * @return true if the line is valid
     */
    private boolean checkLine(final String a,
                              final String b,
                              final String c)
    {
        return a != null && a.equals(b) && b.equals(c);
    }

    /**
     * Checks if the board is full
     *
     * @return true if the board is full
     */
    public boolean isBoardFull()
    {
        for(int r = 0; r < ROWS; r++)
        {
            for(int c = 0; c < COLS; c++)
            {
                if(board[r][c] == null)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * Checks whether the rows and cols are valid
     *
     * @param row is the row
     * @param col is the col
     * @throws IndexOutOfBoundsException
     */
    private void validateRowsAndCols(final int row, final int col)
    {
        if (row < ROWS_LOWER_BOUND || row > ROWS ||
            col < COLS_LOWER_BOUND || col > COLS)
        {
            throw new IndexOutOfBoundsException(ROWS_COLS_EXCEPTION_MSG);
        }
    }
}
