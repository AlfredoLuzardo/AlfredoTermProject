import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class Main extends Application
{
    private static final int ROWS = 4;
    private static final int COLS = 5;

    public static void main(final String[] args)
    {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage)
    {
        final Scanner s;
        char choice;

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        initializeGrid(grid);

        choice = '\0';
        s = new Scanner(System.in);

        do
        {
            System.out.println("Press W to play the Word game.");
            System.out.println("Press N to play the Number game.");
            System.out.println("Press M to play the <your game's name> game.");
            System.out.println("Press Q to quit.");

            choice = s.next().charAt(0);

            switch (choice)
            {
                case 'w':
                case 'W':
                    WordGame wordGame;
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
                    NumberGame numberGame;
                    numberGame = new NumberGame();
                    primaryStage.show();
                    break;

                case 'm':
                case 'M':
                    System.out.println("Running M game");
                    break;

                case 'q':
                case 'Q':
                    System.out.println("Quitting");
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }
        while (choice != 'q' && choice != 'Q');

        scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Number Game");
    }
}