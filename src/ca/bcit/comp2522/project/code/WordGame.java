import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.time.LocalDateTime;


/**
 * WordGame Class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class WordGame
{
    private static final int NUM_OF_FACTS           = 3;
    private static final int GAMES_PLAYED_INITIAL   = 0;
    private static final int INCORRECT_INITIAL      = 0;
    private static final int SCORE_INITIAL          = 0;
    private static final int FIRST                  = 0;
    private static final int SECOND                 = 1;
    private static final int THIRD                  = 2;
    private static final int NUM_OF_QUESTIONS       = 10;
    private static final int ARR_SIZE               = 0;
    private static final int NUM_OF_QUESTION_TYPES  = 3;
    private static final String PLAY_AGAIN_PROMPT            = "\nWould you like to play again?";
    private static final String INVALID_INPUT_PROMPT         = "Invalid input. Please enter 'Yes' or 'No': ";
    private static final String ILLEGAL_INPUT_TXT            = "Input cannot be null or blank";
    private static final String SCORE_PATH                   = "score.txt";
    private static final String PREVIOUS_RECORD_INFO         = "the previous record was %.2f points per game on %s at %s";
    private static final String NOT_HIGH_SCORE_TXT           = "You did not beat the high score of %.2f points per game from %s at %s";
    private static final String PROMPT_COUNTRY_QUESTION      = "\nWhat Country is this the capital of?: ";
    private static final String PROMPT_CAPITAL_QUESTION      = "\nWhat is the capital of this country?: ";
    private static final String PROMPT_COUNTRY_FACT_QUESTION = "\nWhat country is being described?: ";
    private static final String TRY_AGAIN_PROMPT             = "Try again: ";
    private static final String DEFAULT_PLAY_RND_TXT         = "Could not get Question";
    private static final String ANSWER_REGEX                 = "[^\\p{ASCII}]";
    private static final String REGEX_REPLACEMENT_STR        = "";
    private static final String CORRECT_ANSWER_TXT           = "CORRECT";
    private static final String INCORRECT_ANSWER_TXT         = "INCORRECT";
    private static final String RESOURCES_PATH               = "src/resources";
    private static final String TEXT_FILE_EXTENSION          = ".txt";
    private static final String COUNTRY_CAPITAL_SEPARATOR    = ":";
    private static final String COUNTRY_NAME_SEPARATOR       = ", ";
    private static final String DATE_SEPARATOR               = " ";
    private static final String WORD_GAME_SUMMARY            = """
                                                                 %d word game played
                                                                 %d correct answers on the first attempt
                                                                 %d correct answers on the second attempt
                                                                 %d incorrect answers on two attempts each""";
    private static final String NEW_HIGH_SCORE_TXT           =
                                                               """
                                                               CONGRATULATIONS!
                                                               You are the new high score with an average of %.2f points per game;
                                                               """;
    private static final List<Country> countries;

    private static int correctFirstAttempt;
    private static int correctSecondAttempt;
    private static int incorrect;
    private static int gamesPlayed;

    static
    {
        countries = new ArrayList<>();
        correctFirstAttempt = SCORE_INITIAL;
        correctSecondAttempt = SCORE_INITIAL;
        incorrect = INCORRECT_INITIAL;
        gamesPlayed = GAMES_PLAYED_INITIAL;
    }

    /**
     * Play method
     */
    public void play() throws IOException
    {
        final Scanner scan;
        final Random random;
        final Map<String, Country> countryMap;
        final List<String> countryNames;
        final World world;
        final Score score;
        final LocalDateTime currentTime;
        final String[] dateAndTime;
        final Score currentHighScore;
        String playAgain;

        scan         = new Scanner(System.in);
        random       = new Random();
        countryNames = new ArrayList<>();
        countryMap   = new HashMap<>();
        world        = new World(countryMap);

        currentHighScore = Score.getHighestScore();
        readCountryFiles();

        countries.forEach(c -> countryMap.put(c.getName(), c));
        world.getCountryMap().forEach((n, c) -> countryNames.add(n));

        do
        {
            playRound(countryNames, world, random, scan);

            System.out.println(PLAY_AGAIN_PROMPT);

            playAgain = scan.nextLine().trim();

            while(!playAgain.equalsIgnoreCase("Yes") && !playAgain.equalsIgnoreCase("No"))
            {
                System.out.println(INVALID_INPUT_PROMPT);
                playAgain = scan.nextLine().trim();
            }

        } while (playAgain.equalsIgnoreCase("Yes"));


        currentTime = LocalDateTime.now();
        score = new Score(currentTime, gamesPlayed, correctFirstAttempt, correctSecondAttempt, incorrect);


        if(Score.isHighScore(score))
        {
            System.out.printf(NEW_HIGH_SCORE_TXT, score.getAverageScore());

            if(currentHighScore != null)
            {
                dateAndTime = Score.formattedDateTime(currentHighScore.getDateTimePlayed()).split(DATE_SEPARATOR);

                System.out.printf(PREVIOUS_RECORD_INFO,
                        currentHighScore.getAverageScore(),
                        dateAndTime[FIRST],
                        dateAndTime[SECOND]);
            }
        }
        else if(currentHighScore != null)
        {
            dateAndTime = Score.formattedDateTime(currentHighScore.getDateTimePlayed()).split(DATE_SEPARATOR);

            System.out.printf(NOT_HIGH_SCORE_TXT,
                    currentHighScore.getAverageScore(),
                    dateAndTime[FIRST],
                    dateAndTime[SECOND]);
        }

        Score.appendScoreToFile(score, SCORE_PATH);
    }

    /*
     * Play the word game
     *
     * @param countryNames
     * @param world
     * @param random
     * @param scan
     */
    private static void playRound(final List<String> countryNames,
                                  final World world,
                                  final Random random,
                                  final Scanner scan)
    {
        for(int i = 0; i < NUM_OF_QUESTIONS; i++)
        {
            final String answer;
            final Country country;
            final String countryName;
            final String[] facts;
            final String chosenFact;
            final String input;

            countryName = countryNames.get(random.nextInt(countryNames.size()));
            country = world.getCountryMap().get(countryName);
            facts = country.getFacts();

            chosenFact = facts[random.nextInt(facts.length)];

            switch(random.nextInt(NUM_OF_QUESTION_TYPES))
            {
                case FIRST:

                    System.out.println("\n" + country.getCapitalCityName() + PROMPT_COUNTRY_QUESTION);

                    input = scan.nextLine();

                    answer = country.getName().replaceAll(ANSWER_REGEX, REGEX_REPLACEMENT_STR);

                    guessQuestion(scan, answer, input);

                    break;

                case SECOND:

                    System.out.println("\n" + country.getName() + PROMPT_CAPITAL_QUESTION);

                    input = scan.nextLine();

                    answer = country.getCapitalCityName().replaceAll(ANSWER_REGEX, REGEX_REPLACEMENT_STR);

                    guessQuestion(scan, answer, input);

                    break;

                case THIRD:

                    System.out.println("\n" + chosenFact + PROMPT_COUNTRY_FACT_QUESTION);

                    input = scan.nextLine();

                    answer = country.getName().replaceAll(ANSWER_REGEX, REGEX_REPLACEMENT_STR);

                    guessQuestion(scan, answer, input);

                    break;

                default:
                    System.out.println(DEFAULT_PLAY_RND_TXT);
            }
        }

        gamesPlayed++;

        System.out.printf(WORD_GAME_SUMMARY, gamesPlayed, correctFirstAttempt, correctSecondAttempt, incorrect);
    }

    /*
     * Check the guesses of a question
     *
     * @param scan scan
     * @param answer answer
     * @param input input
     */
    private static void guessQuestion(final Scanner scan,
                                      final String answer,
                                      String input)
    {
        if(evaluateAnswer(input, answer))
        {
            correctFirstAttempt++;
        }
        else
        {
            System.out.println(TRY_AGAIN_PROMPT);
            input = scan.nextLine();

            if(evaluateAnswer(input, answer))
            {
                correctSecondAttempt++;
            }
            else
            {
                incorrect++;
                System.out.println("The correct answer was " + answer);
            }
        }
    }

    /*
     * Evaluate an answer
     *
     * @param input input
     * @param correctAnswer correctAnswer
     * @return true if input is equal to answer
     */
    private static boolean evaluateAnswer(final String input, final String correctAnswer)
    {
        validateInput(input);

        if(input.equalsIgnoreCase(correctAnswer))
        {
            System.out.println(CORRECT_ANSWER_TXT);
            return true;
        }
        else
        {
            System.out.println(INCORRECT_ANSWER_TXT);
            return false;
        }
    }

    /*
     * Read all the country files
     */
    private static void readCountryFiles()
    {
        final List<Path> paths;
        final Path resourcesPath;

        resourcesPath = Paths.get(RESOURCES_PATH);

        try(Stream<Path> stream = Files.walk(resourcesPath))
        {
            paths = stream
                    .filter(p -> p.toString().endsWith(TEXT_FILE_EXTENSION))
                    .toList();

            if(!paths.isEmpty())
            {
                for(final Path path : paths)
                {
                    if(path != null)
                    {
                        final List<String> pathLines;

                        pathLines = Files.readAllLines(path);
                        parseCountryFile(pathLines);
                    }
                }
            }

        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }
    }

    /*
     * Parse a country file
     *
     * @param pathLines pathLines
     */
    private static void parseCountryFile(final List<String> pathLines)
    {
        final List<String> tempFacts;
        String country;
        String capital;

        country     = null;
        capital     = null;
        tempFacts   = new ArrayList<>();

        if(!pathLines.isEmpty())
        {
            for(String line : pathLines)
            {
                if(line != null && !line.isBlank())
                {
                    if(tempFacts.size() == NUM_OF_FACTS)
                    {
                        if(country != null && capital != null)
                        {
                            addCountry(country, capital, tempFacts);
                        }

                        country = null;
                        capital = null;

                        tempFacts.clear();
                    }


                    if(line.contains(COUNTRY_CAPITAL_SEPARATOR) && country == null)
                    {
                        final String[] parts;

                        parts = line.split(COUNTRY_CAPITAL_SEPARATOR);

                        if(parts[FIRST].contains(COUNTRY_NAME_SEPARATOR))
                        {
                            final String[] nameParts;

                            nameParts = parts[FIRST].split(COUNTRY_NAME_SEPARATOR);
                            country  = nameParts[SECOND] + nameParts[FIRST];
                        }
                        else
                        {
                            country = parts[FIRST];
                        }

                        capital = parts[SECOND];
                    }
                    else
                    {
                        tempFacts.add(line);
                    }
                }
            }
        }
    }

    /*
     * Adds a country to the countries list
     *
     * @param country is the country
     * @param capital is the capital
     * @param tempFacts is tempFacts
     */
    private static void addCountry(final String country,
                                   final String capital,
                                   final List<String> tempFacts)
    {
        if(country != null && capital != null)
        {
            final String[] facts;

            facts = tempFacts.toArray(new String[ARR_SIZE]);

            countries.add(new Country(country, capital, facts));
        }
    }

    /*
     * validateInput
     *
     * @param input is the input
     * @throws IllegalArgumentException if input is null or blank
     */
    private static void validateInput(final String input)
    {
        if(input == null || input.isBlank())
        {
            throw new IllegalArgumentException(ILLEGAL_INPUT_TXT);
        }
    }
}
