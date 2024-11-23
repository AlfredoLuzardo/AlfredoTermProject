import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * WordGame Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class WordGame
{
    private final static int INCORRECT_INITIAL  = 0;
    private final static int SCORE_INITIAL      = 0;
    private final static int FIRST              = 0;
    private final static int SECOND             = 1;
    private final static int THIRD              = 2;
    private final static int NUM_OF_QUESTIONS   = 10;
    private final static List<Country> countries;

    private static int correctFirstAttempt;
    private static int correctSecondAttempt;
    private static int incorrect;

    static
    {
        countries = new ArrayList<>();
        correctFirstAttempt = SCORE_INITIAL;
        correctSecondAttempt = SCORE_INITIAL;
        incorrect = INCORRECT_INITIAL;
    }

    /**
     * Main method
     *
     * @param args args
     */
    public static void main(final String[] args)
    {
        final Map<String, Country> countryMap;
        final Random random;
        final World world;
        final List<String> countryNames;
        final Scanner scan;

        scan = new Scanner(System.in);
        countryNames = new ArrayList<>();
        countryMap = new HashMap<>();
        random = new Random();

        readCountryFiles();
        countries.forEach(c -> countryMap.put(c.getName(), c));

        world = new World(countryMap);


        world.getCountryMap().forEach((n, c) ->
        {
            countryNames.add(n);
        });
        // have an array of country names (maybe get from world)
        // generate random int, get country name using int
        // access country from world using country name as key

        for(int i = 0; i < NUM_OF_QUESTIONS; i++)
        {
            Country country;
            String countryName;
            String[] facts;
            String chosenFact;
            String input;

            countryName = countryNames.get(random.nextInt(countryNames.size()));
            country = world.getCountryMap().get(countryName);
            facts = country.getFacts();

            chosenFact = facts[random.nextInt(facts.length)];

            switch(random.nextInt(3))
            {
                case FIRST:
                    System.out.println("\n" + country.getCapitalCityName() +
                                        "\nWhat Country is this the capital of?: ");
                    input = scan.nextLine();


                    // Get user input
                    // Evaluate user input
                        // Correct ? add Score : dont add score;

                    break;

                case SECOND:
                    System.out.println("\n" + country.getName() +
                                        "\nWhat is the capital of this country?: ");
                    break;

                case THIRD:
                    System.out.println("\n" + chosenFact +
                                        "\nWhat country is being described?: ");
                    break;

                default:
                    System.out.println("Could not get Question");
            }
        }
    }

    private static void validateInput(final String input)
    {
        if(input == null || input.isBlank())
        {
            throw new IllegalArgumentException("Answer cannot be null or blank");
        }
    }

    private static void evaluateCountryAnswer(final String input, final Country country)
    {
        validateInput(input);

        if(input.equals(country.getName()))
        {
            System.out.println("CORRECT");
            score++;
        }
        else
        {
            if(guess == 2)
            {
                System.out.println("INCORRECT");
                guess = 1;
            }
            else
            {
                score++;

            }
        }
    }

    private static void readCountryFiles()
    {
        final List<Path> paths;
        final Path resourcesPath;


        resourcesPath = Paths.get("src", "resources");

        try(Stream<Path> stream = Files.walk(resourcesPath))
        {
            paths = stream
                    .filter(p -> p.toString().endsWith(".txt"))
                    .toList();

            for(Path path : paths)
            {
                final List<String> pathLines;

                pathLines = Files.readAllLines(path);
                parseCountryFile(pathLines);
            }
        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void parseCountryFile(final List<String> pathLines)
    {
        final List<String> tempFacts;
        String country;
        String capital;

        country = null;
        capital = null;
        tempFacts   = new ArrayList<>();

        if(!pathLines.isEmpty())
        {
            for(String line : pathLines)
            {
                if(line != null && !line.isBlank())
                {
                    if(line.contains(":"))
                    {
                        final String[] parts;

                        if(country != null && capital != null)
                        {
                            addCountry(country, capital, tempFacts);
                        }

                        parts = line.split(":");
                        country = parts[FIRST];
                        capital = parts[SECOND];
                        tempFacts.clear();
                    }
                    else
                    {
                        tempFacts.add(line);
                    }
                }
            }

            if(country != null && capital != null)
            {
                addCountry(country, capital, tempFacts);
            }

        }
    }

    private static void addCountry(final String country, final String capital, final List<String> tempFacts)
    {
        if(country != null && capital != null)
        {
            final String[] facts;

            facts = tempFacts.toArray(new String[0]);
            countries.add(new Country(country, capital, facts));
        }
    }

}
