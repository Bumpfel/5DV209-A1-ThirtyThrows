package eren0045.assignment1;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class DiceGame implements Parcelable {

    final int MAX_ROLLS = 3;
    final int MAX_ROUNDS = 10;
    final int NR_OF_DICE = 6;

    private ArrayList<Die> dice = new ArrayList<>(NR_OF_DICE);

    private int rollsLeft;
    private int currentRound = 0;
    private int[] roundScore = new int[MAX_ROUNDS];
    private int totalScore = 0;

    private ArrayList<Integer> availableDiceValues = new ArrayList<>(); // used for score calculation

    private final String TAG = "------DiceGame";

    DiceGame() {
        getNewDice();
    }

    void rollDice() {
        if(!isRoundOver()) {
            for(Die die : dice) {
                if(die.isEnabled()) {
                    die.rollDie();
                }
            }
            rollsLeft --;
        }
    }

    int getRollsLeft() {
        return rollsLeft;
    }

    int getCurrentRound() {
        return currentRound;
    }

    ArrayList<Die> getDice() {
        return dice;
    }

    void newRound() {
        rollsLeft = MAX_ROLLS - 1;
        availableDiceValues.clear();

        getNewDice();
    }

    void resetGame() {
        currentRound = 0;
        totalScore = 0;
        roundScore = new int[MAX_ROUNDS];

        newRound();
    }


    boolean isRoundOver() {
        return rollsLeft == 0;
    }

    boolean isOver() {
        return currentRound == MAX_ROUNDS;
    }

    void setScore(Score chosenScore) {
        totalScore += calcScore(chosenScore.getValue());
        currentRound ++;
    }

    int getTotalScore() {
        return totalScore;
    }


    int calcScore(int chosenScore) {
//        if(availableDiceValues.size() == 0) { // only sort it once
//            for(Die die : dice) {
//                availableDiceValues.add(die.getValue());
//            }
//            Collections.sort(availableDiceValues, Collections.reverseOrder());
//        }
        Collections.sort(dice, (d1, d2) -> d1.getValue() > d2.getValue() ? -1 : 1);

        return totalScore;
    }


//    void doRecursiveCalc(Die baseDie, int i, int tempScore, final int CHOSEN_SCORE) {
//        if(i == dice.size()) {
//            return;
//        }
//        if(tempScore == CHOSEN_SCORE) {
//            totalScore += tempScore;
//            countedDice.addAll(diceUsedForThisCalc);
//            countedDiceCombos.add(diceUsedForThisCalc);
//            return;
//        }
//
//        Die thisDie = dice.get(i);
//        if(thisDie != baseDie && !countedDice.contains(thisDie)) { //&& !diceUsedForThisCalc.contains(thisDie) <- borde inte kunna hända
//            tempScore += thisDie.value;
//            diceUsedForThisCalc.add(thisDie);
//        }
//
//        if(tempScore > CHOSEN_SCORE) {
//            tempScore = baseDie.value;
//            diceUsedForThisCalc = new ArrayList<>();
//            diceUsedForThisCalc.add(baseDie);
//        }
//
//        doRecursiveCalc(baseDie, ++ i, tempScore, CHOSEN_SCORE);
//    }


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

