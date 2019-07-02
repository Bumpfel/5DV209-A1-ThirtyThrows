package eren0045.assignment1.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ThirtyThrowsGame implements Parcelable {

    // All available score choices. Made in an internal enum since its's only used by this particular game
    public enum ScoreChoice {
        LOW(0),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        ELEVEN(11),
        TWELVE(12),
        ;

        private int value;

        ScoreChoice(int n) {
            value = n;
        }

        public int getValue() {
            return value;
        }
    }

    private ThirtyThrowsScoreCalculator scoreCalculator = new ThirtyThrowsScoreCalculator();

    private final int MAX_ROLLS = 3;
    private final int MAX_ROUNDS = 10;
    private final int NR_OF_DICE = 6;

    private ArrayList<Die> mDice = new ArrayList<>(NR_OF_DICE);
    private ArrayList<ScoreChoice> mAvailableScoreChoices = new ArrayList<>();

    private boolean mHasStarted = false;
    private int mRollsLeft;
    private int mCurrentRound;
    private int mTotalPoints;

    // Used to store scoring records
    private int[] mRoundPoints = new int[MAX_ROUNDS];
    private String[] mRoundScoreChoices = new String[MAX_ROUNDS];

    public ThirtyThrowsGame() {
        for(int i = 0; i < NR_OF_DICE; i ++) {
            mDice.add(new Die());
        }

//        for(ScoreChoice choice : ScoreChoice.values()) {
//            mAvailableScoreChoices.add(choice);
//        }
        mAvailableScoreChoices.addAll(Arrays.asList(ScoreChoice.values()));
        Collections.reverse(mAvailableScoreChoices);
    }


    /**
     * Starts a new round
     * @throws IllegalMethodCallException if method is called when round is not over. Use isRoundOver() to avoid this exception
     */
    public void newRound() throws IllegalMethodCallException {
        if(isRoundOver()) {
            mCurrentRound++;
            mHasStarted = true;
            mRollsLeft = MAX_ROLLS - 1;
            resetDice();
        }
        else
            throw new IllegalMethodCallException("Cannot start new round until round is finished");
    }


    /**
     * Rolls all dice (that aren't disabled)
     * @throws IllegalMethodCallException if method is called when round is over. Use isRoundOver() to avoid this exception
     */
    public void rollDice() throws IllegalMethodCallException {

        if(!isRoundOver()) {
            for(Die die : mDice) {
                die.roll();
            }
            mRollsLeft --;
        }
        else
            throw new IllegalMethodCallException("Cannot roll dice. Round is over");
    }

    /**
     * Toggles whether the die should be rolled the next time or not (used to avoid a direct link between the Die class and the user)
     * @param die the die to be toggled
     */
    public void toggleDie(Die die) {
        if(mDice.contains(die))
            die.toggleDie();
    }

    /**
     * resets all dice
     */
    private void resetDice() {
        for(Die die : mDice) {
            die.reset();
        }
    }

    /**
     * @return nr of rolls left of the current game
     */
    public int getRollsLeft() {
        return mRollsLeft;
    }

    /**
     * @return current round nr
     */
    public int getCurrentRound() {
        return mCurrentRound;
    }

    /**
     * @return the game dice (a copy)
     */
    public ArrayList<Die> getDice() {
        return new ArrayList<>(mDice);
    }

    /**
     * @return Tells whether game has started or not
     */
    public boolean hasStarted() {
        return mHasStarted;
    }

    /**
     * @return Tells whether current round is over or not
     */
    public boolean isRoundOver() {
        return mRollsLeft == 0;
    }

    /**
     * @return Tells whether a score has been chosen for the current round or not
     */
    public boolean isRoundScored() {
        return mAvailableScoreChoices.size() == (MAX_ROUNDS - mCurrentRound) && hasStarted();
    }

    /**
     * @return Tells whether the game has ended or not
     */
    public boolean isOver() {
        return mCurrentRound == MAX_ROUNDS;
    }

    /**
     * Records round points, round score choice, and removes score choice for next round
     */
    public void setScore(ScoreChoice scoreChoice) {
        if(mAvailableScoreChoices.contains(scoreChoice)) {
            int points = getPoints(scoreChoice, new ArrayList<>());
            mTotalPoints += points;
            mRoundPoints[mCurrentRound - 1] = points;
            mRoundScoreChoices[mCurrentRound - 1] = scoreChoice.toString();
            mAvailableScoreChoices.remove(scoreChoice);
        }
        else
            throw new IllegalMethodCallException("Score choice not available. Use getAvailableScoreChoices() to get available score choices");
    }

    /**
     * Returns score choices (a copy) that hasn't been chosen this game
     * @return available score choices
     */
    public ArrayList<ScoreChoice> getAvailableScoreChoices() {
        return new ArrayList<>(mAvailableScoreChoices);
    }

    /**
     * Returns the total points for all rounds
     * @return total points
     */
    public int getTotalPoints() {
        return mTotalPoints;
    }


    /**
     * Finds the score that yields the most points for the rolled dice given the available scoring choices in descending order
     * @return "best" score choice
     */
    public ScoreChoice getBestScoreChoice() {
        int highestPoints = 0;
        ScoreChoice bestScoreChoice = mAvailableScoreChoices.get(0);

        int thisPoints;
        for(ScoreChoice scoreChoice : mAvailableScoreChoices) {
            thisPoints = getPoints(scoreChoice, new ArrayList<>());
            if(thisPoints > highestPoints) {
                highestPoints = thisPoints;
                bestScoreChoice = scoreChoice;
            }

        }
        return bestScoreChoice;
    }

    /**
     * Returns the total points for the chosen score with the rolled dice
     * @param scoreChoice the chosen score
     * @param diceCombos combinations used will be written to this parameter. Note: this is meant to be used as a return value
     * @return total points for the chosen score
     */
    public int getPoints(ScoreChoice scoreChoice, ArrayList<ArrayList<Die>> diceCombos) {
        return scoreCalculator.calculatePoints(scoreChoice, new ArrayList<>(mDice), diceCombos);
    }


    /**
     * Returns the points for each round
     * @return the points for each round (a copy)
     */
    public int[] getRoundPoints() {
        return Arrays.copyOf(mRoundPoints, mRoundPoints.length);
    }

    /**
     * Returns the score choices made for each round
     * @return the score choices for each round (a copy)
     */
    public String[] getRoundScoreChoices() {
        return Arrays.copyOf(mRoundScoreChoices, mRoundScoreChoices.length);
    }


    /* **************** */
    /* Parcelable stuff */
    /* **************** */

    // used to write object to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mTotalPoints);
        dest.writeIntArray(mRoundPoints);
        dest.writeStringArray(mRoundScoreChoices);

        dest.writeInt(mHasStarted ? 1 : 0);
        dest.writeInt(mRollsLeft);
        dest.writeInt(mCurrentRound);

        Die[] tempDice = new Die[mDice.size()];
        dest.writeTypedArray(mDice.toArray(tempDice),0);

        // save available score choices left as enum ordinals
        int[] tempOrdinals = new int[mAvailableScoreChoices.size()];
        for(int i = 0; i < mAvailableScoreChoices.size(); i ++) {
            tempOrdinals[i] = mAvailableScoreChoices.get(i).ordinal();
        }
        dest.writeIntArray(tempOrdinals);
    }

    // constructor used by Creator
    private ThirtyThrowsGame(Parcel in) {
        mTotalPoints = in.readInt();
        mRoundPoints = in.createIntArray();
        mRoundScoreChoices = in.createStringArray();

        mHasStarted = in.readInt() == 1;
        mRollsLeft = in.readInt();
        mCurrentRound = in.readInt();

        Die[] temp = in.createTypedArray(Die.CREATOR);
        mDice.addAll(Arrays.asList(temp));

        // restore score choice arraylist
        int[] ordinals = in.createIntArray();
        for(int i = 0; i < ordinals.length; i ++) {
            ScoreChoice choice = ScoreChoice.values()[ordinals[i]];
            mAvailableScoreChoices.add(choice);
        }
    }

    // used to restore object from parcel
    public static final Creator<ThirtyThrowsGame> CREATOR = new Creator<ThirtyThrowsGame>() {

        //calls private constructor
        public ThirtyThrowsGame createFromParcel(Parcel in) {
            return new ThirtyThrowsGame(in);
        }

        public ThirtyThrowsGame[] newArray(int size) {
            return new ThirtyThrowsGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}

