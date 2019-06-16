package eren0045.assignment1;

import android.util.Log;

import java.util.Collection;

class GameRules {

    static final int MAX_ROLLS = 3;
    static final int MAX_ROUNDS = 10;

    private static final String TAG = "------GameRules";

    static int calcScore(int chosenScore, Collection<Die> dice) {
        Log.d(TAG, "" + chosenScore);

        int totalScore = 0;
        int tempScore;
        for(Die die : dice) {
            tempScore = die.getValue();
            if(tempScore > chosenScore) {
                continue;
            }
            else if(tempScore == chosenScore) {
                totalScore += die.getValue();
                continue;
            }
            for(Die nextDie: dice) {
                tempScore += nextDie.getValue();
                if(tempScore > chosenScore)
                    tempScore -= nextDie.getValue();
                else if(tempScore == chosenScore)
                    break;
            }
        }

        return totalScore;
    }


    static int old_calcScore(Score chosenScore, Collection<Die> dice) {
        int wrongScore = 0;
        for(Die die : dice) {
            wrongScore += die.getValue();
        }

        return wrongScore;
    }

}

