package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

enum SCORE { LOW, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE }

public class MainGameActivity extends AppCompatActivity {

    private ImageButton mDieButton1;
    private ImageButton mDieButton2;
    private ImageButton mDieButton3;
    private ImageButton mDieButton4;
    private ImageButton mDieButton5;
    private ImageButton mDieButton6;

    private TextView mNotificationMsg;
    private Button mRollButton;
    private Button mRestartButton;
    private Spinner mScoreChoice;
    private Button mScoreConfirmationButton;
    private TextView mRoundNrText;
    private TextView mTotalScoreText;

    private Map<ImageButton, Die> dice = new HashMap<>();
    private int nrOfRolls = 0; // TODO rename to currentRoll
    private final int MAX_ROLLS = 3; // TODO move to game rules
    private int round = 0; // TODO rename to currentRound
    private final int MAX_ROUNDS = 10; // TODO move to game rules
    private int[] roundScore = new int[10];
    private int totalScore = 0;
    private int tempRoundScore = 0;

    private enum STATE { ROLLS, NOTIFICATION }

    private static final String TAG = "---MainGameActivity---";

    private int[] availableDiceImages = { 0, R.drawable.white1, R.drawable.white2, R.drawable.white3, R.drawable.white4, R.drawable.white5, R.drawable.white6 };
    private int[] unavailableDiceImages = { 0, R.drawable.grey1, R.drawable.grey2, R.drawable.grey3, R.drawable.grey4, R.drawable.grey5, R.drawable.grey6 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        // tie buttons to variables
        mDieButton1 = findViewById(R.id.die1);
        mDieButton2 = findViewById(R.id.die2);
        mDieButton3 = findViewById(R.id.die3);
        mDieButton4 = findViewById(R.id.die4);
        mDieButton5 = findViewById(R.id.die5);
        mDieButton6 = findViewById(R.id.die6);
        mNotificationMsg = findViewById(R.id.notification_text);

        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setOnClickListener(view -> {
            rollDice();
        });

        mRestartButton = findViewById(R.id.restart_button);
        mRestartButton.setOnClickListener(view -> {
            initializeGame();
        });

        mScoreChoice = findViewById(R.id.score_dropdown);
        mScoreChoice.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                      tempRoundScore = calcScore(SCORE.valueOf("" + mScoreChoice.getSelectedItem()));
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> adapterView) {

                   }
               }
        );
        mScoreConfirmationButton = findViewById(R.id.score_confirmation_button);
        mScoreConfirmationButton.setOnClickListener(view -> {
            useScore();
        });

        mRoundNrText = findViewById(R.id.round_nr);
        mTotalScoreText = findViewById(R.id.total_score);

        if(savedInstanceState == null)
            initializeGame();
