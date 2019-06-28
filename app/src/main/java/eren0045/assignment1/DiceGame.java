package eren0045.assignment1;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DiceGame implements Parcelable {

    private final int MAX_ROLLS = 3;
    private final int MAX_ROUNDS = 10;
    private final int NR_OF_DICE = 6;

    private ArrayList<Die> mDice = new ArrayList<>(NR_OF_DICE);
    private ArrayList<ScoreChoice> mAvailableScoreChoices;

    private boolean mIsStarted = false;
    private int mRollsLeft;
    private int mCurrentRound;
    private int mTotalPoints;

    // Used to store scoring records
    private int[] mRoundPoints = new int[MAX_ROUNDS];
    private String[] mRoundScoreChoices = new String[MAX_ROUNDS];

    // Used for score calculations
    private ArrayList<Die> mDiceUsedForThisCalc;
    private ArrayList<Die> mCountedDice;

    private final String TAG = "------DiceGame";

    public DiceGame() {
        for(int i = 0; i < NR_OF_DICE; i ++) {
            mDice.add(new Die());
        }

        // sorting list to enable the auto-score method to check the higher choices before the lower
        // (thinking generally the higher score choices are less useful than lower scores and should therefore be used first)
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
            mIsStarted = true;
            mRollsLeft = MAX_ROLLS - 1;
            resetDice();
        }
        else
            throw new IllegalMethodCallException("Cannot create new round until round is finished");
    }


    private void setDebugDice() { // TODO debug. delete when done
//        int[] arr = {6, 5, 5, 3, 2, 1 }; // 7 -> 6+1 + 5+2 == 14
//         int[] arr = {5, 4, 3, 2, 2, 2}; // 4 -> 4 + 2+2 == 8
//         int[] arr = {6, 6, 4, 3, 2, 2}; // 10 -> 6+4 + 6+2+2 == 20
        int[] arr = { 2, 6, 3, 2, 5, 2 };

        for(int i = 0; i < 6; i ++) {
            mDice.get(i).setDie(arr[i]);
        }
    }

    /**
     * Rolls all dice (that aren't disabled)
     * @throws IllegalMethodCallException if method is called when round is over. Use isRoundOver() to avoid this exception
     */
    public void rollDice() throws IllegalMethodCallException {
        boolean usePresetDice = false; // TODO change to true to use dice from the debugging method. remove when done

        if(!isRoundOver()) {
            if(usePresetDice) { // TODO remove debug when done
                setDebugDice();
            }
            else {
                for(Die die : mDice) {
                    die.roll();
                }
            }
            mRollsLeft --;
        }
        else
            throw new IllegalMethodCallException("Cannot roll dice. Round is over");
    }

    private void resetDice() {
        for(Die die : mDice) {
            die.reset();
        }
    }

    public int getRollsLeft() {
        return mRollsLeft;
    }

    public int getCurrentRound() {
        return mCurrentRound;
    }

    public ArrayList<Die> getDice() {
        return new ArrayList<>(mDice);
    } // TODO fortfarande ett problem att Die har tillgängliga metoder som modifierar tärningen. skulle kunna lägga Die (och DiceGame) i separat model-paket

    public boolean hasStarted() {
        return mIsStarted;
    }

    public boolean isRoundOver() {
        return mRollsLeft == 0;
    }

    public boolean isRoundScored() {
        return mAvailableScoreChoices.size() == (MAX_ROUNDS - mCurrentRound) && hasStarted();
    }

    public boolean isOver() {
        return mCurrentRound == MAX_ROUNDS;
    }

    /**
     * Records round points, score choice, removes score choice for next round
     */
    public void setScore(ScoreChoice scoreChoice, int points) {
        if(!Arrays.asList(mRoundScoreChoices).contains(scoreChoice)) {
            mTotalPoints += points;
            mRoundPoints[mCurrentRound - 1] = points;
            mRoundScoreChoices[mCurrentRound - 1] = scoreChoice.toString();
            mAvailableScoreChoices.remove(scoreChoice);
        }
    }


    public ArrayList<ScoreChoice> getAvailableScoreChoices() { // TODO behövs kanske inte?
        return new ArrayList<>(mAvailableScoreChoices);
    }

    public int getTotalPoints() {
        return mTotalPoints;
    }


    /**
     * Finds the score that yields the highest points for the rolled dice given the available scoring choices in descending order
     * @return highest score
     */
    public ScoreChoice getBestScoreChoice() {
        Log.e(TAG, "==============================================");
        Log.e(TAG, "Round " + mCurrentRound);
        Log.e(TAG, "==============================================");

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
        Log.e("-", "--------------------------------------");
        Log.d(TAG, "BEST score option " + bestScoreChoice);
        return bestScoreChoice;
    }


    //TODO behöver nog göra om lite hur poängen sparas. känns som ett bättre sätt att returnera värden istället för att spara allt i globala variabler. särskilt eftersom det buggar ibland
    //TODO (viktigt) poängräkning funkar inte alltid med tre tärningar (t.ex. 6,5,4,4,4,3
    public int getPoints(ScoreChoice scoreChoice) {
        Collections.sort(mDice, (d1, d2) -> d1.getValue() > d2.getValue() ? -1 : 1);

        int points = 0;
        mCountedDice = new ArrayList<>();

        // Calculation for choice LOW
        if(scoreChoice == ScoreChoice.LOW) {
            mDiceUsedForThisCalc = new ArrayList<>();
            for (Die die : mDice) {
                if (die.getValue() <= 3) {
                    points += die.getValue();
                    mDiceUsedForThisCalc.add(die);
                }
            }

        }
        // Calculation for choice that isn't LOW
        else {
            for (int i = 0; i < mDice.size(); i++) {
                Die thisDie = mDice.get(i);
                mDiceUsedForThisCalc = new ArrayList<>();
                if (!mCountedDice.contains(thisDie) && thisDie.getValue() <= scoreChoice.getValue()) {
                    mDiceUsedForThisCalc.add(thisDie);
                    points += countRoundPoints(thisDie, 0, thisDie.getValue(), scoreChoice.getValue());
                }
            }
        }

//        Log.d(TAG, "round score for "+ scoreChoice + "(" + scoreChoice.getValue() + "): " + points + " through " + mTempCountedDiceCombos.toString());

        return points;
    }

    private int countRoundPoints(final Die BASE_DIE, int dieNr, int countedPoints, final int SCORE_CHOICE_VALUE) {
        if(countedPoints == SCORE_CHOICE_VALUE) {
            mCountedDice.addAll(mDiceUsedForThisCalc);
            return countedPoints;
        }

        if(dieNr == mDice.size()) {
            return 0;
        }

        Die thisDie = mDice.get(dieNr);
        if(thisDie != BASE_DIE) {
            if(!mCountedDice.contains(thisDie)) { //TODO don't understand why I can't combine these two if blocks
                countedPoints += thisDie.getValue();
                mDiceUsedForThisCalc.add(thisDie);
            }

            if(countedPoints > SCORE_CHOICE_VALUE) { //&& mDiceUsedForThisCalc.contains(thisDie)
                countedPoints -= thisDie.getValue();
                mDiceUsedForThisCalc.remove(thisDie);
            }
        }

        return countRoundPoints(BASE_DIE, ++ dieNr, countedPoints, SCORE_CHOICE_VALUE);
    }

    public int[] getRoundPoints() {
        return Arrays.copyOf(mRoundPoints, mRoundPoints.length);
    }

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
    }

    // constructor used by Creator
    private DiceGame(Parcel in) {
        mTotalPoints = in.readInt();
        mRoundPoints = in.createIntArray();
        mRoundScoreChoices = in.createStringArray();
    }

    // used to restore object from parcel
    public static final Creator<DiceGame> CREATOR = new Creator<DiceGame>() {

        public DiceGame createFromParcel(Parcel in) {
            return new DiceGame(in);
        }

        public DiceGame[] newArray(int size) {
            return new DiceGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}

