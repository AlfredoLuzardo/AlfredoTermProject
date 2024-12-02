import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Main Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class Main extends Application
{
    private static final String ILLEGAL_GAME_MSG = "Game cannot be null";
    private static final String DEFAULT_MSG      = "Invalid choice";
    private static final String QUIT_MSG         = "Quitting";

    /**
     * Main method
     *
     * @param args args
     */
    public static void main(final String[] args)
    {
        launch(args);
    }

    /**
     * Application start method
     *
     * @param primaryStage is the primaryStage
     */
    @Override
    public void start(final Stage primaryStage)
    {
        final Thread thread;

        thread = new Thread(Main::mainMenu);

        Platform.setImplicitExit(false);

        thread.start();
    }

    /*
     * Launches a game
     *
     * @param game is the game to be launched
     * @throws RuntimeException if interrupted
     */
    private static void launchGame(final Game game)
    {
        final CountDownLatch latch;

        validateGame(game);

        latch = new CountDownLatch(1);

        Platform.runLater(() -> game.play(latch));

        try
        {
            latch.await();
        }
        catch (final InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    /*
     * Holds the main menu logic
     */
    private static void mainMenu()
    {
        final Scanner s;
        char choice;

        s = new Scanner(System.in);

        do
        {
            System.out.println("Press W to play the Word game.");
            System.out.println("Press N to play the Number game.");
            System.out.println("Press T to play Tic-Tac-Sweeper.");
            System.out.println("Press Q to quit.");

            choice = s.next().charAt(0);

            switch (choice)
            {
                case 'w':
                case 'W':
                    final WordGame wordGame;

                    wordGame = new WordGame();

                    try
                    {
                        wordGame.play();
                    }
                    catch(final IOException e)
                    {
                        e.printStackTrace();
                    }

                    break;

                case 'n':
                case 'N':
                    final NumberGame numberGame;

                    numberGame = new NumberGame();

                    launchGame(numberGame);
                    break;

                case 't':
                case 'T':
                    final UltimateTicTacSweeper ultimateTicTacSweeper;

                    ultimateTicTacSweeper = new UltimateTicTacSweeper();

                    launchGame(ultimateTicTacSweeper);
                    break;

                case 'q':
                case 'Q':
                    System.out.println(QUIT_MSG);
                    Platform.exit();
                    break;

                default:
                    System.out.println(DEFAULT_MSG);
            }
        }
        while (choice != 'q' && choice != 'Q');
    }

    /*
     * Validation method for game
     *
     * @param game is the game
     * @throws IllegalArgumentException if it does not meet the requirements
     */
    private static void validateGame(final Game game)
    {
        if(game == null)
        {
            throw new IllegalArgumentException(ILLEGAL_GAME_MSG);
        }
    }
}