package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainGameActivity extends AppCompatActivity {

    private ArrayList<Score> mScoreChoices;
    private ArrayAdapter<Score> mSpinnerAdapter;

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
    private Spinner mScoreChoiceDropdown;
    private Button mScoreConfirmationButton;
    private TextView mRoundNrText;
    private TextView mTotalScoreText;

    private DiceGame mGame = new DiceGame();
    private Map<ImageButton, Die> mDice = new HashMap<>();
    private int mTempRoundScore = 0;
    private Score mChosenScore;

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
        mRestartButton.setOnClickListener(view -> resetGame());

        mScoreChoiceDropdown = findViewById(R.id.score_dropdown);
        mScoreChoices = new ArrayList<Score>(Arrays.asList(Score.values()));
        mSpinnerAdapter = new ArrayAdapter<Score>(this, android.R.layout.simple_spinner_dropdown_item, mScoreChoices);
        mScoreChoiceDropdown.setAdapter(mSpinnerAdapter);
        //TODO score ska beräknas direkt när rundan är slut med den "valda", eller aktiva, poängräkningen
        mScoreChoiceDropdown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { //TODO måste ta bort alternativ när det valts en gång. Även ur automatiska poängräkningen i modellen.
                        mChosenScore = Score.valueOf("" + mScoreChoiceDropdown.getSelectedItem());
                        mTempRoundScore = mGame.calcScore(mChosenScore);

                        mNotificationText.setText(getString(R.string.present_score_option, mChosenScore, mTempRoundScore));
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

        mDice.put(mDieButton1, state.getParcelable("" + mDieButton1.getId()));
        mDice.put(mDieButton2, state.getParcelable("" + mDieButton2.getId()));
        mDice.put(mDieButton3, state.getParcelable("" + mDieButton3.getId()));
        mDice.put(mDieButton4, state.getParcelable("" + mDieButton4.getId()));
        mDice.put(mDieButton5, state.getParcelable("" + mDieButton5.getId()));
        mDice.put(mDieButton6, state.getParcelable("" + mDieButton6.getId()));

//        currentRoll = state.getInt(STATE.CURRENT_ROLL.toString());

        mGame = state.getParcelable("mGame");

        for(ImageButton dieButton : mDice.keySet()) {
            updateDieButtonImage(dieButton);

            if(mGame.getRollsLeft() == 0)
                dieButton.setEnabled(false);
        }

        if(mGame.getRollsLeft() == 0)
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

        state.putParcelable("mGame", mGame);
        for(ImageButton dieButton : mDice.keySet()) {
            state.putParcelable("" + dieButton.getId(), mDice.get(dieButton));
        }

        super.onSaveInstanceState(state);
    }

    private void resetGame() {
        mGame.resetGame();
        mTotalScoreText.setText(null);
        mScoreChoices = new ArrayList<Score>(Arrays.asList(Score.values())); //TODO code dupliation
        mSpinnerAdapter.notifyDataSetChanged();
        //        mSpinnerAdapter = new ArrayAdapter<Score>(this, android.R.layout.simple_spinner_dropdown_item, mScoreChoices);
        startRound();
    }

    private void startRound() {
        mGame.newRound();
        mNotificationText.setText(null);

        mDice.clear();
        ArrayList<Die> dieArray = mGame.getDice();
//       //associate buttons with a java die object
        for(int i = 0; i < 6; i ++) {
            mDice.put(dieButtons[i], dieArray.get(i));
        }
//        mDice.put(mDieButton1, dieArray[0]);
//        mDice.put(mDieButton2, dieArray[1]);
//        mDice.put(mDieButton3, dieArray[2]);
//        mDice.put(mDieButton4, dieArray[3]);
//        mDice.put(mDieButton5, dieArray[4]);
//        mDice.put(mDieButton6, dieArray[5]);

        mRoundNrText.setText(getString(R.string.round_nr, mGame.getCurrentRound()));

        // enable die buttons and update their images
        for(ImageButton dieButton : mDice.keySet()) {
            dieButton.setEnabled(true);
            updateDieButtonImage(dieButton);
        }

        mRollButton.setEnabled(true);
        this.updateRollButtonText();
        mRollButton.setOnClickListener(view -> rollDice());
    }


    private void toggleDie(ImageButton dieButton) {
        Die thisDie = mDice.get(dieButton);

        if(thisDie != null) {
            thisDie.toggleDie();
            updateDieButtonImage(dieButton);
        }
    }


    private void rollDice() {
        // Round not over
        mGame.rollDice();

//        if(!mGame.isRoundOver()) {
//            for(ImageButton dieButton : mDice.keySet()) {
//                Die thisDie = mDice.get(dieButton);
//                if(thisDie.isEnabled()) {
//                    int imgId = activeDiceImages[thisDie.getValue()];
//                    dieButton.setImageDrawable(getResources().getDrawable(imgId));
//                }
//            }
//        }
//        // Round ended
//        if(mGame.isRoundOver()) {
//            for(ImageButton dieButton : mDice.keySet()) {
//                int imgId = finishedDiceImages[mDice.get(dieButton).getValue()];
//                dieButton.setImageDrawable(getResources().getDrawable(imgId));
//                dieButton.setEnabled(false);
//            }
        for(ImageButton dieButton : mDice.keySet()) {
            updateDieButtonImage(dieButton);
        }

        if(mGame.isRoundOver()) {
            mRollButton.setVisibility(View.GONE);
            mScoreChoiceDropdown.setVisibility(View.VISIBLE);
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mNotificationText.setText(getString(R.string.round_over_text));
        }

        updateRollButtonText();
    }


    private void useScore() {
        mScoreChoiceDropdown.setVisibility(View.INVISIBLE);
        mScoreConfirmationButton.setVisibility(View.GONE);
        mGame.setScore();
//        totalScore += mTempRoundScore;
//        roundScore[currentRound] = mTempRoundScore;
//        currentRound++;

        mTotalScoreText.setText(getString(R.string.total_score, mGame.getTotalScore()));

        if(mGame.isOver())
            startActivity(new Intent(this, ScoreActivity.class));
        else {
            mNotificationText.setText(R.string.new_round);
//            mRollButton.setEnabled(true);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setText("Roll!");
            mRollButton.setOnClickListener(view -> startRound());
        }

        int pos = mScoreChoiceDropdown.getSelectedItemPosition();
        mScoreChoices.remove(pos);
        mSpinnerAdapter.notifyDataSetChanged();
    }


    private void updateDieButtonImage(ImageButton dieButton) {
        Die thisDie = mDice.get(dieButton);

        int imgId;
        int dieValue = thisDie.getValue();
        if(mGame.isRoundOver()) {
            imgId = finishedDiceImages[dieValue];
        }
        else {
            if(thisDie.isEnabled())
                imgId = activeDiceImages[dieValue];
            else
                imgId = inactiveDiceImages[dieValue];
        }

        dieButton.setImageDrawable(getResources().getDrawable(imgId));
    }


    private void updateRollButtonText() {
        String rollText = getString(R.string.roll_button, mGame.getRollsLeft());
        mRollButton.setText(rollText);
    }
}
