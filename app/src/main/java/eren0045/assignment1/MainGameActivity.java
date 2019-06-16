package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class MainGameActivity extends AppCompatActivity {

    private ImageButton mDieButton1;
    private ImageButton mDieButton2;
    private ImageButton mDieButton3;
    private ImageButton mDieButton4;
    private ImageButton mDieButton5;
    private ImageButton mDieButton6;

    private TextView mNotificationText;
    private Button mRollButton;
    private Button mRestartButton;
    private Spinner mScoreChoice;
    private Button mScoreConfirmationButton;
    private TextView mRoundNrText;
    private TextView mTotalScoreText;

    private Map<ImageButton, Die> dice = new HashMap<>();
    private int currentRoll = 0;
    private final int MAX_ROLLS = GameRules.MAX_ROLLS;
    private int currentRound = 0;
    private final int MAX_ROUNDS = GameRules.MAX_ROUNDS;
    private int[] roundScore = new int[MAX_ROUNDS];
    private int totalScore = 0;
    private int tempRoundScore = 0;

    private enum STATE {CURRENT_ROLL, NOTIFICATION }

    private static final String TAG = "---MainGameActivity---";

    private int[] activeDiceImages = { 0, R.drawable.white1, R.drawable.white2, R.drawable.white3, R.drawable.white4, R.drawable.white5, R.drawable.white6 };
    private int[] inactiveDiceImages = { 0, R.drawable.grey1, R.drawable.grey2, R.drawable.grey3, R.drawable.grey4, R.drawable.grey5, R.drawable.grey6 };
    private int[] finishedDiceImages = { 0, R.drawable.red1, R.drawable.red2, R.drawable.red3, R.drawable.red4, R.drawable.red5, R.drawable.red6 };

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
        mNotificationText = findViewById(R.id.notification_text);

        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setOnClickListener(view -> {
            startRound();
        });

        mRestartButton = findViewById(R.id.restart_button);
        mRestartButton.setOnClickListener(view -> {
            resetGame();
        });

        mScoreChoice = findViewById(R.id.score_dropdown);
        mScoreChoice.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       Score chosenScore = Score.valueOf("" + mScoreChoice.getSelectedItem());
                       tempRoundScore = GameRules.calcScore(chosenScore.getValue(), dice.values());

                       mNotificationText.setText(getString(R.string.present_score_option, chosenScore, tempRoundScore));
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
    }


    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.d(TAG, "onRestoreInstanceState() called");
        super.onRestoreInstanceState(state);

        dice.put(mDieButton1, state.getParcelable("" + mDieButton1.getId()));
        dice.put(mDieButton2, state.getParcelable("" + mDieButton2.getId()));
        dice.put(mDieButton3, state.getParcelable("" + mDieButton3.getId()));
        dice.put(mDieButton4, state.getParcelable("" + mDieButton4.getId()));
        dice.put(mDieButton5, state.getParcelable("" + mDieButton5.getId()));
        dice.put(mDieButton6, state.getParcelable("" + mDieButton6.getId()));

        currentRoll = state.getInt(STATE.CURRENT_ROLL.toString());

        for(ImageButton dieButton : dice.keySet()) {
            setDieButtonImage(dieButton);

            if(MAX_ROLLS == currentRoll)
                dieButton.setEnabled(false);

            dieButton.setOnClickListener(view -> toggleDie(dieButton));
        }

        if(MAX_ROLLS == currentRoll)
            mRollButton.setEnabled(false);
        else
            mRollButton.setEnabled(true);

        setRollButtonText();

        mNotificationText.setText(state.getString(STATE.NOTIFICATION.toString()));
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
//        Log.d(TAG, "onSaveInstanceState() called");
        state.putInt(STATE.CURRENT_ROLL.toString(), currentRoll);
        //state.putInt(STATE.NOTIFICATION.toString(), mNotificationText.getId());
        state.putString(STATE.NOTIFICATION.toString(), mNotificationText.getText().toString());

        for(ImageButton dieButton : dice.keySet()) {
            state.putParcelable("" + dieButton.getId(), dice.get(dieButton));
        }

        super.onSaveInstanceState(state);
    }

    private void resetGame() {
        currentRound = 0;
        totalScore = 0;
        roundScore = new int[MAX_ROUNDS];

        startRound();
    }

    private void startRound() {
        currentRoll = 0;
        mNotificationText.setText("");

        dice.clear();
       //associate buttons with a java die object
        dice.put(mDieButton1, new Die());
        dice.put(mDieButton2, new Die());
        dice.put(mDieButton3, new Die());
        dice.put(mDieButton4, new Die());
        dice.put(mDieButton5, new Die());
        dice.put(mDieButton6, new Die());
        currentRoll ++;

        mRoundNrText.setText(getString(R.string.round_nr, currentRound));

        // set text and onClick action for each die
        for(ImageButton dieButton : dice.keySet()) {
            dieButton.setEnabled(true);
            setDieButtonImage(dieButton);

            //TODO unnecessary to set these every time. set enabled would be enough (and maybe to also clear the images)
            dieButton.setOnClickListener(view -> toggleDie(dieButton));
        }

        mRollButton.setEnabled(true);
        setRollButtonText();
        mRollButton.setOnClickListener(view -> rollDice());

    }


    private void toggleDie(ImageButton dieButton) {
        Die thisDie = dice.get(dieButton);

        thisDie.toggleDie();
        setDieButtonImage(dieButton);
    }


    private void setDieButtonImage(ImageButton dieButton) {
        Die thisDie = dice.get(dieButton);

        int imgId;
        int dieValue = thisDie.getValue();
        if(thisDie.isEnabled())
            imgId = activeDiceImages[dieValue];
        else
            imgId = inactiveDiceImages[dieValue];

        dieButton.setImageDrawable(getResources().getDrawable(imgId));
    }

    private void rollDice() {
        // Round not over
        if(currentRoll < MAX_ROLLS) {
            for(ImageButton dieButton : dice.keySet()) {
                Die thisDie = dice.get(dieButton);
                if(thisDie.isEnabled()) {
                    thisDie.throwDie();
                    int imgId = activeDiceImages[thisDie.getValue()];
                    dieButton.setImageDrawable(getResources().getDrawable(imgId));
                }
            }
            currentRoll++;
        }
        // Round ended
        if(currentRoll == MAX_ROLLS) {
            for(ImageButton dieButton : dice.keySet()) {
                int imgId = inactiveDiceImages[dice.get(dieButton).getValue()];
                dieButton.setImageDrawable(getResources().getDrawable(imgId));
                dieButton.setEnabled(false);
            }
            mRollButton.setEnabled(false);
            mScoreChoice.setVisibility(View.VISIBLE);
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mNotificationText.setText(getString(R.string.round_over_text));
        }

        setRollButtonText();
    }


    private void useScore() {
        mScoreChoice.setVisibility(View.INVISIBLE);
        mScoreConfirmationButton.setVisibility(View.GONE);
        totalScore += tempRoundScore;
        roundScore[currentRound] = tempRoundScore;
        currentRound++;

        mTotalScoreText.setText(getString(R.string.total_score, totalScore));

        if(currentRound == MAX_ROUNDS)
            startActivity(new Intent(this, ScoreActivity.class));
        else {
//            startRound();
            mNotificationText.setText(R.string.new_round);
            mRollButton.setEnabled(true);
            mRollButton.setOnClickListener(view -> startRound());
            currentRoll = 0;
            setRollButtonText();
//            mRollButton.setText("Start next currentRound");
        }

    }


    private void setRollButtonText() {
        String rollText = getString(R.string.roll_button, MAX_ROLLS - currentRoll);
        mRollButton.setText(rollText);
    }
}
