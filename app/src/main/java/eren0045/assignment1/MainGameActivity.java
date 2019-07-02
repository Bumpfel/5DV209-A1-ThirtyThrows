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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eren0045.assignment1.model.Die;
import eren0045.assignment1.model.ThirtyThrowsGame;

public class MainGameActivity extends AppCompatActivity {

    private final String TAG = "--MainGameActivity";

    private ImageButton[] mDieButtons;

    private LinearLayout mCombinationsLayout;
    private TextView mNotificationText;
    private TextView mScoreChoiceText;
    private Button mRollButton;
    private Spinner mScoreChoiceDropdown;
    private Button mScoreConfirmationButton;
    private TextView mRoundNrText;
    private TextView mTotalScoreText;

    private ThirtyThrowsGame mGame = new ThirtyThrowsGame();
    private Map<ImageButton, Die> mDice = new HashMap<>();

    private enum STATE { GAME, NOTIFICATION, SELECTED_SCORE }

    private final float INACTIVE_VIEW_ALPHA = 0.2f;

    private int[] activeDiceImages = { 0, R.drawable.white1, R.drawable.white2, R.drawable.white3, R.drawable.white4, R.drawable.white5, R.drawable.white6 };
    private int[] inactiveDiceImages = { 0, R.drawable.grey1, R.drawable.grey2, R.drawable.grey3, R.drawable.grey4, R.drawable.grey5, R.drawable.grey6 };
    private int[] finishedDiceImages = { 0, R.drawable.red1, R.drawable.red2, R.drawable.red3, R.drawable.red4, R.drawable.red5, R.drawable.red6 };

    @Override
    protected void onCreate(Bundle savedState) {

        super.onCreate(savedState);
        setContentView(R.layout.activity_main_game);

        initialize();
        if(savedState == null)
            setStartValues();
    }


    // Sets values that aren't changed (view-references and listeners)
    // Runs for both new and restored activity
    private void initialize() {
        mDieButtons = new ImageButton[] { findViewById(R.id.die1), findViewById(R.id.die2), findViewById(R.id.die3), findViewById(R.id.die4), findViewById(R.id.die5), findViewById(R.id.die6) };
        for(ImageButton dieButton : mDieButtons) {
            dieButton.setOnClickListener(view -> toggleDie(dieButton));
            dieButton.setBackgroundColor(Color.TRANSPARENT);
        }

        // Layout that holds a visual representation of dice combinations for the selected score choice
        mCombinationsLayout = findViewById(R.id.dice_combinations_layout);
        mCombinationsLayout.setVisibility(View.GONE);

        // Roll button
        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setOnClickListener(view -> play());

//        // Action for reset button
//        Button resetButton = findViewById(R.id.reset_button);
//        resetButton.setOnClickListener(view -> finish());

        mScoreChoiceDropdown = findViewById(R.id.score_dropdown);

        // Notification text
        mScoreChoiceText = findViewById(R.id.score_choice_text);
        mNotificationText = findViewById(R.id.notification_text);

        // Use score choice button
        mScoreConfirmationButton = findViewById(R.id.score_confirmation_button);
        mScoreConfirmationButton.setOnClickListener(view -> useScore() );

        // game info texts
        mRoundNrText = findViewById(R.id.round_nr);
        mTotalScoreText = findViewById(R.id.total_score);
        mTotalScoreText.setOnClickListener(view -> {
            Intent scoreScreen = new Intent(this, ScorePopupActivity.class);
            scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
            startActivity(scoreScreen);
        });
    }


