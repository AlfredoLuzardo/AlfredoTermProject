import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Score class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class Score
{
    private static final int NUM_GAMES_INITIAL                   = 0;
    private static final int NUM_CORRECT_FIRST_ATTEMPT_INITIAL   = 0;
    private static final int NUM_CORRECT_SECOND_ATTEMPT_INITIAL  = 0;
    private static final int NUM_INCORRECT_INITIAL               = 0;
    private static final int FIRST_ATTEMPT_VAL                   = 2;
    private static final int SECOND_ATTEMPT_VAL                  = 1;
    private static final String SCORE_PATH               = "score.txt";
    private static final String TOTAL_SCORE_TXT          = "Total Score: ";
    private static final String DATE_TIME_FMT            = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_TIME_TXT            = "Date and Time: ";
    private static final String GAMES_PLAYED_TXT         = "Games Played: ";
    private static final String CORR_FIRST_ATTEMPTS_TXT  = "Correct First Attempts: ";
    private static final String CORR_SECOND_ATTEMPTS_TXT = "Correct Second Attempts: ";
    private static final String INCORRECT_ATTEMPTS_TXT   = "Incorrect Attempts: ";

    private final LocalDateTime dateTimePlayed;
    private final int           numGamesPlayed;
    private final int           numCorrectFirstAttempt;
    private final int           numCorrectSecondAttempt;
    private final int           numIncorrectTwoAttempt;

    /**
     * Constructor for Score
     *
     * @param numGamesPlayed numGamesPlayed
     * @param numCorrectFirstAttempt numCorrectFirstAttempt
     * @param numCorrectSecondAttempt numCorrectSecondAttempt
     * @param numIncorrectTwoAttempt numIncorrectTwoAttempt
     */
    public Score(final LocalDateTime dateTimePlayed,
                 final int numGamesPlayed,
                 final int numCorrectFirstAttempt,
                 final int numCorrectSecondAttempt,
                 final int numIncorrectTwoAttempt)
    {
        this.dateTimePlayed = dateTimePlayed;
        this.numGamesPlayed = numGamesPlayed;
        this.numCorrectFirstAttempt = numCorrectFirstAttempt;
        this.numCorrectSecondAttempt = numCorrectSecondAttempt;
        this.numIncorrectTwoAttempt = numIncorrectTwoAttempt;
    }

    /**
     * Getter for total score
     *
     * @return totalScore
     */
    public int getScore()
    {
        final int totalScoreFirstAttempt;
        final int totalScoreSecondAttempt;
        final int totalScore;

        totalScoreFirstAttempt = FIRST_ATTEMPT_VAL * numCorrectFirstAttempt;
        totalScoreSecondAttempt = SECOND_ATTEMPT_VAL * numCorrectSecondAttempt;

        totalScore = totalScoreFirstAttempt + totalScoreSecondAttempt;

        return totalScore;
    }

    /**
     * Getter for dateTimePlayed
     *
     * @return dateTimePlayed
     */
    public LocalDateTime getDateTimePlayed()
    {
        return dateTimePlayed;
    }

    /**
     * Gets the highestScore
     *
     * @return highestScore
     */
    public static Score getHighestScore()
    {
        final List<Score> scores;

        scores = readScoresFromFile(SCORE_PATH);

        return scores.stream()
                .max(Comparator.comparingInt(Score::getScore))
                .orElse(null);
    }

    /**
     * Gets the averageScore per game
     *
     * @return average
     */
    public double getAverageScore()
    {
        final double average;

        average = (double) getScore() / numGamesPlayed;

        return average;
    }

    /**
     * Appends the score to a file;
     *
     * @param score score
     * @param file file
     * @throws IOException e
     */
    public static void appendScoreToFile(final Score score,
                                         final String file)
            throws IOException
    {
        final Path path;
        final List<String> scoreLines;

        path = Paths.get(file);

        if(Files.notExists(path))
        {
            Files.createFile(path);
        }

        Files.writeString(path, score.toString() + "\n", StandardOpenOption.APPEND);
    }

    /**
     * Reads scores from a file
     *
     * @param file file
     * @return scores
     */
    public static List<Score> readScoresFromFile(final String file)
    {
        final List<Score> scores;

        scores = new ArrayList<>();

        try
        {
            final Path path;
            final List<String> lines;
            final List<String> currentBlock;

            path         = Paths.get(file);
            currentBlock = new ArrayList<>();

            if(Files.notExists(path))
            {
                Files.createFile(path);
            }

            lines = Files.readAllLines(path);

            if(!lines.isEmpty())
            {
                for(final String line : lines)
                {
                    if(line != null)
                    {
                        if(!line.startsWith(TOTAL_SCORE_TXT))
                        {
                            if(line.trim().isEmpty())
                            {
                                scores.add(parseScoreBlock(currentBlock));
                                currentBlock.clear();
                            }

                            currentBlock.add(line);
                        }
                    }
                }
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }

        return scores;
    }

    /*
     * Parses a score block
     *
     * @param scoreBlock is the scoreBlock
     * @return score
     */
    private static Score parseScoreBlock(final List<String> scoreBlock)
    {
        final DateTimeFormatter formatter;
        LocalDateTime dateTime;
        int numGamesPlayed;
        int numCorrectFirstAttempt;
        int numCorrectSecondAttempt;
        int numIncorrectTwoAttempt;

        dateTime                = null;
        numGamesPlayed          = NUM_GAMES_INITIAL;
        numCorrectFirstAttempt  = NUM_CORRECT_FIRST_ATTEMPT_INITIAL;
        numCorrectSecondAttempt = NUM_CORRECT_SECOND_ATTEMPT_INITIAL;
        numIncorrectTwoAttempt  = NUM_INCORRECT_INITIAL;

        formatter = DateTimeFormatter.ofPattern(DATE_TIME_FMT);

        if(!scoreBlock.isEmpty())
        {
            for(final String line : scoreBlock)
            {
                if(line != null)
                {
                    if(line.startsWith(DATE_TIME_TXT))
                    {
                        final String dateTimeString;

                        dateTimeString = line.substring(DATE_TIME_TXT.length()).trim();
                        dateTime = LocalDateTime.parse(dateTimeString, formatter);
                    }
                    else if(line.startsWith(GAMES_PLAYED_TXT))
                    {
                        numGamesPlayed = Integer.parseInt(line.substring(GAMES_PLAYED_TXT.length()).trim());
                    }
                    else if(line.startsWith(CORR_FIRST_ATTEMPTS_TXT))
                    {
                        numCorrectFirstAttempt = Integer.parseInt(line.substring(CORR_FIRST_ATTEMPTS_TXT.length()).trim());
                    }
                    else if(line.startsWith(CORR_SECOND_ATTEMPTS_TXT))
                    {
                        numCorrectSecondAttempt = Integer.parseInt(line.substring(CORR_SECOND_ATTEMPTS_TXT.length()).trim());
                    }
                    else if(line.startsWith(INCORRECT_ATTEMPTS_TXT))
                    {
                        numIncorrectTwoAttempt = Integer.parseInt(line.substring(INCORRECT_ATTEMPTS_TXT.length()).trim());
                    }
                }
            }
        }

        return new Score(dateTime, numGamesPlayed, numCorrectFirstAttempt, numCorrectSecondAttempt, numIncorrectTwoAttempt);
    }

    /**
     * Returns a formattedDateTime
     *
     * @param dateTime dateTime
     * @return formattedDateTime
     */
    public static String formattedDateTime(final LocalDateTime dateTime)
    {
        final DateTimeFormatter formatter;
        final String formattedDateTime;

        formatter = DateTimeFormatter.ofPattern(DATE_TIME_FMT);
        formattedDateTime = dateTime.format(formatter);

        return formattedDateTime;
    }

    /**
     * Returns if the score is a high score
     *
     * @param score score
     * @return true if the score is greater than all the other scores
     */
    public static boolean isHighScore(final Score score)
    {
        final List<Score> otherScores;

        otherScores = readScoresFromFile(SCORE_PATH);

        return otherScores.stream()
                .mapToInt(Score::getScore)
                .max()
                .orElse(Integer.MIN_VALUE) < score.getScore();

    }

    /**
     * ToString method
     *
     * @return scoreInfoStr
     */
    public String toString()
    {
        final StringBuilder builder;
        final String scoreInfoStr;

        builder = new StringBuilder();

        builder.append(DATE_TIME_TXT);
        builder.append(formattedDateTime(dateTimePlayed));

        builder.append("\n" + GAMES_PLAYED_TXT);
        builder.append(numGamesPlayed);

        builder.append("\n" + CORR_FIRST_ATTEMPTS_TXT);
        builder.append(numCorrectFirstAttempt);

        builder.append("\n" + CORR_SECOND_ATTEMPTS_TXT);
        builder.append(numCorrectSecondAttempt);

        builder.append("\n" + INCORRECT_ATTEMPTS_TXT);
        builder.append(numIncorrectTwoAttempt);

        builder.append("\nScore: ");
        builder.append(getScore());
        builder.append(" points\n");

        scoreInfoStr = builder.toString();
        return scoreInfoStr;
    }
}
