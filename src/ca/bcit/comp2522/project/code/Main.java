import javafx.application.Application;

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
public class Main
{
    /**
     * Main method
     *
     * @param args args
     */
    public static void main(final String[] args)
    {
        final Scanner s;
        char choice;

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
                    System.out.println("Running Number game");
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
     }
}