package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

    private ArrayList<ScoreChoice> mScoreChoices;
    private ArrayAdapter<ScoreChoice> mSpinnerAdapter;

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
    private ScoreChoice mChosenScore;

    private enum STATE { GAME, NOTIFICATION }

    private static final String TAG = "---MainGameActivity---";

//    private int[] activeDiceImages = { 0, R.drawable.d1, R.drawable.d2, R.drawable.d3, R.drawable.d4, R.drawable.d5, R.drawable.d6 };
    private int[] activeDiceImages = { 0, R.drawable.white1, R.drawable.white2, R.drawable.white3, R.drawable.white4, R.drawable.white5, R.drawable.white6 };
    private int[] inactiveDiceImages = { 0, R.drawable.grey1, R.drawable.grey2, R.drawable.grey3, R.drawable.grey4, R.drawable.grey5, R.drawable.grey6 };
    private int[] finishedDiceImages = { 0, R.drawable.red1, R.drawable.red2, R.drawable.red3, R.drawable.red4, R.drawable.red5, R.drawable.red6 };

    @Override
    protected void onCreate(Bundle savedState) {
        Log.d(TAG, "onCreate() called");

        super.onCreate(savedState);
        setContentView(R.layout.activity_main_game);

        initialize();
        if(savedState == null)
            setStartValues();
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
        for(ImageButton dieButton : dieButtons) {
            dieButton.setOnClickListener(view -> toggleDie(dieButton));
            dieButton.setBackgroundColor(Color.TRANSPARENT);
        }

        // Action for roll button
        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setVisibility(View.VISIBLE);

        // Action for reset button
        mResetButton = findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(view -> finish());

        mScoreChoiceDropdown = findViewById(R.id.score_dropdown);

        // Notification text
        mScoreChoiceText = findViewById(R.id.score_choice_text);
        mNotificationText = findViewById(R.id.notification_text);

        // Use score choice button
        mScoreConfirmationButton = findViewById(R.id.score_confirmation_button);
        mScoreConfirmationButton.setOnClickListener(view -> useScore());

        // game info texts
        mRoundNrText = findViewById(R.id.round_nr);
        mTotalScoreText = findViewById(R.id.total_score);
        mTotalScoreText.setOnClickListener(view -> {
            Intent scoreScreen = new Intent(this, ScoreActivity.class);
            scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
            startActivity(scoreScreen);
        });
    }


    private void initDropdown() {
        mScoreChoices = mGame.getAvailableScoreChoices();
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mScoreChoices);
        mScoreChoiceDropdown.setAdapter(mSpinnerAdapter);
        mScoreChoiceDropdown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(mGame.hasStarted() && mGame.isRoundOver()) {
                            mChosenScore = ScoreChoice.valueOf("" + mScoreChoiceDropdown.getSelectedItem());
                            mScoreChoiceText.setText(getString(R.string.present_score_choice, mChosenScore, mGame.getPoints(mChosenScore))); //, countedDice.toString()
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }
        );
        mScoreChoiceDropdown.setVisibility(View.GONE);
    }


    private void setStartValues() {
        mGame = new DiceGame();

        ImageButton[] dieButtons = { mDieButton1, mDieButton2, mDieButton3, mDieButton4, mDieButton5, mDieButton6 };

        ArrayList<Die> gameDice = mGame.getDice();
        for(int i = 0; i < gameDice.size(); i ++) {
            final ImageButton DIE_BUTTON = dieButtons[i];
            DIE_BUTTON.setEnabled(false);
//            DIE_BUTTON.setOnClickListener(view -> toggleDie(DIE_BUTTON));
            mDice.put(DIE_BUTTON, gameDice.get(i));

//            DIE_BUTTON.setImageDrawable(getResources().getDrawable(R.drawable.white6));
//            DIE_BUTTON.setBackgroundColor(Color.TRANSPARENT);
        }

        mRollButton.setOnClickListener(view -> startRound());

        mScoreChoiceText.setVisibility(View.GONE);
        mNotificationText.setText(null);
        mScoreConfirmationButton.setVisibility(View.GONE);

        mRoundNrText.setVisibility(View.INVISIBLE);
        mTotalScoreText.setVisibility(View.INVISIBLE);
        mTotalScoreText.setText(getString(R.string.total_score, 0));

        initDropdown();
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        Log.d(TAG, "onSaveInstanceState() called");
        state.putString(STATE.NOTIFICATION.toString(), mNotificationText.getText().toString());

        state.putParcelable(STATE.GAME.toString(), mGame);
        for(ImageButton dieButton : mDice.keySet()) {
            state.putParcelable("" + dieButton.getId(), mDice.get(dieButton));
        }

        super.onSaveInstanceState(state);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState() called");
        super.onRestoreInstanceState(savedState);

        mDice.put(mDieButton1, savedState.getParcelable("" + mDieButton1.getId()));
        mDice.put(mDieButton2, savedState.getParcelable("" + mDieButton2.getId()));
        mDice.put(mDieButton3, savedState.getParcelable("" + mDieButton3.getId()));
        mDice.put(mDieButton4, savedState.getParcelable("" + mDieButton4.getId()));
        mDice.put(mDieButton5, savedState.getParcelable("" + mDieButton5.getId()));
        mDice.put(mDieButton6, savedState.getParcelable("" + mDieButton6.getId()));

        mGame = savedState.getParcelable(STATE.GAME.toString());

        updateAllDieButtons();
        initDropdown();

        if(!mGame.hasStarted()) {
            setStartValues();
            Log.e(TAG, "Game not started. setting start values");
        }
        else if(mGame.isRoundScored()) {
            mScoreConfirmationButton.setVisibility(View.GONE);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setOnClickListener(view -> startRound());
        }
        else if(mGame.isRoundOver()) {
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mRollButton.setVisibility(View.GONE);
            mRollButton.setOnClickListener(view -> startRound());

            mScoreChoiceDropdown.setVisibility(View.VISIBLE);
            mChosenScore = mGame.getBestScoreChoice();
            int chosenScoreIndex = mScoreChoices.indexOf(mChosenScore);
            mScoreChoiceDropdown.setSelection(chosenScoreIndex);
        }
        else { // Game running
            mScoreConfirmationButton.setVisibility(View.GONE);
            mScoreChoiceText.setVisibility(View.GONE);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setOnClickListener(view -> rollDice());
        }


        updateRollButtonText();
//        mScoreChoices = mGame.getAvailableScoreChoices();
        mRoundNrText.setText(getString(R.string.round_nr, mGame.getCurrentRound()));
        mTotalScoreText.setText(getString(R.string.total_score, mGame.getTotalPoints()));
        mNotificationText.setText(savedState.getString(STATE.NOTIFICATION.toString()));
    }


    private void startRound() {
        mGame.newRound();

//        mScoreChoiceText.setVisibility(View.VISIBLE);
//        mScoreChoiceDropdown.setVisibility(View.VISIBLE);
//        mScoreChoiceText.setText(R.string.available_score_choices);

        for(ImageButton dieButton: mDice.keySet()) {
            updateDieButtonImage(dieButton);
            dieButton.setEnabled(true);
        }
        updateRollButtonText();

        mNotificationText.setText(null);
        mRoundNrText.setVisibility(View.VISIBLE);
        mRoundNrText.setText(getString(R.string.round_nr, mGame.getCurrentRound()));
        mRollButton.setEnabled(true);
        mRollButton.setOnClickListener(view -> rollDice());
        mTotalScoreText.setVisibility(View.VISIBLE);
    }


    private void rollDice() {
        mGame.rollDice();
        updateAllDieButtons();

        if(mGame.isRoundOver())
           setRoundEndedStates();
        else
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
        mScoreConfirmationButton.setVisibility(View.GONE);
        mGame.setScore(mChosenScore);

        mScoreChoices.remove(mChosenScore);
        mTotalScoreText.setText(getString(R.string.total_score, mGame.getTotalPoints()));
        mScoreChoiceDropdown.setVisibility(View.GONE);
        mScoreChoiceText.setVisibility(View.GONE);

        prepareNextRound();
    }


    private void prepareNextRound() { //TODO förbättra metod-struktur - mindre spellogiksimplementation i controllern
        if(mGame.isOver()) {
            Intent scoreScreen = new Intent(this, ScoreActivity.class);
            scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
            startActivity(scoreScreen);
            finish();
        }
        else {
//            mScoreChoiceText.setText(R.string.available_score_choices);
            mNotificationText.setText(R.string.new_round);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setText(R.string.roll);
            mRollButton.setOnClickListener(view -> startRound());

//            mScoreChoices = mGame.getAvailableScoreChoices();
//            mSpinnerAdapter.clear();
//            mSpinner
//            mSpinnerAdapter.addAll(mScoreChoices);
            mSpinnerAdapter.notifyDataSetChanged();
        }
    }




    private void setRoundEndedStates() {
        mRollButton.setVisibility(View.GONE);
        mScoreChoiceDropdown.setVisibility(View.VISIBLE);
        mScoreChoiceText.setVisibility(View.VISIBLE);

        //TODO (low prio) could add a checkbox option to use automatic calculation or not

//TODO (viktig) Ibland funkar inte setSelection(?), det buggar när mChosenScore sätts till nåt som inte finns längre
//      ScoreChoice bestScoreChoice = mGame.getBestScoreChoice();
        int highestScore = mGame.getHighestPoints();
        mChosenScore = mGame.getBestScoreChoice();
        int bestScoreChoiceIndex = mScoreChoices.indexOf(mChosenScore);
        Log.d(TAG, "Auto-selected: " + bestScoreChoiceIndex + ": " + mChosenScore);
        Log.d(TAG, ""+ mScoreChoices.toString());
//            Log.e(TAG, "index for selected score choice (" + bestScoreChoice + ") is " + bestScoreChoiceIndex);
        mScoreChoiceDropdown.setVisibility(View.VISIBLE);
        mScoreChoiceDropdown.setSelection(bestScoreChoiceIndex, false);
//            Log.d(TAG, "setting dropdown selection to " + bestScoreChoice + ", " + bestScoreChoiceIndex);
        mScoreChoiceText.setText(getString(R.string.present_best_score_choice, mChosenScore, highestScore));
//        mNotificationText.setText("The highest score (" + mChosenScore + ") was pre-selected"); //It yields a score of " + highestScore + " using " + mGame.getCountedDiceForBestScoreChoice()
        //TODO visa tärningskombinationer som användes för poängräkning (i UI't)

        mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mNotificationText.setText(getString(R.string.round_over_text));

    }

    private void setRoundStartedStates() {

    }




    private void updateDieButtonImage(ImageButton dieButton) {
        Die thisDie = mDice.get(dieButton);

        int imgId;
        int dieValue = thisDie.getValue();

        if(mGame.isRoundOver()) {
            dieButton.setEnabled(false);
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

    private void updateAllDieButtons() {
        if(!mGame.hasStarted())
            return;
        for(ImageButton dieButton : mDice.keySet()) {
            updateDieButtonImage(dieButton);
        }
    }



    private void updateRollButtonText() {
        String rollText;
        if(mGame.isRoundOver())
            rollText = getString(R.string.roll);
        else
            rollText = getString(R.string.roll_button, mGame.getRollsLeft());
        mRollButton.setText(rollText);
    }


//    //TODO for debugging. delete when done
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart() called");
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume() called");
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause() called");
//    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop() called");
//    }
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d(TAG, "onRestart() called");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy() called");
//    }

}
