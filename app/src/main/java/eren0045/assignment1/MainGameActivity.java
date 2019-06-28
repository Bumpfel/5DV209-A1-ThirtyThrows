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

import eren0045.assignment1.model.Die;
import eren0045.assignment1.model.ThirtyThrowsGame;

public class MainGameActivity extends AppCompatActivity {

    private ArrayList<ThirtyThrowsGame.ScoreChoice> mAvailableScoreChoices;
//    private ArrayAdapter<ThirtyThrowsGame.ScoreChoice> mSpinnerAdapter;

    private ImageButton mDieButton1;
    private ImageButton mDieButton2;
    private ImageButton mDieButton3;
    private ImageButton mDieButton4;
    private ImageButton mDieButton5;
    private ImageButton mDieButton6;

    private TextView mNotificationText;
    private TextView mScoreChoiceText;
    private Button mRollButton;
    private Button mResetButton;
    private Spinner mScoreChoiceDropdown;
    private Button mScoreConfirmationButton;
    private TextView mRoundNrText;
    private TextView mTotalScoreText;

    private ThirtyThrowsGame mGame = new ThirtyThrowsGame();
    private Map<ImageButton, Die> mDice = new HashMap<>();

    private enum STATE { GAME, NOTIFICATION, SELECTED_SCORE }

    private final String TAG = "---MainGameActivity---";
    private final float INACTIVE_VIEW_ALPHA = 0.15f;

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

    // Sets values that aren't changed (view-references and listeners)
    // Runs for both new and restored activity
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

        // Roll button
        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setOnClickListener(view -> play());

        // Action for reset button
        mResetButton = findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(view -> finish());

        mScoreChoiceDropdown = findViewById(R.id.score_dropdown);

        // Notification text
        mScoreChoiceText = findViewById(R.id.score_choice_text);
        mNotificationText = findViewById(R.id.notification_text);

        // Use score choice button
        mScoreConfirmationButton = findViewById(R.id.score_confirmation_button);
        mScoreConfirmationButton.setOnClickListener(view -> {
            useScore();
        });