    // sets initial values for the views (game startup)
    private void setStartValues() {
        mGame = new ThirtyThrowsGame();

        ArrayList<Die> gameDice = mGame.getDice();
        for(int i = 0; i < gameDice.size(); i ++) {
            mDieButtons[i].setEnabled(false);
            mDice.put(mDieButtons[i], gameDice.get(i));
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


    // initializes the score choice drop down
    private void initDropdown() {
        ArrayAdapter<ThirtyThrowsGame.ScoreChoice> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mGame.getAvailableScoreChoices());
        mScoreChoiceDropdown.setAdapter(spinnerAdapter);
        mScoreChoiceDropdown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ThirtyThrowsGame.ScoreChoice selectedScore = ThirtyThrowsGame.ScoreChoice.valueOf("" + mScoreChoiceDropdown.getSelectedItem());

                        ArrayList<ArrayList<Die>> diceCombos = new ArrayList<>();
                        int points = mGame.getPoints(selectedScore, diceCombos);
                        mScoreChoiceText.setText(getString(R.string.present_score_choice, selectedScore, points));
                        displayDiceCombos(diceCombos);
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
        state.putString(STATE.NOTIFICATION.toString(), mNotificationText.getText().toString());
        state.putString(STATE.SELECTED_SCORE.toString(), mScoreChoiceDropdown.getSelectedItem().toString());

        state.putParcelable(STATE.GAME.toString(), mGame);
        for(ImageButton dieButton : mDice.keySet()) {
            state.putParcelable("" + dieButton.getId(), mDice.get(dieButton));
        }

        super.onSaveInstanceState(state);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);

        mGame = savedState.getParcelable(STATE.GAME.toString());
        ArrayList<Die> dice = mGame.getDice();
        for(int i = 0; i < mDieButtons.length; i ++) {
            mDice.put(mDieButtons[i], dice.get(i));
        }

        updateAllDieButtons();
        initDropdown();

        if(!mGame.hasStarted()) {
            setStartValues();
        }
        else if(mGame.isRoundScored()) {
            mScoreConfirmationButton.setVisibility(View.INVISIBLE);
            mRollButton.setVisibility(View.VISIBLE);
            mScoreChoiceText.setVisibility(View.GONE);
        }
        else if(mGame.isRoundOver()) {
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mRollButton.setVisibility(View.GONE);
            mScoreChoiceDropdown.setAlpha(1);
            mScoreChoiceDropdown.setVisibility(View.VISIBLE);
            mCombinationsLayout.setVisibility(View.VISIBLE);
        }
        // Game running
        else {
            mScoreConfirmationButton.setVisibility(View.INVISIBLE);
            mScoreChoiceText.setVisibility(View.GONE);
            mRollButton.setVisibility(View.VISIBLE);
            mScoreChoiceDropdown.setVisibility(View.VISIBLE);
        }
        // restore dropdown score selection
        ThirtyThrowsGame.ScoreChoice selectedScore = ThirtyThrowsGame.ScoreChoice.valueOf(savedState.getString(STATE.SELECTED_SCORE.toString()));
        int chosenScoreIndex = mGame.getAvailableScoreChoices().indexOf(selectedScore);
        mScoreChoiceDropdown.setSelection(chosenScoreIndex);

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
        // Game running - rolls dice and selects the "best" score choice if round is over
        else {
            mGame.rollDice();
            updateAllDieButtons();

            if(mGame.isRoundOver()) {
                mRollButton.setVisibility(View.GONE);
                mScoreChoiceText.setVisibility(View.VISIBLE);
                mCombinationsLayout.setVisibility(View.VISIBLE);

                //Select best score option
                ThirtyThrowsGame.ScoreChoice bestScoreChoice = mGame.getBestScoreChoice();
                int bestScoreChoiceIndex = mGame.getAvailableScoreChoices().indexOf(bestScoreChoice);
                int selectedIndex = mScoreChoiceDropdown.getSelectedItemPosition();
                // This is needed because some item will always be selected in the dropdown, and onItemSelect will not run if attempting to select the same option as is selected
                if (selectedIndex == bestScoreChoiceIndex) {
                    ArrayList<ArrayList<Die>> diceCombos = new ArrayList<>();
                    int points = mGame.getPoints(bestScoreChoice, diceCombos);
                    mScoreChoiceText.setText(getString(R.string.present_score_choice, bestScoreChoice, points));
                    displayDiceCombos(diceCombos);
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


    // toggles whether to roll the die the next time or not
    private void toggleDie(ImageButton dieButton) {
        Die thisDie = mDice.get(dieButton);

        if(thisDie != null) {
            mGame.toggleDie(thisDie);
            updateDieButtonImage(dieButton);
        }
    }


    // round is over. uses the score choice selected in the drop down
    private void useScore() {
        mScoreConfirmationButton.setVisibility(View.INVISIBLE);

        ThirtyThrowsGame.ScoreChoice selectedScoreChoice = (ThirtyThrowsGame.ScoreChoice) mScoreChoiceDropdown.getSelectedItem();
        mGame.setScore(selectedScoreChoice);

        ArrayAdapter<ThirtyThrowsGame.ScoreChoice> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mGame.getAvailableScoreChoices());
        mScoreChoiceDropdown.setAdapter(spinnerAdapter);

        mTotalScoreText.setText(getString(R.string.total_points, mGame.getTotalPoints()));
        mScoreChoiceDropdown.setAlpha(INACTIVE_VIEW_ALPHA);
        mScoreChoiceDropdown.setVisibility(View.INVISIBLE);
        mScoreChoiceText.setVisibility(View.GONE);

        mCombinationsLayout.removeAllViews();
        mCombinationsLayout.setVisibility(View.GONE);

        // if game is over, present score screen, pop activity stack
        if(mGame.isOver()) {
            Intent scoreScreen = new Intent(this, ScoreActivity.class);
            scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
            startActivity(scoreScreen);
            finish();
        }
        // preparations for next round
        else {
            mScoreChoiceText.setText(null);
            mNotificationText.setText(R.string.new_round);
            mRollButton.setVisibility(View.VISIBLE);
            updateRollButtonText();
        }
    }


    // updates the die image button to reflect the current state of the die
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


    // updates all die image buttons
    private void updateAllDieButtons() {
        if(!mGame.hasStarted())
            return;
        for(ImageButton dieButton : mDice.keySet()) {
            updateDieButtonImage(dieButton);
        }
    }


    // updates the text of the roll button to reflect the current state of the round (shows nr of rolls left if round is started)
    private void updateRollButtonText() {
        String rollText;
        if(mGame.isRoundOver())
            rollText = getString(R.string.roll);
        else
            rollText = getString(R.string.roll_button, mGame.getRollsLeft());
        mRollButton.setText(rollText);
    }


    // displays dice combinations graphically
    private void displayDiceCombos(ArrayList<ArrayList<Die>> diceCombos) {
        if(mGame.isRoundOver() && mGame.hasStarted() && !mGame.isRoundScored()) {
            Log.e(TAG, "displayCombos()");
            mCombinationsLayout.removeAllViews();

            for(ArrayList<Die> dice : diceCombos) {
                ImageView img = null;
                for(Die die : dice) {
                    img = new ImageView(this);
                    img.setImageDrawable(getResources().getDrawable(finishedDiceImages[die.getValue()]));
                    mCombinationsLayout.addView(img);
                    img.setPadding(-40,0,-40,0);
                }
                img.setPadding(-40,0,-10,0);

            }
        }
    }

}
