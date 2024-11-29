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

/**
 * NumberGame class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class NumberGame extends BoardGame<Integer>
{
    private static final int ROWS           = 4;
    private static final int COLS           = 5;
    private static final int INITIAL        = 0;
    private static final int HOR_GAP        = 10;
    private static final int VERT_GAP       = 10;
    private static final int TOTAL_NUMBERS  = ROWS * COLS;
    private static final int WIDTH_PX       = 800;
    private static final int HEIGHT_PX      = 400;
    private static final int UPPER_BOUND    = 1000;
    private static final int LOWER_BOUND    = 1;

    private static final Random rand    = new Random();
    private static final int[][] board  = new int[ROWS][COLS];

    private static int      totalGames;
    private static int      wonGames;
    private static int      lostGames;
    private static int      successfulPlacements;
    private static int      currentNumber;
    private static int      numbersPlaced;
    private static Stage    primaryStage;
    private static GridPane grid;
    private static Label    label;

    /**
     * play method
     * <p>
     * Plays NumberGame
     */
    @Override
    public void play()
    {
        final BorderPane root;
        final Scene scene;

        totalGames = INITIAL;
        wonGames = INITIAL;
        lostGames = INITIAL;
        successfulPlacements = INITIAL;

        root    = new BorderPane();
        grid    = new GridPane();
        label   = new Label("Starting Game");

        grid.setHgap(HOR_GAP);
        grid.setVgap(VERT_GAP);
        grid.setMaxWidth(Double.MAX_VALUE);
        initializeGrid();

        initializeGame();

        root.setTop(label);
        root.setCenter(grid);
        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setAlignment(grid, Pos.CENTER);

        scene = new Scene(root, WIDTH_PX, HEIGHT_PX);
        scene.getStylesheets().add(getClass().getResource("numbergame.css").toExternalForm());

        primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Number Game");
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
        numbersPlaced = INITIAL;
        initializeBoard();
        resetGridText();
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
        endAlert.setTitle("Game Over");

        totalGames++;

        if(numbersPlaced == TOTAL_NUMBERS)
        {
            endAlert.setContentText("You won!");
            wonGames++;
        }
        else
        {
            endAlert.setContentText("Game Over! Impossible to place the next number: " + currentNumber + ". Try again?");
            lostGames++;
        }

        tryAgain = new ButtonType("Try Again");
        quit = new ButtonType("Quit");
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
        }
    }

    private void showScoreStatus()
    {
        final Alert scoreAlert;
        final DecimalFormat df;
        final double averagePlacements;
        final StringBuilder builder;
        final String scoreString;

        df = new DecimalFormat("#.##");
        scoreAlert = new Alert(Alert.AlertType.INFORMATION);
        averagePlacements = totalGames > 0 ? (double) successfulPlacements / totalGames : 0;
        builder = new StringBuilder();

        if(wonGames > 0)
        {
            builder.append("You won ");
            builder.append(wonGames);
            builder.append(" out of ");
            builder.append(totalGames);
            builder.append(" games\n");
        }

        if(lostGames > 0)
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

        scoreAlert.setHeaderText(null);
        scoreAlert.setTitle("Score");
        scoreAlert.setContentText(scoreString);
        scoreAlert.showAndWait();
    }

    /**
     * generateNextNumber method
     * <p>
     * Generates the next number between the upper and lower bounds
     *
     * @return nextNumber
     */
    private int generateNextNumber()
    {
        final int nextNumber;

        nextNumber = rand.nextInt(UPPER_BOUND) + LOWER_BOUND;

        return nextNumber;
    }

    /**
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
                board[r][c] = INITIAL;
            }
        }
    }

    /**
     * getElementsBefore method
     * <p>
     * gets the elements before the given point on the grid
     *
     * @param row is the row
     * @param col is the col
     * @return valuesBefore
     */
    public List<Integer> getElementsBefore(final int row, final int col)
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

    /**
     * getElementsAfter method
     * <p>
     * gets the elements after the given point on the grid
     *
     * @param row is the row
     * @param col is the col
     * @return valuesAfter
     */
    public List<Integer> getElementsAfter(final int row, final int col)
    {
        final List<Integer> valuesAfter;

        valuesAfter = new ArrayList<>();

        for (int r = row; r < ROWS; r++)
        {
            for (int c = (r == row ? col + 1 : 0); c < COLS; c++)
            {
                final int value;
                value = board[r][c];
                valuesAfter.add(value);
            }
        }

        return valuesAfter;
    }

    /**
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

    /**
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
                        button.setText("[ ]");
                    }
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

                b = new Button("[ ]");

                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(e -> handleButtonClick(row, col, b));
                GridPane.setHgrow(b, Priority.ALWAYS);

                grid.add(b, c, r);
            }
        }
    }
}
