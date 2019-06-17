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

    private ArrayList<Die> dice = new ArrayList<>(NR_OF_DICE);
    private ArrayList<Score> availableScoreChoices = new ArrayList<>(Arrays.asList(Score.values()));

    private int mRollsLeft;
    private int mCurrentRound;
    private int[] mRoundScore;
    private Score[] mRoundScoreChoices;
    private int mTotalScore;
    private int mTempRoundScore;
    private Score mChosenScore = null;
    private Score mBestScoreChoice = null;

    // Used for score calculations
    private ArrayList<Die> diceUsedForThisCalc;
    private ArrayList<Die> countedDice;
    private ArrayList<ArrayList<Die>> countedDiceCombos;


    private final String TAG = "------DiceGame";

    DiceGame() {
        resetGame();
    }


    void setDebugDice() { // TODO debug
//        int[] arr = {6, 5, 5, 3, 2, 1 }; // 7 -> 6+1 + 5+2 == 14
//         int[] arr = {5, 4, 3, 2, 2, 2}; // 4 -> 4 + 2+2 == 8
         int[] arr = {6, 6, 4, 3, 2, 2}; // 10 -> 6+4 + 6+2+2 == 20

        for(int i = 0; i < 6; i ++) {
            dice.get(i).setDie(arr[i]);
        }
    }

    void rollDice() {
        boolean usePresetDice = false; // TODO change to use dice from the debugging method

        if(!isRoundOver()) {
            if(usePresetDice) { // TODO remove debug when done
                setDebugDice();
            }
            else {
                for(Die die : dice) {
                    if(die.isEnabled()) {
                        die.rollDie();
                    }
                }
            }
            mRollsLeft--;
        }
    }

    int getRollsLeft() {
        return mRollsLeft;
    }

    int getCurrentRound() {
        return mCurrentRound;
    }

    ArrayList<Die> getDice() {
        return dice;
    }

    void newRound() {
        mRollsLeft = MAX_ROLLS - 1;
        getNewDice();
    }

    void resetGame() {
        mCurrentRound = 0;
        mTempRoundScore = 0;
        mRoundScore = new int[MAX_ROUNDS];
        mRoundScoreChoices = new Score[MAX_ROUNDS];

        newRound();
    }

    boolean isRoundOver() {
        return mRollsLeft == 0;
    }

    boolean isOver() {
        return mCurrentRound == MAX_ROUNDS;
    }

    /**
     * Records round score, score choice, removes score choice for next round, and sets round to +1
     */
    void setScore() {
        mTotalScore += mTempRoundScore;
        mRoundScore[mCurrentRound] = mTempRoundScore;
        mRoundScoreChoices[mCurrentRound] = mChosenScore;
        availableScoreChoices.remove(mChosenScore);
        mCurrentRound++;
        Log.d("-", "==============================================");
        Log.d("-", "Round " + mCurrentRound);
        Log.d("-", "==============================================");

        mBestScoreChoice = null;
    }

    int getTotalScore() {
        return mTotalScore;
    }

    /**
     * Finds the best score for the rolled dice given the available scoring choices
     * @return best found score
     */
    int calcBestScore() {
        int bestScore = 0;
        ArrayList<ArrayList<Die>> bestScoreDiceCombos = null;

        for(Score score : availableScoreChoices) {
            int thisScore = calcScore(score);
            if(thisScore > bestScore) {
                bestScore = thisScore;
                mBestScoreChoice = score;
                bestScoreDiceCombos = new ArrayList<>(countedDiceCombos);
            }
        }
        return bestScore;
    }


    /**
     * Returns the best score choice. calcBestScoreChoice has to be run before this method
     * @return
     */
    Score getBestScoreChoice() {
        Log.d("-", "--------------------------------------");
        Log.d(TAG, "BEST score choice " + mBestScoreChoice);
        return mBestScoreChoice;
    }

    //TODO implementation av low
    //TODO ta bort alternativ som använts
    //TODO mer buggtestning av poängräkning
    //TODO visa tärningskombinationer som användes för poängräkning
    //TODO (användarvänlight) när allt funkar - rekommenderat val. beräkna roundscore för alla poängräkningar och föreslå det bästa
    int calcScore(Score scoreChoice) {
        mChosenScore = scoreChoice;
        Collections.sort(dice, (d1, d2) -> d1.getValue() > d2.getValue() ? -1 : 1);

        mTempRoundScore = 0;
        countedDice = new ArrayList<>();
        countedDiceCombos = new ArrayList<>();

        for(int i = 0; i < dice.size(); i ++) {
            diceUsedForThisCalc = new ArrayList<>();
            if(!countedDice.contains(dice.get(i)) && dice.get(i).getValue() <= scoreChoice.getValue()) {
                diceUsedForThisCalc.add(dice.get(i));
                doRecursiveCalc(dice.get(i), 0, dice.get(i).getValue(), scoreChoice.getValue());
            }
        }

        Log.d(TAG, "round score for "+ scoreChoice + "(" + scoreChoice.getValue() + "): " + mTempRoundScore + " through " + countedDiceCombos.toString());

        return mTempRoundScore;
    }

    ArrayList<Score> getAvailableScoreChoices() {
        return availableScoreChoices;
    }


    private void doRecursiveCalc(Die baseDie, int i, int score, final int CHOSEN_SCORE) {
        if(score == CHOSEN_SCORE) {
            mTempRoundScore += score;
            countedDice.addAll(diceUsedForThisCalc);
            countedDiceCombos.add(diceUsedForThisCalc);
            return;
        }

        if(i == dice.size()) {
            return;
        }

        Die thisDie = dice.get(i);
        if(thisDie != baseDie) {
            if(!countedDice.contains(thisDie)) {
                score += thisDie.getValue();
                diceUsedForThisCalc.add(thisDie);
            }

            if(score > CHOSEN_SCORE) {
                score -= thisDie.getValue();
                diceUsedForThisCalc.remove(thisDie);
            }
        }

        doRecursiveCalc(baseDie, ++ i, score, CHOSEN_SCORE);
    }


    private void getNewDice() {
        dice.clear();
        for(int i = 0; i < NR_OF_DICE; i ++) {
            dice.add(new Die());
        }
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

