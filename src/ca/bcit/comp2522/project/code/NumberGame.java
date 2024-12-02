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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * NumberGame class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class NumberGame extends BoardGame<Integer>
{
    private static final int ROWS                           = 4;
    private static final int COLS                           = 5;
    private static final int INITIAL_BOARD_VAL              = 0;
    private static final int INITIAL_NUMBERS_PLACED         = 0;
    private static final int INITIAL_SUCCESSFUL_PLACEMENTS  = 0;
    private static final int INITIAL_TOTAL_GAMES            = 0;
    private static final int INITIAL_WON_GAMES              = 0;
    private static final int INITIAL_LOST_GAMES             = 0;
    private static final int GRID_HOR_GAP_PX                = 10;
    private static final int GRID_VERT_GAP_PX               = 10;
    private static final int SCENE_WIDTH_PX                 = 800;
    private static final int SCENE_HEIGHT_PX                = 400;
    private static final int NUMBERS_UPPER_BOUND            = 1000;
    private static final int NUMBERS_LOWER_BOUND            = 1;
    private static final int INCREMENT_COL_VAL              = 1;
    private static final String STYLESHEET_PATH     = "numbergame.css";
    private static final String STAGE_TITLE_TXT     = "Number Game";
    private static final String STARTING_LABEL_TXT  = "Next number: ";
    private static final String GAME_OVER_TITLE_TXT = "Game Over";
    private static final String TRY_AGAIN_BTN_TXT   = "Try Again";
    private static final String QUIT_BTN_TXT        = "Quit";
    private static final String DECIMAL_FORMAT_TXT  = "#.##";
    private static final String END_ALERT_WON_TEXT  = "You won!";
    private static final String SCORE_ALERT_TITLE   = "Score";
    private static final String EMPTY_BTN_TXT       = "[ ]";
    private static final int[][] board  = new int[ROWS][COLS];
    private static final Random rand    = new Random();
    private static final int TOTAL_NUMBERS;

    private static int      totalGames;
    private static int      wonGames;
    private static int      lostGames;
    private static int      successfulPlacements;
    private static int      currentNumber;
    private static int      numbersPlaced;
    private static Stage    primaryStage;
    private static GridPane grid;
    private static Label    label;

    private CountDownLatch latch;

    static
    {
        TOTAL_NUMBERS           = ROWS * COLS;
        totalGames              = INITIAL_TOTAL_GAMES;
        wonGames                = INITIAL_WON_GAMES;
        lostGames               = INITIAL_LOST_GAMES;
        successfulPlacements    = INITIAL_SUCCESSFUL_PLACEMENTS;
    }

    /**
     * play method
     * <p>
     * Plays NumberGame
     *
     * @param latch latch
     */
    @Override
    public void play(final CountDownLatch latch)
    {
        final BorderPane root;
        final Scene scene;

        this.latch  = latch;
        root        = new BorderPane();
        grid        = new GridPane();
        label       = new Label(STARTING_LABEL_TXT);

        grid.setHgap(GRID_HOR_GAP_PX);
        grid.setVgap(GRID_VERT_GAP_PX);
        grid.setMaxWidth(Double.MAX_VALUE);

        initializeGrid();
        initializeGame();

        root.setTop(label);
        root.setCenter(grid);

        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setAlignment(grid, Pos.CENTER);

        scene = new Scene(root, SCENE_WIDTH_PX, SCENE_HEIGHT_PX);
        scene.getStylesheets().add(getClass().getResource(STYLESHEET_PATH).toExternalForm());

        primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle(STAGE_TITLE_TXT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.showAndWait();
    }

    /**
     * initializeGame method
     * <p>
     * Initializes NumberGame
     */
    @Override
    public void initializeGame()
    {
        currentNumber = generateNextNumber();
        updateLabelNumber(label);
        numbersPlaced = INITIAL_NUMBERS_PLACED;
        initializeBoard();
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
                board[r][c] = INITIAL_BOARD_VAL;
            }
        }
    }

    /**
     * endGame method
     * <p>
     * Handles the endgame logic
     */
    @Override
    public void endGame()
    {
        final Alert endAlert;
        final ButtonType tryAgain;
        final ButtonType quit;
        final Optional<ButtonType> result;

        primaryStage.setAlwaysOnTop(false);

        endAlert = new Alert(Alert.AlertType.INFORMATION);
        endAlert.setHeaderText(null);
        endAlert.setTitle(GAME_OVER_TITLE_TXT);

        totalGames++;

        if(numbersPlaced == TOTAL_NUMBERS)
        {
            endAlert.setContentText(END_ALERT_WON_TEXT);
            wonGames++;
        }
        else
        {
            endAlert.setContentText("Game Over! Impossible to place the next number: " + currentNumber + ". Try again?");
            lostGames++;
        }

        tryAgain = new ButtonType(TRY_AGAIN_BTN_TXT);
        quit     = new ButtonType(QUIT_BTN_TXT);

        endAlert.getButtonTypes().setAll(tryAgain, quit);
        result = endAlert.showAndWait();
        showScoreStatus();

        if(result.isPresent() && result.get() == tryAgain)
        {
            primaryStage.setAlwaysOnTop(true);
            initializeGame();
        }
        else
        {
            final Stage stage;
            stage = (Stage) label.getScene().getWindow();
            stage.close();
            closeGame();
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
     * @param number is the number
     * @return true if the number can be placed in the selected point
     */
    @Override
    public boolean isValidPlacement(final int row,
                                    final int col,
                                    final Integer number)
    {
        if(board[row][col] == INITIAL_BOARD_VAL)
        {
            final List<Integer> elementsBefore;
            final List<Integer> elementsAfter;
            final int maxBefore;
            final int minAfter;

            elementsBefore = getElementsBefore(row, col);
            elementsAfter = getElementsAfter(row, col);

            maxBefore = elementsBefore.stream()
                    .filter(e -> e != INITIAL_BOARD_VAL)
                    .max(Integer::compareTo)
                    .orElse(Integer.MIN_VALUE);

            minAfter = elementsAfter.stream()
                    .filter(e -> e != INITIAL_BOARD_VAL)
                    .min(Integer::compareTo)
                    .orElse(Integer.MAX_VALUE);

            return number > maxBefore && number < minAfter;
        }

        return false;
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
        if(isValidPlacement(row, col, currentNumber))
        {
            handlePlacement(row, col);
            button.setText(Integer.toString(currentNumber));

            if(numbersPlaced == TOTAL_NUMBERS)
            {
                endGame();
            }
            else
            {
                currentNumber = generateNextNumber();
                updateLabelNumber(label);
            }


            if(!hasValidPlacement(currentNumber))
            {
                endGame();
            }
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
     * Shows the scoreStatus
     */
    private void showScoreStatus()
    {
        final Alert scoreAlert;
        final double averagePlacements;
        final String scoreString;

        scoreAlert          = new Alert(Alert.AlertType.INFORMATION);
        averagePlacements   = totalGames > INITIAL_TOTAL_GAMES ?
                            (double) successfulPlacements / totalGames : INITIAL_TOTAL_GAMES;
        scoreString         = buildScoreString(averagePlacements);

        scoreAlert.setHeaderText(null);
        scoreAlert.setTitle(SCORE_ALERT_TITLE);
        scoreAlert.setContentText(scoreString);
        scoreAlert.showAndWait();
    }

    /*
     * Builds the scoreString
     *
     * @param averagePlacements is the averagePlacements per game
     * @return scoreString
     */
    private String buildScoreString(final double averagePlacements)
    {
        final StringBuilder builder;
        final DecimalFormat df;
        final String scoreString;

        df      = new DecimalFormat(DECIMAL_FORMAT_TXT);
        builder = new StringBuilder();

        if(wonGames > INITIAL_WON_GAMES)
        {
            builder.append("You won ");
            builder.append(wonGames);
            builder.append(" out of ");
            builder.append(totalGames);
            builder.append(" games\n");
        }

        if(lostGames > INITIAL_LOST_GAMES)
        {
            builder.append("You lost ");
            builder.append(lostGames);
            builder.append(" out of ");
            builder.append(totalGames);
            builder.append(" games\n");
        }

        builder.append("With ");
        builder.append(successfulPlacements);
        builder.append(" successful placements, ");
        builder.append("you had an average of ");
        builder.append(df.format(averagePlacements));
        builder.append(" per game\n");

        scoreString = builder.toString();
        return scoreString;
    }

    /*
     * generateNextNumber method
     * <p>
     * Generates the next number between the upper and lower bounds
     *
     * @return nextNumber
     */
    private int generateNextNumber()
    {
        final int nextNumber;

        nextNumber = rand.nextInt(NUMBERS_UPPER_BOUND) + NUMBERS_LOWER_BOUND;

        return nextNumber;
    }

    /*
     * updateLabelNumber method
     * <p>
     * Updates the label to match the currentNumber
     *
     * @param label is the label node
     */
    private void updateLabelNumber(final Label label)
    {
        label.setText("Next number: " + currentNumber + " - Select a slot");
    }

    /*
     * getElementsBefore method
     * <p>
     * gets the elements before the given point on the grid
     *
     * @param row is the row
     * @param col is the col
     * @return valuesBefore
     */
    private List<Integer> getElementsBefore(final int row, final int col)
    {
        final List<Integer> valuesBefore;

        valuesBefore = new ArrayList<>();

        for (int r = 0; r <= row; r++)
        {
            for (int c = 0; c < (r == row ? col : COLS); c++)
            {
                final int value;
                value = board[r][c];
                valuesBefore.add(value);
            }
        }

        return valuesBefore;
    }

    /*
     * getElementsAfter method
     * <p>
     * gets the elements after the given point on the grid
     *
     * @param row is the row
     * @param col is the col
     * @return valuesAfter
     */
    private List<Integer> getElementsAfter(final int row, final int col)
    {
        final List<Integer> valuesAfter;

        valuesAfter = new ArrayList<>();

        for (int r = row; r < ROWS; r++)
        {
            for (int c = (r == row ? col + INCREMENT_COL_VAL : 0); c < COLS; c++)
            {
                final int value;
                value = board[r][c];
                valuesAfter.add(value);
            }
        }

        return valuesAfter;
    }

    /*
     * handlePlacement method
     * <p>
     * handles the placing of the currentNumber in the selected point
     *
     * @param row is the row
     * @param col is the col
     */
    private void handlePlacement(final int row, final int col)
    {
        if(isValidPlacement(row, col, currentNumber))
        {
            board[row][col] = currentNumber;
            numbersPlaced++;
            successfulPlacements++;
        }
    }

    /*
     * hasValidPlacement method
     * <p>
     * checks if the number can be placed on the board
     *
     * @param number is the number
     * @return true if the number can be placed on the board
     */
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
                        button.setText(EMPTY_BTN_TXT);
                    }
                }
            }
        }
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
                final int row;
                final int col;

                b = new Button(EMPTY_BTN_TXT);
                row = r;
                col = c;

                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(e -> handleButtonClick(row, col, b));
                GridPane.setHgrow(b, Priority.ALWAYS);

                grid.add(b, c, r);
            }
        }
    }
}
