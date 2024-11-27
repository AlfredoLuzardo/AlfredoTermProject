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
    private static final String SCORE_PATH          = "score.txt";
    private static final int FIRST_ATTEMPT_VAL      = 2;
    private static final int SECOND_ATTEMPT_VAL     = 1;

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
     * Get total score
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

    public static List<Score> readScoresFromFile(final String file)
    {
        final List<Score> scores;

        scores = new ArrayList<>();

        try
        {
            final Path path;
            final List<String> lines;
            final List<String> currentBlock;

            path = Paths.get(file);

            if(Files.notExists(path))
            {
                Files.createFile(path);
            }

            lines = Files.readAllLines(path);
            currentBlock = new ArrayList<>();

            if(!lines.isEmpty())
            {
                for(final String line : lines)
                {
                    if(line != null)
                    {
                        if(!line.startsWith("Total Score: "))
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

    private static Score parseScoreBlock(final List<String> scoreBlock)
    {
        final DateTimeFormatter formatter;
        LocalDateTime dateTime = null;
        int numGamesPlayed = 0;
        int numCorrectFirstAttempt = 0;
        int numCorrectSecondAttempt = 0;
        int numIncorrectTwoAttempt = 0;

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(!scoreBlock.isEmpty())
        {
            for(final String line : scoreBlock)
            {
                if(line != null)
                {
                    if(line.startsWith("Date and Time: "))
                    {
                        final String dateTimeString;

                        dateTimeString = line.substring("Date and Time: ".length()).trim();
                        dateTime = LocalDateTime.parse(dateTimeString, formatter);
                    }
                    else if(line.startsWith("Games Played: "))
                    {
                        numGamesPlayed = Integer.parseInt(line.substring("Games Played: ".length()).trim());
                    }
                    else if(line.startsWith("Correct First Attempts: "))
                    {
                        numCorrectFirstAttempt = Integer.parseInt(line.substring("Correct First Attempts: ".length()).trim());
                    }
                    else if(line.startsWith("Correct Second Attempts: "))
                    {
                        numCorrectSecondAttempt = Integer.parseInt(line.substring("Correct Second Attempts: ".length()).trim());
                    }
                    else if(line.startsWith("Incorrect Attempts: "))
                    {
                        numIncorrectTwoAttempt = Integer.parseInt(line.substring("Incorrect Attempts: ".length()).trim());
                    }
                }
            }
        }

        return new Score(dateTime, numGamesPlayed, numCorrectFirstAttempt, numCorrectSecondAttempt, numIncorrectTwoAttempt);
    }

    public LocalDateTime getDateTimePlayed()
    {
        return dateTimePlayed;
    }

    public static String formattedDateTime(final LocalDateTime dateTime)
    {
        final DateTimeFormatter formatter;
        final String formattedDateTime;

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        formattedDateTime = dateTime.format(formatter);

        return formattedDateTime;
    }

    public static boolean isHighScore(final Score score)
    {
        final List<Score> otherScores;

        otherScores = readScoresFromFile(SCORE_PATH);

        return otherScores.stream()
                .mapToInt(Score::getScore)
                .max()
                .orElse(Integer.MIN_VALUE) < score.getScore();

    }

    public static Score getHighestScore()
    {
        final List<Score> scores;
        scores = readScoresFromFile(SCORE_PATH);

        return scores.stream()
                .max(Comparator.comparingInt(Score::getScore))
                .orElse(null);
    }

    public double getAverageScore()
    {
        final double average;

        average = (double) getScore() / numGamesPlayed;

        return average;
    }

    public String toString()
    {
        final StringBuilder builder;
        final String scoreInfoStr;

        builder = new StringBuilder();

        builder.append("Date and Time: ");
        builder.append(formattedDateTime(dateTimePlayed));

        builder.append("\nGames Played: ");
        builder.append(numGamesPlayed);

        builder.append("\nCorrect First Attempts: ");
        builder.append(numCorrectFirstAttempt);

        builder.append("\nCorrect Second Attempts: ");
        builder.append(numCorrectSecondAttempt);

        builder.append("\nIncorrect Attempts: ");
        builder.append(numIncorrectTwoAttempt);

        builder.append("\nScore: ");
        builder.append(getScore());
        builder.append(" points\n");

        scoreInfoStr = builder.toString();
        return scoreInfoStr;
    }
}
