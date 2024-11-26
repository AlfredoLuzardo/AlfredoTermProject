import java.time.LocalDateTime;

/**
 * Score class
 *
 * @author Alfredo Luzardo
 * @version 1.0
 */
public class Score
{
    private static final int FIRST_ATTEMPT_VAL      = 2;
    private static final int SECOND_ATTEMPT_VAL     = 1;
    private static final int TOTAL_SCORE_INITIAL    = 0;

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
    private int getScore()
    {
        final int totalScoreFirstAttempt;
        final int totalScoreSecondAttempt;
        int totalScore;

        totalScore = TOTAL_SCORE_INITIAL;
        totalScoreFirstAttempt = FIRST_ATTEMPT_VAL * numCorrectFirstAttempt;
        totalScoreSecondAttempt = SECOND_ATTEMPT_VAL * numCorrectSecondAttempt;

        totalScore += totalScoreFirstAttempt + totalScoreSecondAttempt;

        return totalScore;
    }

    private void appendScoreToFile()
    {

    }

    private void readScoresFromFile()
    {

    }

}
