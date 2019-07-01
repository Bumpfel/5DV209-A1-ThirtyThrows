package eren0045.assignment1.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

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

    private final int MAX_ROLLS = 3;
    private final int MAX_ROUNDS = 10;
    private final int NR_OF_DICE = 6;

    private ArrayList<Die> mDice = new ArrayList<>(NR_OF_DICE);
    private ArrayList<ScoreChoice> mAvailableScoreChoices;    // TODO ev ändra till LinkedList för ngt bättre prestanda

    private boolean mHasStarted = false;
    private int mRollsLeft;
    private int mCurrentRound;
    private int mTotalPoints;

    // Used to store scoring records
    private int[] mRoundPoints = new int[MAX_ROUNDS];
    private String[] mRoundScoreChoices = new String[MAX_ROUNDS];


    private final String TAG = "------ThirtyThrowsGame";

    public ThirtyThrowsGame() {
        for(int i = 0; i < NR_OF_DICE; i ++) {
            mDice.add(new Die());
        }

        mAvailableScoreChoices = new ArrayList<>(Arrays.asList(ScoreChoice.values()));
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
            throw new IllegalMethodCallException("Cannot create new round until round is finished");
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
     * @return The game dice (a copy)
     */
    public ArrayList<Die> getDice() {
        return new ArrayList<>(mDice);
    }

    /**
     * @return whether game has started or not
     */
    public boolean hasStarted() {
        return mHasStarted;
    }

    /**
     * @return whether current round is over or not
     */
    public boolean isRoundOver() {
        return mRollsLeft == 0;
    }

    /**
     * @return whether a score has been chosen for the current round or not
     */
    public boolean isRoundScored() {
        return mAvailableScoreChoices.size() == (MAX_ROUNDS - mCurrentRound) && hasStarted();
    }

    /**
     * @return whether game has ended or not
     */
    public boolean isOver() {
        return mCurrentRound == MAX_ROUNDS;
    }

    /**
     * Records round points, round score choice, and removes score choice for next round
     */
    public void setScore(ScoreChoice scoreChoice) {
        if(mAvailableScoreChoices.contains(scoreChoice)) {
            int points = getPoints(scoreChoice);
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
     * Finds the score that yields the highest points for the rolled dice given the available scoring choices in descending order
     * @return highest score
     */
    public ScoreChoice getBestScoreChoice() {
        int highestPoints = 0;
        ScoreChoice bestScoreChoice = mAvailableScoreChoices.get(0);

        int thisPoints;
        for(ScoreChoice scoreChoice : mAvailableScoreChoices) {
            thisPoints = getPoints(scoreChoice);
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
     * @return total points for the chosen score
     */
    public int getPoints(ScoreChoice scoreChoice) {
        int points = 0;

        // Calculation for choice LOW
        if(scoreChoice == ScoreChoice.LOW) {
            for (Die die : mDice) {
                if (die.getValue() <= 3) {
                    points += die.getValue();
                }
            }
        }
        // Calculation for choice that isn't LOW
        else {
            int startingDiceAmount = 1;
            if(scoreChoice.getValue() > 6)
                startingDiceAmount = 2;
            ArrayList<Die> countedDice = findDiceCombinations(scoreChoice.getValue(), 0, 0, startingDiceAmount, new Stack<>(), new ArrayList<>(mDice), new ArrayList<>());
            points = countDiceValues(countedDice);
       }

        return points;
    }

    /**
     * Recursive method that automatically finds the combinations of the given dice for the supplied score choice (prioritizes searching for combinations with as few dice as possible)
     * @param SCORE_CHOICE_VALUE the chosen score
     * @param dieIndex used internally in calculation. should start on 0
     * @param startDieIndex used internally in calculation. should start on 0
     * @param diceToUseAmount nr of dice to count with. should start on 1 (or possibly 2 if scorehoice is above 6 and cannot be produced by only one die)
     * @param diceUsed used for calculation. start with an empty arraylist
     * @param dice the available dice. will be modified (send a copy)
     * @param countedDice dice that has been counted in a valid combination. start with an empty arraylist
     * @return all the dice that is a part of valid combinations
     */
    private ArrayList<Die> findDiceCombinations(final int SCORE_CHOICE_VALUE, int dieIndex, int startDieIndex, int diceToUseAmount, Stack<Die> diceUsed, ArrayList<Die> dice, ArrayList<Die> countedDice) {
        Die currentDie;

        // add the amount of dice that are supposed to be used for the calculation
        while(diceToUseAmount > diceUsed.size() && dieIndex < dice.size()) {
            currentDie = dice.get(dieIndex ++);
            diceUsed.push(currentDie);
        }
        int points = countDiceValues(diceUsed);

        // combination found
        if(points == SCORE_CHOICE_VALUE) {
            countedDice.addAll(diceUsed);
//            countedCombos.add(new ArrayList<>(diceUsed));
            while(!diceUsed.isEmpty()) {
                Die tempDie = diceUsed.pop();
                startDieIndex = dice.indexOf(tempDie);
                dice.remove(tempDie);
            }
            dieIndex = startDieIndex;
        }
        // combination not valid, remove last entry
        if(points > SCORE_CHOICE_VALUE || diceUsed.size() == diceToUseAmount) {
            diceUsed.pop();

            if (diceUsed.isEmpty()) // only used for the single dice tests to avoid going through them more than once
                startDieIndex++;
        }

        // added last dice of the "series" - do resets
        if(dieIndex == dice.size()) {
            if(!diceUsed.isEmpty()) {
                diceUsed.pop();
            }

            if(startDieIndex >= dice.size() - 1 || diceToUseAmount > dice.size() - startDieIndex + diceUsed.size()) {
                // all combinations with the amount of dice exhausted. increase nr of dice to use
                if(diceUsed.isEmpty()) {
                    startDieIndex = diceToUseAmount - 1;
                    dieIndex = 0;
                    diceToUseAmount ++;
                    // no longer possible to find a valid combination
                    if(diceToUseAmount > dice.size())
                        return countedDice;
                    diceUsed.clear();
                }
                // more combinations with the current diceToUseAmount possible
                else {
                    dieIndex = dice.indexOf(diceUsed.peek()) + 1;

                    diceUsed.pop();
                    if(diceToUseAmount > dice.size() - dieIndex + diceUsed.size() && !diceUsed.isEmpty()) {
                        dieIndex = dice.indexOf(diceUsed.peek()) + 1;
                        diceUsed.pop();
                    }
                    startDieIndex = diceToUseAmount - 1 + dieIndex - 1;
                }
            }
            else {
                startDieIndex ++;
                dieIndex = startDieIndex;
            }
        }
        findDiceCombinations(SCORE_CHOICE_VALUE, dieIndex, startDieIndex, diceToUseAmount, diceUsed, dice, countedDice);
        return countedDice;
    }

    /**
     * Sums up the dice values in an iterable
     * @param dice the iterable you want to sum up
     * @return total value of the dice in the iterable
     */
    private int countDiceValues(Iterable<Die> dice) {
        int value = 0;
        for(Die die : dice) {
            value += die.getValue();
        }
        return value;
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
        for(Die die : temp) {
            mDice.add(die);
        }

        // restore score choice arraylist
        int[] ordinals = in.createIntArray();
        mAvailableScoreChoices = new ArrayList<>();
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

