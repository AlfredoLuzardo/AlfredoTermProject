import java.util.Random;
import java.util.Scanner;

public class NumberGame extends Game
{
    private static final int ELEM_INITIAL = 0;
    private static final int ROWS = 4;
    private static final int COLS = 5;

    private static int[][] board;

    public static void main(String[] args)
    {
        final Scanner scan;
        int row;
        int col;

        initializeBoard();
        scan = new Scanner(System.in);
        board = new int[ROWS][COLS];


        for(int r = 0; r < 4; r++)
        {
            for(int c = 0; c < 5; c++)
            {
                System.out.print(board[r][c] + " ");
            }
        }

        System.out.println("Please enter row: ");
        row = scan.nextInt();
        System.out.println("Please enter column: ");
        col = scan.nextInt();

        if(evaluateInput(16, 2, 1))
        {
            System.out.println("Can place here boi");
        }
        else
        {
            System.out.println("Cant place here");
        }

    }

    private static void initializeBoard()
    {
        for(int r = 0; r < ROWS; r++)
        {
            for(int c = 0; c < COLS; c++)
            {
                board[r][c] = ELEM_INITIAL;
            }
        }
    }

//    @Override
    public static boolean evaluateInput(final int input, final int row, final int col)
    {
        final int placement = board[row][col];

        //       2      1
        if(placement == 0)
        {
            // Before
            for(int r = 0; r <= row; r++)
            {
                for(int c = 0; c < (r == row ? col : 5); c++)
                {
                    final int previous;

                    previous = board[r][c];

                    if(previous > placement)
                    {
                        return false;
                    }
                }
            }


            // After
            //         2
            for(int r = row; r < 4; r++)
            {
                //           2 == 2    col != col
                for(int c = (r == row ? col + 1 : 0); c < 5; c++)
                {
                    final int next;

                    next = board[r][c];

                    if(next != 0 && next < placement)
                    {
                        return false;
                    }
                }
            }
//            previous =

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean isGameOver()
    {
        return false;
    }

    //    @Override
//    void play()
//    {
//        int currentNumber;
//
//        currentNumber = rand.nextInt(1000) + 1;
//        do
//        {
//            final int input;
//            System.out.println("The number is: " + currentNumber);
//            System.out.println("Which slot would you like to put it in?");
////            input = scan.nextInt();
////            evaluateInput(input);
//
//        }
//        while(!isGameOver());
//    }

//    private static void handlePlacement()
//    {
//        if()
//    }
}
