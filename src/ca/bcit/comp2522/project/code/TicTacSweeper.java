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

/**
 * MyGame Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class TicTacSweeper extends BoardGame
{
    private static final int ROWS           = 6;
    private static final int COLS           = 6;
    private static final int WIDTH_PX       = 800;
    private static final int HEIGHT_PX      = 400;
    private static final int INITIAL_LIVES = 3;
    private static final String PLAYER_ONE  = "X";
    private static final String PLAYER_TWO  = "O";
    private static final Cell[][] board = new Cell[ROWS][COLS];
    private static final Random   rand  = new Random();

    private static Stage    primaryStage;
    private static GridPane grid;
    private static Label playerTurnLabel;
    private static Label playerOneLivesLabel;
    private static Label playerTwoLivesLabel;

    private int playerOneLives;
    private int playerTwoLives;
    private String currentPlayer;

    @Override
    public void play()
    {
        final BorderPane root;
        final BorderPane bottomPane;
        final Scene scene;

        root = new BorderPane();
        bottomPane= new BorderPane();

        playerTurnLabel = new Label("Starting Game");
        playerOneLivesLabel = new Label("Player One Lives: " + playerOneLives);
        playerTwoLivesLabel = new Label("Player Two Lives: " + playerTwoLives);

        bottomPane.setLeft(playerOneLivesLabel);
        bottomPane.setRight(playerTwoLivesLabel);
        BorderPane.setAlignment(playerOneLivesLabel, Pos.CENTER_LEFT);
        BorderPane.setAlignment(playerTwoLivesLabel, Pos.CENTER_RIGHT);

        grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);
        initializeGrid();

        root.setTop(playerTurnLabel);
        root.setCenter(grid);
        root.setBottom(bottomPane);

        BorderPane.setAlignment(playerTurnLabel, Pos.CENTER);
        BorderPane.setAlignment(grid, Pos.CENTER);

        scene = new Scene(root, WIDTH_PX, HEIGHT_PX);
        scene.getStylesheets().add(getClass().getResource("tictacsweeper.css").toExternalForm());

        initializeGame();

        primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic-Tac-Sweeper");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.showAndWait();
    }

    @Override
    public void initializeGame()
    {
        initializeBoard();
        currentPlayer = PLAYER_ONE;
        playerTurnLabel.setText("Current Player: " + currentPlayer);
        playerOneLives = INITIAL_LIVES;
        playerTwoLives = INITIAL_LIVES;
        playerOneLivesLabel.setText("Player One Lives: " + playerOneLives);
        playerTwoLivesLabel.setText("Player Two Lives: " + playerTwoLives);

    }

    @Override
    public void endGame()
    {
        final Alert endAlert;
        final ButtonType playAgain;
        final ButtonType quit;
        final Optional<ButtonType> result;

        primaryStage.setAlwaysOnTop(false);

        endAlert = new Alert(Alert.AlertType.INFORMATION);
        endAlert.setHeaderText(null);
        endAlert.setTitle("Game Over");

        if(checkWinner())
        {
            if(currentPlayer.equals(PLAYER_ONE))
            {
                endAlert.setContentText("Player 1 (X) wins!");
            }
            else
            {
                endAlert.setContentText("Player 2 (O) wins!");
            }
        }
        else if(playerOneLives == 0)
        {
            endAlert.setContentText("Player 2 wins! Player 1 is out of lives.");
        }
        else if(playerTwoLives == 0)
        {
            endAlert.setContentText("Player 1 wins! Player 2 is out of lives.");
        }
        else if(isBoardFull())
        {
            endAlert.setContentText("Draw. All cells have been clicked");
        }

        playAgain = new ButtonType("Play Again");
        quit = new ButtonType("Quit");
        endAlert.getButtonTypes().setAll(playAgain, quit);

        result = endAlert.showAndWait();

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
        }
    }

    @Override
    protected void initializeBoard()
    {
        for(int r = 0; r < ROWS; r++)
        {
            for(int c = 0; c < COLS; c++)
            {
                if(rand.nextInt(5) == 0)
                {
                    board[r][c] = new MineCell();
                }
                else
                {
                    board[r][c] = new EmptyCell();
                }
            }
        }
    }

    /**
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
                GridPane.setHgrow(b, Priority.ALWAYS);

                grid.add(b, c, r);
            }
        }
    }

    /**
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
                        button.setText("");
                    }
                }
            }
        }
    }

    @Override
    protected void handleButtonClick(final int row,
                                     final int col,
                                     final Button button)
    {
        if(isValidPlacement(row, col, button))
        {
            board[row][col].reveal(currentPlayer);
            button.setText(board[row][col].toString());

            if(board[row][col] instanceof MineCell)
            {
                if(currentPlayer.equals(PLAYER_ONE))
                {
                    playerOneLives--;
                    playerOneLivesLabel.setText("Player One Lives: " + playerOneLives);
                    mineAlert(PLAYER_ONE);
                }
                else
                {
                    playerTwoLives--;
                    playerTwoLivesLabel.setText("Player Two Lives: " + playerTwoLives);
                    mineAlert(PLAYER_TWO);
                }

                if(playerOneLives == 0 || playerTwoLives == 0)
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
                playerTurnLabel.setText("Current Player: " + currentPlayer);
            }
        }
    }

    private void mineAlert(final String playerSymbol)
    {
        final Alert mineAlert;
        final Optional<ButtonType> result;
        final String player;

        if(playerSymbol.equals(PLAYER_ONE))
        {
            player = "Player one";
        }
        else
        {
            player = "Player two";
        }

        primaryStage.setAlwaysOnTop(false);

        mineAlert = new Alert(Alert.AlertType.INFORMATION);
        mineAlert.setHeaderText(null);
        mineAlert.setTitle("Mine!");
        mineAlert.setContentText(player + " stepped on a mine and lost a life!");

        result = mineAlert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK)
        {
            primaryStage.setAlwaysOnTop(true);
        }
    }

    @Override
    protected boolean isValidPlacement(final int row,
                                       final int col, Object value)
    {
        return !board[row][col].isClicked();
    }

    private void switchPlayer()
    {
        currentPlayer = currentPlayer.equals(PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;
    }

    private boolean checkWinner()
    {

        if(checkLine(0, 0, 0, 1) ||
           checkLine(0, COLS - 1, 1, -1))
        {
            return true;
        }

        for(int r = 0; r < ROWS; r++)
        {
            if(checkLine(r, 0, 0, 1))
            {
                return true;
            }
        }

        for(int c = 0; c < COLS; c++)
        {
            if(checkLine(0, c, 1, 0))
            {
                return true;
            }
        }

        return false;
    }

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
}
