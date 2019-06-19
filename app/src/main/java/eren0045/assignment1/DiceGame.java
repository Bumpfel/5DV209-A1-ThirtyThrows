package eren0045.assignment1;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DiceGame implements Parcelable {

    //TODO score och scoreChoice är lite förvirrande. kan ev. byta till scoreChoice och points?
    final int MAX_ROLLS = 3;
    final int MAX_ROUNDS = 10;
    final int NR_OF_DICE = 6;

    private ArrayList<Die> mDice = new ArrayList<>(NR_OF_DICE);
    private ArrayList<Score> mAvailableScoreChoices;

    private boolean isStarted = false;
    private int mRollsLeft;
    private int mCurrentRound;
    private int[] mRoundScores = new int[MAX_ROUNDS];
    private Score[] mRoundScoreChoices = new Score[MAX_ROUNDS];
    private int mTotalScore;
    private int mTempRoundScore;
    private Score mChosenScore = null;
    private Score mBestScoreChoice = null;

    // Used for score calculations
    private ArrayList<Die> mDiceUsedForThisCalc;
    private ArrayList<Die> mCountedDice;
    private ArrayList<ArrayList<Die>> mCountedDiceCombos;
    private ArrayList<ArrayList<Die>> mBestScoreCountedDice;


    private final String TAG = "------DiceGame";

    public DiceGame() {
        for(int i = 0; i < NR_OF_DICE; i ++) {
            mDice.add(new Die());
        }

        // sorting list to enable the auto-score method to check the higher choices before the lower
        // (thinking generally the higher score choices are less useful than lower scores and should therefore be used first)
        mAvailableScoreChoices = new ArrayList<>(Arrays.asList(Score.values()));
        Collections.reverse(mAvailableScoreChoices);

//        newRound();
    }

    /**
     * Starts a new round
     * @throws IllegalMethodCallException if method is called when round is not over. Use isRoundOver() to avoid this exception
     */
    public void newRound() throws IllegalMethodCallException {
        if(isRoundOver()) {
            isStarted = true;
            mRollsLeft = MAX_ROLLS - 1; // TODO (något osnyggt att ha -1)
            resetDice();
        }
        else
            throw new IllegalMethodCallException("Cannot create new round until round is finished");
    }



    private void setDebugDice() { // TODO debug
//        int[] arr = {6, 5, 5, 3, 2, 1 }; // 7 -> 6+1 + 5+2 == 14
//         int[] arr = {5, 4, 3, 2, 2, 2}; // 4 -> 4 + 2+2 == 8
         int[] arr = {6, 6, 4, 3, 2, 2}; // 10 -> 6+4 + 6+2+2 == 20

        for(int i = 0; i < 6; i ++) {
            mDice.get(i).setDie(arr[i]);
        }
    }

    /**
     *
     * @throws IllegalMethodCallException if method is called when round is over. Use isRoundOver() to avoid this exception
     */
    public void rollDice() throws IllegalMethodCallException {
        boolean usePresetDice = false; // TODO change to true to use dice from the debugging method

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
        return isStarted;
    }

    public boolean isRoundOver() { // TODO not sure if this needs to be public
        return mRollsLeft == 0;
    }

    public boolean isOver() { // TODO not sure if this needs to be public
        return mCurrentRound == MAX_ROUNDS;
    }

    /**
     * Records round score, score choice, removes score choice for next round, and sets round to +1
     */
    public void setScore() {
        mTotalScore += mTempRoundScore;
        mRoundScores[mCurrentRound] = mTempRoundScore;
        mRoundScoreChoices[mCurrentRound] = mChosenScore;
        boolean removed = mAvailableScoreChoices.remove(mChosenScore);
        mCurrentRound++;
//        Log.d("-", "==============================================");
//        Log.d("-", "Round " + mCurrentRound);
//        Log.d("-", "==============================================");

        mBestScoreChoice = null;
    }


    ArrayList<Score> getAvailableScoreChoices() {
        return new ArrayList<>(mAvailableScoreChoices);
    }

    public int getTotalScore() {
        return mTotalScore;
    }

    /**
     * Returns the best score choice. calcBestScoreChoice has to be run before this method
     * @return
     */
    public Score getBestScoreChoice() {
//        Log.d("-", "--------------------------------------");
//        Log.d(TAG, "BEST score choice " + mBestScoreChoice);
        return mBestScoreChoice;
    }


    /**
     * Finds the highest score for the rolled dice given the available scoring choices in descending order
     * @return highest score
     */
    public int getHighestScore() {
        int highestScore = 0;

        for(Score score : mAvailableScoreChoices) {
            int thisScore;
            thisScore = getScore(score);
            if(thisScore > highestScore) {
                highestScore = thisScore;
                mBestScoreChoice = score;
                mBestScoreCountedDice = new ArrayList<>(mCountedDiceCombos);
            }
        }
        return highestScore;
    }

//    //TODO inte jättebra namn
//    public ArrayList<ArrayList<Die>> getCountedDice(Score scoreChoice) { //TODO temp return type
//        getScore(scoreChoice);// TODO inte snyggt
//        return mCountedDiceCombos;
//    }

    public String getCountedDiceForBestScore() { // TODO is it allowed format this in the model?
        StringBuilder str = new StringBuilder();
        for(ArrayList list : mBestScoreCountedDice) {
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
    public int getScore(Score scoreChoice) {
        mChosenScore = scoreChoice;
        Collections.sort(mDice, (d1, d2) -> d1.getValue() > d2.getValue() ? -1 : 1);

        mTempRoundScore = 0;
        mCountedDice = new ArrayList<>();
        mCountedDiceCombos = new ArrayList<>();

        // Calculation for choice LOW
        if(scoreChoice == Score.LOW) {
            mDiceUsedForThisCalc = new ArrayList<>();
            for (Die die : mDice) {
                if (die.getValue() <= 3) {
                    mTempRoundScore += die.getValue();
                    mDiceUsedForThisCalc.add(die);
                }
            }
            mCountedDiceCombos.add(mDiceUsedForThisCalc);
        }
        // Calculation for choice that isn't LOW
        else {
            for (int i = 0; i < mDice.size(); i++) {
                mDiceUsedForThisCalc = new ArrayList<>();
                if (!mCountedDice.contains(mDice.get(i)) && mDice.get(i).getValue() <= scoreChoice.getValue()) {
                    mDiceUsedForThisCalc.add(mDice.get(i));
                    doRecursiveCalc(mDice.get(i), 0, mDice.get(i).getValue(), scoreChoice.getValue());
                }
            }
        }

//        Log.d(TAG, "round score for "+ scoreChoice + "(" + scoreChoice.getValue() + "): " + mTempRoundScore + " through " + mCountedDiceCombos.toString());

        return mTempRoundScore;
    }

    private void doRecursiveCalc(Die baseDie, int i, int score, final int CHOSEN_SCORE) {
        if(score == CHOSEN_SCORE) {
            mTempRoundScore += score;
            mCountedDice.addAll(mDiceUsedForThisCalc);
            mCountedDiceCombos.add(mDiceUsedForThisCalc);
            return;
        }

        if(i == mDice.size()) {
            return;
        }

        Die thisDie = mDice.get(i);
        if(thisDie != baseDie) {
            if(!mCountedDice.contains(thisDie)) {
                score += thisDie.getValue();
                mDiceUsedForThisCalc.add(thisDie);
            }

            if(score > CHOSEN_SCORE) {
                score -= thisDie.getValue();
                mDiceUsedForThisCalc.remove(thisDie);
            }
        }

        doRecursiveCalc(baseDie, ++ i, score, CHOSEN_SCORE);
    }


    int[] getRoundScores() {
        return mRoundScores;
    }

    Score[] getRoundScoreChoices() {
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

