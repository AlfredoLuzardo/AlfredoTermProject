import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * NumberGame Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class NumberGameGui extends Application
{
    private Button[][] buttons;

    @Override
    public void start(final Stage primaryStage)
    {
        final GridPane grid;
        final Scene scene;

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);



        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                Button b = new Button("[ ]");

                grid.add(b, j, i);
            }
        }

        scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Number Game");
        primaryStage.show();
    }

    public static void main(final String[] args)
    {
        Application.launch(args);
    }
}
