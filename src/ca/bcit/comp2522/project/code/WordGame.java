import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * WordGame Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class WordGame
{
    private static List<Country> countries;

    static
    {
        countries = new ArrayList<>();
    }

    public static void main(final String[] args)
    {
        List<Path> paths;
        Path resourcesPath = Paths.get("src", "resources");

        try(Stream<Path> stream = Files.walk(resourcesPath))
        {
            paths = stream
                    .filter(p -> p.toString().endsWith(".txt"))
                    .toList();

            for(Path path : paths)
            {
                List<String> pathLines;
//                pathLines = Files.readAllLines(path);
//                pathLines.forEach(System.out::println);


            }

        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }
    }

}
