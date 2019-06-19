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
//    private ImageButton[] dieButtons;

    private TextView mNotificationText;
    private TextView mScoreChoiceText;
    private Button mRollButton;
    private Button mResetButton;
    private Spinner mScoreChoiceDropdown;
    private Button mScoreConfirmationButton;
    private TextView mRoundNrText;
    private TextView mTotalScoreText;

    private DiceGame mGame = new DiceGame();
    private Map<ImageButton, Die> mDice = new HashMap<>();
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

        initialize();
    }

    private void initialize() {
        // tie buttons to variables
        mDieButton1 = findViewById(R.id.die1);
        mDieButton2 = findViewById(R.id.die2);
        mDieButton3 = findViewById(R.id.die3);
        mDieButton4 = findViewById(R.id.die4);
        mDieButton5 = findViewById(R.id.die5);
        mDieButton6 = findViewById(R.id.die6);
        ImageButton[] dieButtons = { mDieButton1, mDieButton2, mDieButton3, mDieButton4, mDieButton5, mDieButton6 };
        int i = 0;
        for(Die die : mGame.getDice()) {
            final ImageButton DIE_BUTTON = dieButtons[i ++];
            DIE_BUTTON.setOnClickListener(view -> toggleDie(DIE_BUTTON));
            mDice.put(DIE_BUTTON, die);
            DIE_BUTTON.setImageDrawable(null); // TODO använd startbild med tärningar och en text "Thirty Throws"
            //TODO gör startaktivitet istället för att hålla på att konstant sätta synlighet för text views
        }

        // Action for roll button
        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setOnClickListener(view -> startRound());
        mRollButton.setVisibility(View.VISIBLE);
        mRollButton.setText(R.string.start_game);

        // Action for reset button
        mResetButton = findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(view -> resetGame());
        mResetButton.setVisibility(View.INVISIBLE);

        // Populate and set action for score choice dropdown
        mScoreChoiceDropdown = findViewById(R.id.score_dropdown);
        mScoreChoices = mGame.getAvailableScoreChoices();
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mScoreChoices);
        mScoreChoiceDropdown.setAdapter(mSpinnerAdapter);
        mScoreChoiceDropdown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(mGame.isStarted() && mGame.isRoundOver()) {
                            mChosenScore = Score.valueOf("" + mScoreChoiceDropdown.getSelectedItem());
//                            ArrayList<ArrayList<Die>> countedDice = mGame.getCountedDice(mChosenScore);
                            mScoreChoiceText.setText(getString(R.string.present_score_option, mChosenScore, mGame.getScore(mChosenScore))); //, countedDice.toString()
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }
        );
        mScoreChoiceDropdown.setVisibility(View.INVISIBLE);

        // Notification text
        mScoreChoiceText = findViewById(R.id.score_choice_text);
        mScoreChoiceText.setVisibility(View.INVISIBLE);
        mNotificationText = findViewById(R.id.notification_text);
        mNotificationText.setText(null);

        // Use score choice button
        mScoreConfirmationButton = findViewById(R.id.score_confirmation_button);
        mScoreConfirmationButton.setOnClickListener(view -> useScore());
        mScoreConfirmationButton.setVisibility(View.GONE);

        // game info texts
        mRoundNrText = findViewById(R.id.round_nr);
        mRoundNrText.setText(null);
        mTotalScoreText = findViewById(R.id.total_score);
        mTotalScoreText.setText(null);
    }


    @Override
    protected void onRestoreInstanceState(Bundle state) { // TODO out of date
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

            if(mGame.isRoundOver())
                dieButton.setEnabled(false);
        }

        if(mGame.isRoundOver())
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


    private void resetGame() { //TODO funkar inte som det ska. återställer inte listan över val m.m.
        mGame = new DiceGame();

        initialize();
//        startRound();
    }


    private void startRound() {
        mGame.newRound();
        mNotificationText.setText(null);
        mScoreChoiceDropdown.setVisibility(View.VISIBLE);
        mScoreChoiceText.setVisibility(View.VISIBLE);
        mScoreChoiceText.setText(R.string.available_score_choices);

        for(ImageButton dieButton: mDice.keySet()) {
            dieButton.setEnabled(true);
            updateDieButtonImage(dieButton);
        }

        mRoundNrText.setText(getString(R.string.round_nr, mGame.getCurrentRound() + 1));

        mRollButton.setEnabled(true);
        this.updateRollButtonText();
        mRollButton.setOnClickListener(view -> rollDice());

        mResetButton.setVisibility(View.VISIBLE);
    }


    private void rollDice() {
        mGame.rollDice();

        for(ImageButton dieButton : mDice.keySet()) {
            updateDieButtonImage(dieButton);
        }

        if(mGame.isRoundOver()) {
            mGame.getHighestScore(); //TODO (low prio) could add a checkbox option to use automatic calculation or not

            mRollButton.setVisibility(View.INVISIBLE);
//            mScoreChoiceDropdown.setVisibility(View.VISIBLE);

            Score bestScoreChoice = mGame.getBestScoreChoice();
            int bestScoreChoiceIndex = mScoreChoices.indexOf(bestScoreChoice);
            int highestScore = mGame.getHighestScore();
            mScoreChoiceDropdown.setSelection(bestScoreChoiceIndex);
            mNotificationText.setText(bestScoreChoice + " will give you the highest score of " + highestScore + " using " + mGame.getCountedDiceForBestScore());

            mScoreConfirmationButton.setVisibility(View.VISIBLE);
//            mNotificationText.setText(getString(R.string.round_over_text));
        }

        updateRollButtonText();
    }


    private void toggleDie(ImageButton dieButton) {
        Die thisDie = mDice.get(dieButton);

        if(thisDie != null) {
            thisDie.toggleDie();
            updateDieButtonImage(dieButton);
        }
    }


    private void useScore() {
//        mScoreChoiceDropdown.setVisibility(View.INVISIBLE);
        mScoreConfirmationButton.setVisibility(View.INVISIBLE);
        mGame.setScore();

        mTotalScoreText.setText(getString(R.string.total_score, mGame.getTotalScore()));

        if(mGame.isOver())
            startActivity(new Intent(this, ScoreActivity.class));
        else {
            mScoreChoiceText.setText(R.string.available_score_choices);
            mNotificationText.setText(R.string.new_round);
//            mRollButton.setEnabled(true);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setText("Roll!");
            mRollButton.setOnClickListener(view -> startRound());
        }

        mScoreChoices = mGame.getAvailableScoreChoices();
        mSpinnerAdapter.clear();
        mSpinnerAdapter.addAll(mScoreChoices);
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
