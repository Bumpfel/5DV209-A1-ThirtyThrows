package eren0045.assignment1;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DiceGame implements Parcelable {

    final int MAX_ROLLS = 3;
    final int MAX_ROUNDS = 10;
    final int NR_OF_DICE = 6;

    private ArrayList<Die> mDice = new ArrayList<>(NR_OF_DICE);
    private ArrayList<ScoreChoice> mAvailableScoreChoices;

    private boolean mIsStarted = false;
    private int mRollsLeft;
    private int mCurrentRound;
    private int mTotalPoints;
    private int mTempRoundPoints;
    private ScoreChoice mScoreChoice = null;
    private ScoreChoice mBestScoreChoice = null;

    // Used to store scoring records
    private int[] mRoundPoints = new int[MAX_ROUNDS];
    private ScoreChoice[] mRoundScoreChoices = new ScoreChoice[MAX_ROUNDS];

    // Used for score calculations
    private ArrayList<Die> mDiceUsedForThisCalc;
    private ArrayList<Die> mTempCountedDice;
    private ArrayList<ArrayList<Die>> mTempCountedDiceCombos;
    private ArrayList<ArrayList<Die>> mBestScoreChoiceCountedDiceCombos;


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
            mIsStarted = true;
            mRollsLeft = MAX_ROLLS - 1; // TODO (något osnyggt att ha -1)
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
     *
     * @throws IllegalMethodCallException if method is called when round is over. Use isRoundOver() to avoid this exception
     */
    public void rollDice() throws IllegalMethodCallException {
        boolean usePresetDice = false; // TODO change to true to use dice from the debugging method. remove when done

        if(!isRoundOver()) {
            if(usePresetDice) { // TODO remove debug when done
                setDebugDice();
            }
            else {
                int i = 1;
                for(Die die : mDice) {
                    if(die.isEnabled()) {
                        die.roll();
                    }
                }
            }
            mRollsLeft--;
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

    public Iterable<Die> getDice() {
        return mDice;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    public boolean isRoundOver() { // TODO not sure if this needs to be public
        return mRollsLeft == 0;
    }

    public boolean isOver() { // TODO not sure if this needs to be public
        return mCurrentRound == MAX_ROUNDS;
    }

    /**
     * Records round points, score choice, removes score choice for next round, and sets round to +1
     */
    public void setScore() {
//        if(mRoundPoints[mCurrentRound] == 0)
        mTotalPoints += mTempRoundPoints;
        mRoundPoints[mCurrentRound] = mTempRoundPoints;
        mRoundScoreChoices[mCurrentRound] = mScoreChoice;
        mAvailableScoreChoices.remove(mScoreChoice);
//        Log.d(TAG, mAvailableScoreChoices.toString());
        Log.e(TAG,  mScoreChoice + " removed");
        mCurrentRound++;
//        Log.d("-", "==============================================");
//        Log.d("-", "Round " + mCurrentRound);
//        Log.d("-", "==============================================");

        mBestScoreChoice = null;
    }


    ArrayList<ScoreChoice> getAvailableScoreChoices() {
        return new ArrayList<>(mAvailableScoreChoices);
    }

    public int getTotalPoints() {
        return mTotalPoints;
    }

    /**
     * Returns the best score choice. getPoints has to be run before this method
     * @return
     */
    public ScoreChoice getBestScoreChoice() {
//        Log.d("-", "--------------------------------------");
//        Log.d(TAG, "BEST score choice " + mBestScoreChoice);
        return mBestScoreChoice;
    }


    /**
     * Finds the highest points for the rolled dice given the available scoring choices in descending order
     * @return highest possible points for the current dice
     */
    public int getHighestPoints() {
        int highestPoints= 0;

        for(ScoreChoice scoreChoice : mAvailableScoreChoices) {
            int thisPoints;
            thisPoints = getPoints(scoreChoice);
            if(thisPoints > highestPoints) {
                highestPoints = thisPoints;
                mBestScoreChoice = scoreChoice;
                mBestScoreChoiceCountedDiceCombos = new ArrayList<>(mTempCountedDiceCombos);
            }
        }
        return highestPoints;
    }

//    //TODO inte jättebra namn
//    public ArrayList<ArrayList<Die>> getCountedDice(ScoreChoice scoreChoice) { //TODO temp return type
//        getPoints(scoreChoice);// TODO inte snyggt
//        return mTempCountedDiceCombos;
//    }

    public String getCountedDiceForBestScoreChoice() { // TODO is it allowed to format this in the model?
        StringBuilder str = new StringBuilder();
        for(ArrayList list : mBestScoreChoiceCountedDiceCombos) {
            for(Object obj : list) {
                Die die = (Die) obj;
                str.append(die.getValue() + "+");
            }
            str.setLength(str.length() - 1);
            str.append(", ");
        }
        str.setLength(str.length() - 1);

        return str.toString();
    }



    //TODO mer buggtestning av poängräkning
    //TODO visa tärningskombinationer som användes för poängräkning (i UI't)
    public int getPoints(ScoreChoice scoreChoice) {
        mScoreChoice = scoreChoice;
        Collections.sort(mDice, (d1, d2) -> d1.getValue() > d2.getValue() ? -1 : 1);

        mTempRoundPoints = 0;
        mTempCountedDice = new ArrayList<>();
        mTempCountedDiceCombos = new ArrayList<>();

        // Calculation for choice LOW
        if(scoreChoice == ScoreChoice.LOW) {
            mDiceUsedForThisCalc = new ArrayList<>();
            for (Die die : mDice) {
                if (die.getValue() <= 3) {
                    mTempRoundPoints += die.getValue();
                    mDiceUsedForThisCalc.add(die);
                }
            }
            mTempCountedDiceCombos.add(mDiceUsedForThisCalc);
        }
        // Calculation for choice that isn't LOW
        else {
            for (int i = 0; i < mDice.size(); i++) {
                Die thisDie = mDice.get(i);
                mDiceUsedForThisCalc = new ArrayList<>();
                if (!mTempCountedDice.contains(thisDie) && thisDie.getValue() <= scoreChoice.getValue()) {
                    mDiceUsedForThisCalc.add(thisDie);
                    doRecursiveCalc(thisDie, 0, thisDie.getValue(), scoreChoice.getValue());
                }
            }
        }

//        Log.d(TAG, "round score for "+ scoreChoice + "(" + scoreChoice.getValue() + "): " + mTempRoundPoints + " through " + mTempCountedDiceCombos.toString());

        return mTempRoundPoints;
    }

    private void doRecursiveCalc(final Die BASE_DIE, int dieNr, int countedPoints, final int SCORE_CHOICE_VALUE) {
        if(countedPoints == SCORE_CHOICE_VALUE) {
            mTempRoundPoints += countedPoints;
            mTempCountedDice.addAll(mDiceUsedForThisCalc);
            mTempCountedDiceCombos.add(mDiceUsedForThisCalc);
            return;
        }

        if(dieNr == mDice.size()) {
            return;
        }

        Die thisDie = mDice.get(dieNr);
        if(thisDie != BASE_DIE) {
            if(!mTempCountedDice.contains(thisDie)) { //TODO don't understand why I can't combine these two if blocks
                countedPoints += thisDie.getValue();
                mDiceUsedForThisCalc.add(thisDie);
            }

            if(countedPoints > SCORE_CHOICE_VALUE) { //&& mDiceUsedForThisCalc.contains(thisDie)
                countedPoints -= thisDie.getValue();
                mDiceUsedForThisCalc.remove(thisDie);
            }
        }

        doRecursiveCalc(BASE_DIE, ++ dieNr, countedPoints, SCORE_CHOICE_VALUE);
    }


    int[] getRoundScores() {
        return mRoundPoints;
    }

    ScoreChoice[] getRoundScoreChoices() {
        return mRoundScoreChoices;
    }


    /* **************** */
    /* Parcelable stuff */
    /* **************** */
    private int mData;

    // used to write object to Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    // constructor used by Creator
    private DiceGame(Parcel in) {
        mData = in.readInt();
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

