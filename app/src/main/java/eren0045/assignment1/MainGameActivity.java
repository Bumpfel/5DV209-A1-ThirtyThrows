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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainGameActivity extends AppCompatActivity {

    private ImageButton mDieButton1;
    private ImageButton mDieButton2;
    private ImageButton mDieButton3;
    private ImageButton mDieButton4;
    private ImageButton mDieButton5;
    private ImageButton mDieButton6;
    private ImageButton[] dieButtons;

    private TextView mNotificationText;
    private Button mRollButton;
    private Button mRestartButton;
    private Spinner mScoreChoice;
    private Button mScoreConfirmationButton;
    private TextView mRoundNrText;
    private TextView mTotalScoreText;

    private DiceGame game = new DiceGame();
    private Map<ImageButton, Die> dice = new HashMap<>();
    private int tempRoundScore = 0;
    private Score chosenScore;

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
        dieButtons = new ImageButton[] { mDieButton1, mDieButton2, mDieButton3, mDieButton4, mDieButton5, mDieButton6 };
        for(ImageButton dieButton : dieButtons) {
            dieButton.setOnClickListener(view -> toggleDie(dieButton));
        }

        mNotificationText = findViewById(R.id.notification_text);

        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setOnClickListener(view -> startRound());

        mRestartButton = findViewById(R.id.restart_button);
        mRestartButton.setOnClickListener(view -> game.resetGame());

        mScoreChoice = findViewById(R.id.score_dropdown);
        mScoreChoice.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       chosenScore = Score.valueOf("" + mScoreChoice.getSelectedItem());
                       tempRoundScore = game.calcScore(chosenScore.getValue());

                       mNotificationText.setText(getString(R.string.present_score_option, chosenScore, tempRoundScore));
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> adapterView) {

                   }
               }
        );
        mScoreConfirmationButton = findViewById(R.id.score_confirmation_button);
        mScoreConfirmationButton.setOnClickListener(view -> useScore());

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

//        currentRoll = state.getInt(STATE.CURRENT_ROLL.toString());

        game = state.getParcelable("game");

        for(ImageButton dieButton : dice.keySet()) {
            updateDieButtonImage(dieButton);

            if(game.getRollsLeft() == 0)
                dieButton.setEnabled(false);
        }

        if(game.getRollsLeft() == 0)
            mRollButton.setEnabled(false);
        else
            mRollButton.setEnabled(true);

        this.updateRollButtonText();

        mNotificationText.setText(state.getString(STATE.NOTIFICATION.toString()));
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
//        Log.d(TAG, "onSaveInstanceState() called");
//        state.putInt(STATE.CURRENT_ROLL.toString(), currentRoll);
        //state.putInt(STATE.NOTIFICATION.toString(), mNotificationText.getId());
        state.putString(STATE.NOTIFICATION.toString(), mNotificationText.getText().toString());

        state.putParcelable("game", game);
        for(ImageButton dieButton : dice.keySet()) {
            state.putParcelable("" + dieButton.getId(), dice.get(dieButton));
        }

        super.onSaveInstanceState(state);
    }

//    private void resetGame() {
//        currentRound = 0;
//        totalScore = 0;
//        roundScore = new int[MAX_ROUNDS];
//
//        newRound();
//    }

    private void startRound() {
        game.newRound();
        mNotificationText.setText(null);

        dice.clear();
        ArrayList<Die> dieArray = game.getDice();
//       //associate buttons with a java die object
        for(int i = 0; i < 6; i ++) {
            dice.put(dieButtons[i], dieArray.get(i));
        }
//        dice.put(mDieButton1, dieArray[0]);
//        dice.put(mDieButton2, dieArray[1]);
//        dice.put(mDieButton3, dieArray[2]);
//        dice.put(mDieButton4, dieArray[3]);
//        dice.put(mDieButton5, dieArray[4]);
//        dice.put(mDieButton6, dieArray[5]);

        mRoundNrText.setText(getString(R.string.round_nr, game.getCurrentRound()));

        // enable die buttons and update their images
        for(ImageButton dieButton : dice.keySet()) {
            dieButton.setEnabled(true);
            updateDieButtonImage(dieButton);
        }

        mRollButton.setEnabled(true);
        this.updateRollButtonText();
        mRollButton.setOnClickListener(view -> rollDice());
    }


    private void toggleDie(ImageButton dieButton) {
        Die thisDie = dice.get(dieButton);

        if(thisDie != null) {
            thisDie.toggleDie();
            updateDieButtonImage(dieButton);
        }
    }


    private void updateDieButtonImage(ImageButton dieButton) {
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
        game.rollDice();

        if(!game.isRoundOver()) {
            for(ImageButton dieButton : dice.keySet()) {
                Die thisDie = dice.get(dieButton);
                if(thisDie.isEnabled()) {
                    int imgId = activeDiceImages[thisDie.getValue()];
                    dieButton.setImageDrawable(getResources().getDrawable(imgId));
                }
            }
        }
        // Round ended
        if(game.isRoundOver()) {
            for(ImageButton dieButton : dice.keySet()) {
                int imgId = finishedDiceImages[dice.get(dieButton).getValue()];
                dieButton.setImageDrawable(getResources().getDrawable(imgId));
                dieButton.setEnabled(false);
            }
//            mRollButton.setEnabled(false);
            mRollButton.setVisibility(View.GONE);
            mScoreChoice.setVisibility(View.VISIBLE);
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mNotificationText.setText(getString(R.string.round_over_text));
        }

        updateRollButtonText();
    }


    private void useScore() {
        mScoreChoice.setVisibility(View.INVISIBLE);
        mScoreConfirmationButton.setVisibility(View.GONE);
        game.setScore(chosenScore);
//        totalScore += tempRoundScore;
//        roundScore[currentRound] = tempRoundScore;
//        currentRound++;

        mTotalScoreText.setText(getString(R.string.total_score, game.getTotalScore()));

        if(game.isOver())
            startActivity(new Intent(this, ScoreActivity.class));
        else {
            mNotificationText.setText(R.string.new_round);
//            mRollButton.setEnabled(true);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setText("Roll!");
            mRollButton.setOnClickListener(view -> startRound());
        }

    }


    private void updateRollButtonText() {
        String rollText = getString(R.string.roll_button, game.getRollsLeft());
        mRollButton.setText(rollText);
    }
}