        // game info texts
        mRoundNrText = findViewById(R.id.round_nr);
        mTotalScoreText = findViewById(R.id.total_score);
        mTotalScoreText.setOnClickListener(view -> {
            Intent scoreScreen = new Intent(this, ScorePopupActivity.class);
            scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
            startActivity(scoreScreen);
        });
    }


    private void setStartValues() {
        mGame = new ThirtyThrowsGame();

        ImageButton[] dieButtons = { mDieButton1, mDieButton2, mDieButton3, mDieButton4, mDieButton5, mDieButton6 };

        ArrayList<Die> gameDice = mGame.getDice();
        for(int i = 0; i < gameDice.size(); i ++) {
            final ImageButton DIE_BUTTON = dieButtons[i];
            DIE_BUTTON.setEnabled(false);
            mDice.put(DIE_BUTTON, gameDice.get(i));
        }

        mRollButton.setVisibility(View.VISIBLE);

        mScoreChoiceText.setVisibility(View.GONE);
        mNotificationText.setText(null);
        mScoreConfirmationButton.setVisibility(View.INVISIBLE);

        mRoundNrText.setVisibility(View.INVISIBLE);
        mTotalScoreText.setVisibility(View.INVISIBLE);
        mTotalScoreText.setText(getString(R.string.total_points, 0));

        initDropdown();
    }


    private void initDropdown() {
        mAvailableScoreChoices = mGame.getAvailableScoreChoices();
        ArrayAdapter<ThirtyThrowsGame.ScoreChoice> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mAvailableScoreChoices);
        mScoreChoiceDropdown.setAdapter(spinnerAdapter);
        mScoreChoiceDropdown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ThirtyThrowsGame.ScoreChoice selectedScore = ThirtyThrowsGame.ScoreChoice.valueOf("" + mScoreChoiceDropdown.getSelectedItem());
                        mScoreChoiceText.setText(getString(R.string.present_score_choice, selectedScore, mGame.getPoints(selectedScore))); //, countedDice.toString()
                        Log.e(TAG, "Selected " + selectedScore);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }
        );
        mScoreChoiceDropdown.setAlpha(INACTIVE_VIEW_ALPHA);
        mScoreChoiceDropdown.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        Log.d(TAG, "onSaveInstanceState() called");
        state.putString(STATE.NOTIFICATION.toString(), mNotificationText.getText().toString());
        state.putString(STATE.SELECTED_SCORE.toString(), mScoreChoiceDropdown.getSelectedItem().toString());

        state.putParcelable(STATE.GAME.toString(), mGame);
        for(ImageButton dieButton : mDice.keySet()) {
            state.putParcelable("" + dieButton.getId(), mDice.get(dieButton));
        }

        super.onSaveInstanceState(state);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedState) { // TODO undersök varför getPoints() körs två ggr
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
        }
        else if(mGame.isRoundScored()) {
            mScoreConfirmationButton.setVisibility(View.INVISIBLE);
            mRollButton.setVisibility(View.VISIBLE);
//            mScoreChoiceDropdown.setVisibility(View.INVISIBLE);
        }
        else if(mGame.isRoundOver()) {
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mRollButton.setVisibility(View.GONE);

            // restore dropdown score selection
            mScoreChoiceDropdown.setAlpha(1);
            mScoreChoiceDropdown.setVisibility(View.VISIBLE);
            ThirtyThrowsGame.ScoreChoice selectedScore = ThirtyThrowsGame.ScoreChoice.valueOf(savedState.getString(STATE.SELECTED_SCORE.toString()));
            int chosenScoreIndex = mAvailableScoreChoices.indexOf(selectedScore);
            mScoreChoiceDropdown.setSelection(chosenScoreIndex);
        }
        else { // Game running
            mScoreConfirmationButton.setVisibility(View.INVISIBLE);
            mScoreChoiceText.setVisibility(View.GONE);
            mRollButton.setVisibility(View.VISIBLE);
            mScoreChoiceDropdown.setVisibility(View.VISIBLE);
        }

        updateRollButtonText();
        mRoundNrText.setText(getString(R.string.round_nr, mGame.getCurrentRound()));
        mTotalScoreText.setText(getString(R.string.total_points, mGame.getTotalPoints()));
        mNotificationText.setText(savedState.getString(STATE.NOTIFICATION.toString()));
    }


    private void play() {
        if(mGame.isRoundOver()) {
            mGame.newRound();

            for(ImageButton dieButton: mDice.keySet()) {
                updateDieButtonImage(dieButton);
                dieButton.setEnabled(true);
            }
            updateRollButtonText();

            mNotificationText.setText(null);
            mRoundNrText.setVisibility(View.VISIBLE);
            mRoundNrText.setText(getString(R.string.round_nr, mGame.getCurrentRound()));
            mRollButton.setEnabled(true);
            mTotalScoreText.setVisibility(View.VISIBLE);
            mScoreChoiceDropdown.setVisibility(View.VISIBLE);
        }
        else {
            mGame.rollDice();
            updateAllDieButtons();

            if(mGame.isRoundOver()) {
                mRollButton.setVisibility(View.GONE);
                mScoreChoiceText.setVisibility(View.VISIBLE);

                //Select best score option
                ThirtyThrowsGame.ScoreChoice bestScoreChoice = mGame.getBestScoreChoice();
                int bestScoreChoiceIndex = mAvailableScoreChoices.indexOf(bestScoreChoice);
                int selectedIndex = mScoreChoiceDropdown.getSelectedItemPosition();
                // This is needed because some item will always be selected in the dropdown, and onItemSelect will not run if attempting to select the same option as is selected
                if (selectedIndex == bestScoreChoiceIndex) {
                    int points = mGame.getPoints(bestScoreChoice);
                    mScoreChoiceText.setText(getString(R.string.present_score_choice, bestScoreChoice, points));
                }
                mScoreChoiceDropdown.setAlpha(1);
                mScoreChoiceDropdown.setSelection(bestScoreChoiceIndex, false);

                mScoreConfirmationButton.setVisibility(View.VISIBLE);
                mNotificationText.setText(getString(R.string.round_over));
            }
            else
                updateRollButtonText();
        }
    }


    private void toggleDie(ImageButton dieButton) {
        Die thisDie = mDice.get(dieButton);

        if(thisDie != null) {
            mGame.toggleDie(thisDie);
            updateDieButtonImage(dieButton);
        }
    }


    private void useScore() {
        mScoreConfirmationButton.setVisibility(View.INVISIBLE);

        ThirtyThrowsGame.ScoreChoice selectedScoreChoice = (ThirtyThrowsGame.ScoreChoice) mScoreChoiceDropdown.getSelectedItem();
        mGame.setScore(selectedScoreChoice);

//        mAvailableScoreChoices.remove(selectedScoreChoice); //TODO (done) prob prettier to get score choices from mGame
//        mSpinnerAdapter.notifyDataSetChanged();

        mAvailableScoreChoices = mGame.getAvailableScoreChoices();
        //TODO not the prettiest
        ArrayAdapter<ThirtyThrowsGame.ScoreChoice> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mAvailableScoreChoices);
        mScoreChoiceDropdown.setAdapter(spinnerAdapter);

        mTotalScoreText.setText(getString(R.string.total_points, mGame.getTotalPoints()));
        mScoreChoiceDropdown.setAlpha(INACTIVE_VIEW_ALPHA);
        mScoreChoiceDropdown.setVisibility(View.INVISIBLE);
        mScoreChoiceText.setVisibility(View.GONE);

        // preparations for next round.
        if(mGame.isOver()) {
            Intent scoreScreen = new Intent(this, ScoreActivity.class);
            scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
            startActivity(scoreScreen);
            finish();
        }
        else {
            mScoreChoiceText.setText(null);
            mNotificationText.setText(R.string.new_round);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setText(R.string.roll);
        }
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
