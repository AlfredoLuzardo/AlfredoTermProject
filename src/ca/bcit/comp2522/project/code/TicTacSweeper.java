import javafx.application.Platform;
import javafx.geometry.Pos;
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
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MyGame Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class TicTacSweeper extends BoardGame<Void>
{
    private static final int ROWS                       = 3;
    private static final int COLS                       = 3;
    private static final int SCENE_WIDTH_PX             = 800;
    private static final int SCENE_HEIGHT_PX            = 400;
    private static final int INITIAL_LIVES              = 2;
    private static final int PLAYER_LIVES_THRESHOLD     = 0;
    private static final int TURN_TIME_SECONDS          = 10;
    private static final int TURN_TIME_THRESHOLD        = 0;
    private static final int THREAD_POOL_SIZE           = 1;
    private static final int EXECUTOR_DELAY_SECONDS     = 0;
    private static final int EXECUTOR_PERIOD            = 1;
    private static final int ADJACENT_MINES_INITIAL     = 0;
    private static final int MINE_COUNT_BLUE            = 1;
    private static final int MINE_COUNT_GREEN           = 2;
    private static final int MINE_COUNT_RED             = 3;
    private static final int DIAGONAL_START_ROW         = 0;
    private static final int DIAGONAL_START_COL_LEFT    = 0;
    private static final int DIAGONAL_START_COL_RIGHT   = COLS - 1;
    private static final int ROW_STEP_DIAGONAL          = 1;
    private static final int COL_STEP_DIAGONAL_LEFT     = 1;
    private static final int COL_STEP_DIAGONAL_RIGHT    = -1;
    private static final int ROW_START                  = 0;
    private static final int COL_START                  = 0;
    private static final int ROW_STEP_VERTICAL          = 1;
    private static final int ROW_STEP_HORIZONTAL        = 0;
    private static final int COL_STEP_VERTICAL          = 0;
    private static final int COL_STEP_HORIZONTAL        = 1;
    private static final int NEIGHBOR_START_OFFSET      = -1;
    private static final int NEIGHBOR_END_OFFSET        = 1;
    private static final int MIN_ROW_INDEX              = 0;
    private static final int MIN_COL_INDEX              = 0;
    private static final String MINE_COUNT_BLUE_STYLE  = "-fx-text-fill: blue";
    private static final String MINE_COUNT_GREEN_STYLE = "-fx-text-fill: green";
    private static final String MINE_COUNT_RED_STYLE   = "-fx-text-fill: red";
    private static final String MINE_STYLE_MOUSE_EXIT  = "";
    private static final String BTN_INITIAL_TXT        = "";
    private static final String TIME_UNITS_TXT         = " seconds";
    private static final String TIMEOUT_ALERT_TITLE    = "Time is up!";
    private static final String COUNT_DOWN_LABEL       = "Time left: ";
    private static final String PLAYER_TURN_LABEL      = "Current Player: ";
    private static final String GAME_TITLE             = "Tic-Tac-Sweeper";
    private static final String STYLE_SHEET_PATH       = "tictacsweeper.css";
    private static final String END_ALERT_TITLE        = "Game Over";
    private static final String MINE_ALERT_TITLE       = "Mine!";
    private static final String DRAW_TXT               = "Draw. The board is full, and both players have equal lives.";
    private static final Cell[][] board = new Cell[ROWS][COLS];
    private static final Label  countdownLabel;
    private static ScheduledExecutorService executor;
    private static Stage primaryStage;
    private static GridPane grid;
    private static Label playerTurnLabel;
    private static Label playerOneLivesLabel;
    private static Label playerTwoLivesLabel;
    private static String currentPlayer;
    private static int timeLeft;

    private final String playerOne;
    private final String playerTwo;
    private CountDownLatch latch;
    private String  winner;
    private int playerOneLives;
    private int playerTwoLives;

    static
    {
        countdownLabel = new Label(COUNT_DOWN_LABEL);
    }

    /**
     * Constructor for TicTacSweeper
     *
     * @param playerOne playerOne
     * @param playerTwo playerTwo
     */
    public TicTacSweeper(final String playerOne, final String playerTwo)
    {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    /**
     * Getter for the winner
     *
     * @return winner
     */
    public String getWinner()
    {
        return winner;
    }

    /**
     * Play method
     *
     * @param latch is the CountDownLatch
     */
    @Override
    public void play(final CountDownLatch latch)
    {
        final BorderPane root;
        final BorderPane topPane;
        final BorderPane bottomPane;
        final Scene scene;

        this.latch = latch;

        root        = new BorderPane();
        topPane     = new BorderPane();
        bottomPane  = new BorderPane();

        playerTurnLabel     = new Label(PLAYER_TURN_LABEL);
        playerOneLivesLabel = new Label("Player " + playerOne + " Lives: " + playerOneLives);
        playerTwoLivesLabel = new Label("Player " + playerTwo + " Lives: " + playerTwoLives);

        bottomPane.setLeft(playerOneLivesLabel);
        bottomPane.setRight(playerTwoLivesLabel);

        BorderPane.setAlignment(playerOneLivesLabel, Pos.CENTER_LEFT);
        BorderPane.setAlignment(playerTwoLivesLabel, Pos.CENTER_RIGHT);

        topPane.setLeft(playerTurnLabel);
        topPane.setRight(countdownLabel);

        BorderPane.setAlignment(playerTurnLabel, Pos.CENTER_LEFT);
        BorderPane.setAlignment(countdownLabel, Pos.CENTER_RIGHT);

        grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);

        initializeGrid();

        root.setTop(topPane);
        root.setCenter(grid);
        root.setBottom(bottomPane);

        BorderPane.setAlignment(grid, Pos.CENTER);

        scene = new Scene(root, SCENE_WIDTH_PX, SCENE_HEIGHT_PX);
        scene.getStylesheets().add(getClass().getResource(STYLE_SHEET_PATH).toExternalForm());

        initializeGame();
        startTimer();

        primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle(GAME_TITLE);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.showAndWait();
    }

    /**
     * initializeGame method
     * <p>
     * Initializes TicTacSweeper
     */
    @Override
    public void initializeGame()
    {
        initializeBoard();
        currentPlayer = playerOne;
        playerTurnLabel.setText(PLAYER_TURN_LABEL + currentPlayer);
        playerOneLives = INITIAL_LIVES;
        playerTwoLives = INITIAL_LIVES;
        playerOneLivesLabel.setText("Player " + playerOne + " Lives: " + playerOneLives);
        playerTwoLivesLabel.setText("Player " + playerTwo + " Lives: " + playerTwoLives);
        resetGridText();
    }

    /**
     * initializeBoard method
     * <p>
     * initializes the game board
     */
    @Override
    public void initializeBoard()
    {
        for(int r = 0; r < ROWS; r++)
        {
            for(int c = 0; c < COLS; c++)
            {
                board[r][c] = CellFactory.createCell();
            }
        }
    }

    /**
     * Ends the game
     */
    @Override
    public void endGame()
    {
        final Alert endAlert;
        final Optional<ButtonType> result;

        stopTimer();
        primaryStage.setAlwaysOnTop(false);

        endAlert = new Alert(Alert.AlertType.INFORMATION);
        endAlert.setHeaderText(null);
        endAlert.setTitle(END_ALERT_TITLE);

        if(checkWinner())
        {
            if(currentPlayer.equals(playerOne))
            {
                winner = playerOne;
                endAlert.setContentText("Player " + playerOne + " wins!");
            }
            else
            {
                winner = playerTwo;
                endAlert.setContentText("Player " + playerTwo + " wins!");
            }
        }
        else if(playerOneLives == PLAYER_LIVES_THRESHOLD)
        {
            winner = playerTwo;
            endAlert.setContentText("Player " + playerTwo + " wins! Player " + playerOne + " is out of lives.");
        }
        else if(playerTwoLives == PLAYER_LIVES_THRESHOLD)
        {
            winner = playerOne;
            endAlert.setContentText("Player " + playerOne + " wins! Player " + playerTwo + " is out of lives.");
        }
        else if(isBoardFull())
        {
            if(playerOneLives > playerTwoLives)
            {
                winner = playerOne;
                endAlert.setContentText("Player " + playerOne + " wins! Player " + playerOne + " has more lives.");
            }
            else if(playerTwoLives > playerOneLives)
            {
                winner = playerTwo;
                endAlert.setContentText("Player " + playerTwo + " wins! Player " + playerTwo + " has more lives.");
            }
            else
            {
                winner = null;
                endAlert.setContentText(DRAW_TXT);
            }
        }

        result = endAlert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK)
        {
            final Stage stage;
            stage = (Stage) playerTurnLabel.getScene().getWindow();
            stage.close();
            closeGame();
        }
    }

    /**
     * handleButtonClick method
     * <p>
     * Handles the logic behind a button being clicked
     *
     * @param row is the row
     * @param col is the col
     * @param button is the button
     */
    @Override
    public void handleButtonClick(final int row,
                                  final int col,
                                  final Button button)
    {
        if(isValidPlacement(row, col))
        {
            stopTimer();

            board[row][col].reveal(currentPlayer);

            if(board[row][col] instanceof MineCell)
            {
                button.setGraphic(((MineCell) board[row][col]).getImageView());
            }
            else
            {
                button.setText(board[row][col].toString());
            }

            if(board[row][col] instanceof MineCell)
            {
                if(currentPlayer.equals(playerOne))
                {
                    playerOneLives--;
                    playerOneLivesLabel.setText("Player " + playerOne + " Lives: " + playerOneLives);
                    mineAlert(playerOne);
                }
                else
                {
                    playerTwoLives--;
                    playerTwoLivesLabel.setText("Player " + playerTwo + " Lives: " + playerTwoLives);
                    mineAlert(playerTwo);
                }

                if(playerOneLives == PLAYER_LIVES_THRESHOLD || playerTwoLives == PLAYER_LIVES_THRESHOLD)
                {
                    endGame();
                }
            }

            if(checkWinner() || isBoardFull())
            {
                endGame();
            }
            else
            {
                switchPlayer();
                startTimer();
                playerTurnLabel.setText("Current Player: " + currentPlayer);
            }
        }
    }

    /**
     * isValidPlacement method
     * <p>
     * Checks if the point selected by the user is a valid location
     * for the number to be placed in
     *
     * @param row is the row
     * @param col is the col
     * @return true if the number can be placed in the selected point
     */
    @Override
    public boolean isValidPlacement(final int row,
                                    final int col)
    {
        return !board[row][col].isClicked();
    }

    /*
     * Begins the Timer
     */
    private void startTimer()
    {
        if(executor != null && !executor.isShutdown())
        {
            executor.shutdownNow();
        }

        executor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        timeLeft = TURN_TIME_SECONDS;

        executor.scheduleAtFixedRate(() ->
        {
            Platform.runLater(() ->
            {
                if(timeLeft >= TURN_TIME_THRESHOLD)
                {
                    countdownLabel.setText(COUNT_DOWN_LABEL + timeLeft + TIME_UNITS_TXT);
                    timeLeft--;
                }
                else
                {
                    handleTimeout();
                }
            });
        }, EXECUTOR_DELAY_SECONDS, EXECUTOR_PERIOD, TimeUnit.SECONDS);
    }

    /*
     * Handles the logic if a player runs out of time
     */
    private void handleTimeout()
    {
        final Alert timeoutAlert;

        primaryStage.setAlwaysOnTop(false);
        stopTimer();

        timeoutAlert = new Alert(Alert.AlertType.INFORMATION);
        timeoutAlert.setHeaderText(null);
        timeoutAlert.setTitle(TIMEOUT_ALERT_TITLE);
        timeoutAlert.setContentText("Player " + currentPlayer + " ran out of time!");
        timeoutAlert.showAndWait();

        switchPlayer();

        playerTurnLabel.setText(PLAYER_TURN_LABEL + currentPlayer);
        primaryStage.setAlwaysOnTop(true);

        startTimer();
    }

    /*
     * Stops the timer
     */
    private void stopTimer()
    {
        if(executor != null && !executor.isShutdown())
        {
            executor.shutdownNow();
            executor = null;
        }
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
        for(int r = 0; r < ROWS; r++)
        {
            for(int c = 0; c < COLS; c++)
            {
                final Button b;
                final int row = r;
                final int col = c;

                b = new Button();
                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(e -> handleButtonClick(row, col, b));

                b.setOnMouseEntered(e ->
                {
                    if(board[row][col].isClicked() && !(board[row][col] instanceof MineCell))
                    {
                        final int mineCount;

                        mineCount = countAdjacentMines(row, col);

                        if(mineCount != ADJACENT_MINES_INITIAL)
                        {
                            b.setText(String.valueOf(mineCount));
                        }


                        if(mineCount == MINE_COUNT_BLUE)
                        {
                            b.setStyle(MINE_COUNT_BLUE_STYLE);
                        }
                        else if (mineCount == MINE_COUNT_GREEN)
                        {
                            b.setStyle(MINE_COUNT_GREEN_STYLE);
                        }
                        else if (mineCount >= MINE_COUNT_RED)
                        {
                            b.setStyle(MINE_COUNT_RED_STYLE);
                        }
                    }
                });

                b.setOnMouseExited(e ->
                {
                    if(board[row][col].isClicked() && !(board[row][col] instanceof MineCell))
                    {
                        b.setText(board[row][col].toString());
                        b.setStyle(MINE_STYLE_MOUSE_EXIT);
                    }
                });

                GridPane.setHgrow(b, Priority.ALWAYS);
                grid.add(b, c, r);
            }
        }
    }

    /*
     * resetGridText method
     * <p>
     * Resets the text of all the buttons within the grid
     */
    private void resetGridText()
    {
        if(!grid.getChildren().isEmpty())
        {
            for(final Node node : grid.getChildren())
            {
                if(node != null)
                {
                    if(node instanceof Button button)
                    {
                        button.setText(BTN_INITIAL_TXT);
                    }
                }
            }
        }
    }

    /*
     * Shows an alert whenever a mine is stepped on by the player
     *
     * @param playerSymbol is the player
     */
    private void mineAlert(final String playerSymbol)
    {
        final Alert mineAlert;
        final Optional<ButtonType> result;
        final String player;

        if(playerSymbol.equals(playerOne))
        {
            player = "Player " + playerOne;
        }
        else
        {
            player = "Player " + playerTwo;
        }

        primaryStage.setAlwaysOnTop(false);

        mineAlert = new Alert(Alert.AlertType.INFORMATION);
        mineAlert.setHeaderText(null);
        mineAlert.setTitle(MINE_ALERT_TITLE);
        mineAlert.setContentText(player + " stepped on a mine and lost a life!");

        mineAlert.showAndWait();

        primaryStage.setAlwaysOnTop(true);
    }

    /*
     * Switches the current player
     */
    private void switchPlayer()
    {
        currentPlayer = currentPlayer.equals(playerOne) ? playerTwo : playerOne;
    }

    /*
     * Checks if there is a winner
     *
     * @return true if there is a line diagonally, horizontally, or vertically
     * of the same symbol
     */
    private boolean checkWinner()
    {
        if(checkLine(DIAGONAL_START_ROW, DIAGONAL_START_COL_LEFT, ROW_STEP_DIAGONAL, COL_STEP_DIAGONAL_LEFT) ||
           checkLine(DIAGONAL_START_ROW, DIAGONAL_START_COL_RIGHT, ROW_STEP_DIAGONAL, COL_STEP_DIAGONAL_RIGHT))
        {
            return true;
        }


        for(int r = 0; r < ROWS; r++)
        {
            if(checkLine(r, COL_START, ROW_STEP_HORIZONTAL, COL_STEP_HORIZONTAL))
            {
                return true;
            }
        }


        for(int c = 0; c < COLS; c++)
        {
            if(checkLine(ROW_START, c, ROW_STEP_VERTICAL, COL_STEP_VERTICAL))
            {
                return true;
            }
        }

        return false;
    }

    /*
     * Checks whether the line given is all the same symbol
     *
     * @param startRow is the startRow
     * @param startCol is the startCol
     * @param rowStep is the rowStep
     * @param colStep is the colStep
     * @return
     */
    private boolean checkLine(final int startRow,
                              final int startCol,
                              final int rowStep,
                              final int colStep)
    {
        final String firstSymbol;
        int row;
        int col;

        if(board[startRow][startCol].isClicked())
        {
            firstSymbol = board[startRow][startCol].toString();
            row = startRow;
            col = startCol;

            for(int r = 0; r < ROWS; r++)
            {
                if(board[row][col].toString().equals(firstSymbol))
                {
                    row += rowStep;
                    col += colStep;
                }
                else
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /*
     * Checks if the board is full
     *
     * @return true if all cells have been clicked
     */
    private boolean isBoardFull()
    {
        for(int r = 0; r < ROWS; r++)
        {
            for(int c = 0; c < COLS; c++)
            {
                if(!board[r][c].isClicked())
                {
                    return false;
                }
            }
        }

        return true;
    }

    /*
     * Counts the mines adjacent to a clicked cell
     *
     * @param row is the row
     * @param col is the col
     * @return count
     */
    private int countAdjacentMines(final int row, final int col)
    {
        int count;

        count = ADJACENT_MINES_INITIAL;

        for(int dr = NEIGHBOR_START_OFFSET; dr <= NEIGHBOR_END_OFFSET; dr++)
        {
            for(int dc = NEIGHBOR_START_OFFSET; dc <= NEIGHBOR_END_OFFSET; dc++)
            {
                final int newRow;
                final int newCol;

                newRow = row + dr;
                newCol = col + dc;

                if(newRow >= MIN_ROW_INDEX                      &&
                   newRow < ROWS                                &&
                   newCol >= MIN_COL_INDEX                      &&
                   newCol < COLS                                &&
                   (dr != MIN_ROW_INDEX || dc != MIN_COL_INDEX) &&
                   board[newRow][newCol] instanceof MineCell)
                {
                    count++;
                }
            }
        }

        return count;
    }
}
