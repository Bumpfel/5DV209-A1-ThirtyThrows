package eren0045.assignment1.model;

import java.util.ArrayList;
import java.util.Stack;


// I made this class since the game class was getting very big, and also, this class would not be needed if one wanted to disable the automatic score calculation
public class ThirtyThrowsScoreCalculator {

    /**
     * Returns the total points for the chosen score with the rolled dice
     * @param scoreChoice the chosen score
     * @param gameDice the rolled game dice
     * @param diceCombos combinations used will be written to this parameter. Note: this is meant to be used as a return value
     * @return total points for the chosen score
     */
    int calculatePoints(ThirtyThrowsGame.ScoreChoice scoreChoice, ArrayList<Die> gameDice, ArrayList<ArrayList<Die>> diceCombos) {
        int points = 0;
        ArrayList<Die> countedDice = new ArrayList<>();
        ArrayList<ArrayList<Die>> countedDiceCombos;

        // Calculation for choice LOW
        if(scoreChoice == ThirtyThrowsGame.ScoreChoice.LOW) {
            for (Die die : gameDice) {
                if (die.getValue() <= 3) {
                    points += die.getValue();
                    countedDice.add(die);
                }
            }
            countedDiceCombos = new ArrayList<>();
            countedDiceCombos.add(countedDice);
        }
        // Calculation for choice that isn't LOW
        else {
            countedDiceCombos = getDiceCombinations(scoreChoice, gameDice);

            for(ArrayList<Die> diceCombo : countedDiceCombos) {
                points += countDiceValues(diceCombo);
            }
        }
        diceCombos.addAll(countedDiceCombos);

        return points;
    }

    /**
     * Helper method for findDiceCombinations (supplies a more understandable interface, esp. if one wished to raise the access level)
     * @param scoreChoice chosen score
     * @param gameDice the rolled dice of the game
     * @return
     */
    private ArrayList<ArrayList<Die>> getDiceCombinations(ThirtyThrowsGame.ScoreChoice scoreChoice, ArrayList<Die> gameDice) {
        int startingDiceAmount = 1;
        if(scoreChoice.getValue() > 6)
            startingDiceAmount = 2;
        ArrayList<ArrayList<Die>> countedDiceCombos = findDiceCombinations(scoreChoice.getValue(), 0, 0, startingDiceAmount, new Stack<>(), new ArrayList<>(gameDice), new ArrayList<>());
        return countedDiceCombos;
    }

    /**
     * Recursive method that automatically finds the combinations of the given dice for the supplied score choice (prioritizes searching for combinations with as few dice as possible)
     * @param SCORE_CHOICE_VALUE the chosen score
     * @param dieIndex used internally in calculation. should start on 0
     * @param startDieIndex used internally in calculation. should start on 0
     * @param diceToUseAmount nr of dice to count with. should start on 1 (or possibly 2 if scorehoice is above 6 and cannot be produced by only one die)
     * @param diceUsed used for calculation. start with an empty arraylist
     * @param dice the available dice. will be modified (send a copy)
     * @param countedDiceCombos stores dice combinations. start with a new arraylist
     * @return all the dice that is a part of valid combinations
     */
    private ArrayList<ArrayList<Die>> findDiceCombinations(final int SCORE_CHOICE_VALUE, int dieIndex, int startDieIndex, int diceToUseAmount, Stack<Die> diceUsed, ArrayList<Die> dice, ArrayList<ArrayList<Die>> countedDiceCombos) {
        Die currentDie;

        // add the amount of dice that are supposed to be used for the calculation
        while(diceToUseAmount > diceUsed.size() && dieIndex < dice.size()) {
            currentDie = dice.get(dieIndex ++);
            diceUsed.push(currentDie);
        }
        int points = countDiceValues(diceUsed);

        // combination found
        if(points == SCORE_CHOICE_VALUE) {
//            mCountedDice.addAll(diceUsed);
            countedDiceCombos.add(new ArrayList<>(diceUsed));
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
                        return countedDiceCombos;
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
        findDiceCombinations(SCORE_CHOICE_VALUE, dieIndex, startDieIndex, diceToUseAmount, diceUsed, dice, countedDiceCombos);
        return countedDiceCombos;
    }

    /**
     * Sums up the dice values in an iterable
     * @param dice the iterable you want to sum up
     * @return total value of the dice in the iterable
     */
    int countDiceValues(Iterable<Die> dice) {
        int value = 0;
        for(Die die : dice) {
            value += die.getValue();
        }
        return value;
    }

}