//        else
//            restoreGame(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
//        Log.d(TAG, "onRestoreInstanceState() called");
        super.onRestoreInstanceState(state);
        restoreGame(state);
    }

    private void initializeGame() {
       //associate buttons with a java die object
        dice.put(mDieButton1, new Die());
        dice.put(mDieButton2, new Die());
        dice.put(mDieButton3, new Die());
        dice.put(mDieButton4, new Die());
        dice.put(mDieButton5, new Die());
        dice.put(mDieButton6, new Die());

        startRound();
    }

    private void startRound() {
        nrOfRolls = 0;

        mNotificationMsg.setText("");

        mRoundNrText.setText(getString(R.string.round_nr, round));
        mTotalScoreText.setText(getString(R.string.total_score, totalScore));

        // set text and onClick action for each die
        for(ImageButton dieButton : dice.keySet()) {
            dieButton.setEnabled(true);

            //TODO unnecessary to set these every time. set enabled would be enough (and maybe to also clear the images)
//            int imgId = availableDiceImages[dice.get(dieButton).getValue()];
//            dieButton.setImageDrawable(getResources().getDrawable(imgId));
            dieButton.setOnClickListener(view -> toggleDie(dieButton));
        }

        mRollButton.setEnabled(true);
        mRollButton.setText(getString(R.string.roll_button, MAX_ROLLS - nrOfRolls));
    }

    private void restoreGame(Bundle state) { // TODO probably a lot of code duplication in this and initializeGame()
        dice.put(mDieButton1, state.getParcelable("" + mDieButton1.getId()));
        dice.put(mDieButton2, state.getParcelable("" + mDieButton2.getId()));
        dice.put(mDieButton3, state.getParcelable("" + mDieButton3.getId()));
        dice.put(mDieButton4, state.getParcelable("" + mDieButton4.getId()));
        dice.put(mDieButton5, state.getParcelable("" + mDieButton5.getId()));
        dice.put(mDieButton6, state.getParcelable("" + mDieButton6.getId()));

        nrOfRolls = state.getInt(STATE.ROLLS.toString());

        for(ImageButton dieButton : dice.keySet()) {

            int imgId;
            if(dice.get(dieButton).isDisabled())
                imgId = unavailableDiceImages[dice.get(dieButton).getValue()];
            else
                imgId = availableDiceImages[dice.get(dieButton).getValue()];

            dieButton.setImageDrawable(getResources().getDrawable(imgId));

            if(MAX_ROLLS == nrOfRolls)
               dieButton.setEnabled(false);

            dieButton.setOnClickListener(view -> toggleDie(dieButton));
        }

        if(MAX_ROLLS == nrOfRolls)
            mRollButton.setEnabled(false);
        else
            mRollButton.setEnabled(true);

        mRollButton.setText(getString(R.string.roll_button, MAX_ROLLS - nrOfRolls));

        mNotificationMsg.setText(state.getString(STATE.NOTIFICATION.toString()));
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
//        Log.d(TAG, "onSaveInstanceState() called");
        state.putInt(STATE.ROLLS.toString(), nrOfRolls);
        //state.putInt(STATE.NOTIFICATION.toString(), mNotificationMsg.getId());
        state.putString(STATE.NOTIFICATION.toString(), mNotificationMsg.getText().toString());

        for(ImageButton dieButton : dice.keySet()) {
            state.putParcelable("" + dieButton.getId(), dice.get(dieButton));
        }

        super.onSaveInstanceState(state);
    }

    /*@Override
    public void onRestoreInstanceState(Bundle savedInstanceState) { // gör lite dubbelt i och med denna metod. onCreate körs och sätter värden, sen körs denna och skriver över
        super.onRestoreInstanceState(savedInstanceState);
        nrOfRolls = savedInstanceState.getInt(STATE.ROLLS.toString());
        mRollButton.setText("Roll (" + (MAX_ROLLS - nrOfRolls) + ")");

        if(MAX_ROLLS - nrOfRolls == 0) {
            mRollButton.setEnabled(false);
        }

        mNotificationMsg.setText(savedInstanceState.getString(STATE.NOTIFICATION.toString()));
    }*/


    private void toggleDie(ImageButton dieButton) { // TODO possibly re-write toa make it more re-usable
        Die thisDie = dice.get(dieButton);
        dice.get(dieButton).toggleDie();

        int imgId;
        int dieValue = dice.get(dieButton).getValue();
        if(thisDie.isDisabled())
            imgId = unavailableDiceImages[dieValue];
        else
            imgId = availableDiceImages[dieValue];

        dieButton.setImageDrawable(getResources().getDrawable(imgId));
    }


    private void rollDice() {
        if(nrOfRolls < MAX_ROLLS) {
            for(ImageButton dieButton : dice.keySet()) {
                Die thisDie = dice.get(dieButton);
                if(!thisDie.isDisabled()) {
                    thisDie.throwDie();
                    int imgId = availableDiceImages[dice.get(dieButton).getValue()];
                    dieButton.setImageDrawable(getResources().getDrawable(imgId));
                }
                else {
                    Log.d(TAG, dieButton.getId() + " disabled");
                }
            }
            nrOfRolls ++;
        }
        if(nrOfRolls == MAX_ROLLS) {
            for(ImageButton dieButton : dice.keySet()) {
                int imgId = unavailableDiceImages[dice.get(dieButton).getValue()];
                dieButton.setImageDrawable(getResources().getDrawable(imgId));
                dieButton.setEnabled(false);
            }
            mRollButton.setEnabled(false);
            mScoreChoice.setVisibility(View.VISIBLE);
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mNotificationMsg.setText(getString(R.string.round_over_text));
        }

        String rollText = getString(R.string.roll_button, MAX_ROLLS - nrOfRolls);
        mRollButton.setText(rollText);
    }

    private int calcScore(SCORE chosenScore) { //TODO move to GameRules
        int wrongScore = 0;
        for(Die die : dice.values()) {
            wrongScore += die.getValue();
        }
        mNotificationMsg.setText(chosenScore + " gives you a round score of " + wrongScore);

        return wrongScore;
    }

    private void useScore() {
        mScoreChoice.setVisibility(View.INVISIBLE);
        mScoreConfirmationButton.setVisibility(View.GONE);
        totalScore += tempRoundScore;
        roundScore[round] = tempRoundScore;
        round ++;
        startRound();
    }

}
