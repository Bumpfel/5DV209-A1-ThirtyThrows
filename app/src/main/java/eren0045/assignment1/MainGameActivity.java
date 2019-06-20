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

    private enum STATE {CURRENT_ROLL, NOTIFICATION }

    private static final String TAG = "---MainGameActivity---";

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
//        else {
//            restoreGame(savedState);
//        }
    }

    private void setStartValues() {
        mGame = new DiceGame();

        ImageButton[] dieButtons = { mDieButton1, mDieButton2, mDieButton3, mDieButton4, mDieButton5, mDieButton6 };
        int i = 0;
        for(Die die : mGame.getDice()) {
            final ImageButton DIE_BUTTON = dieButtons[i ++];
            DIE_BUTTON.setOnClickListener(view -> toggleDie(DIE_BUTTON));
            mDice.put(DIE_BUTTON, die);
            DIE_BUTTON.setImageDrawable(getResources().getDrawable(R.drawable.white6));
        }

        mRollButton.setOnClickListener(view -> startRound());
        mRollButton.setText(R.string.roll);

        mNotificationText.setText(null);
        mScoreConfirmationButton.setVisibility(View.GONE);

        mRoundNrText.setText(null);
        mTotalScoreText.setText(null);

        initDropdown();
    }


    private void initialize() {
        // tie buttons to variables
        mDieButton1 = findViewById(R.id.die1);
        mDieButton2 = findViewById(R.id.die2);
        mDieButton3 = findViewById(R.id.die3);
        mDieButton4 = findViewById(R.id.die4);
        mDieButton5 = findViewById(R.id.die5);
        mDieButton6 = findViewById(R.id.die6);

        // Action for roll button
        mRollButton = findViewById(R.id.roll_button);
        mRollButton.setVisibility(View.VISIBLE);

        // Action for reset button
        mResetButton = findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(view -> finish());

        mScoreChoiceDropdown = findViewById(R.id.score_dropdown);
//        mScoreChoiceDropdown.setOnItemSelectedListener(
//                new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        if(mGame.isStarted() && mGame.isRoundOver()) {
//                            mChosenScore = ScoreChoice.valueOf("" + mScoreChoiceDropdown.getSelectedItem());
////                            ArrayList<ArrayList<Die>> countedDice = mGame.getCountedDice(mChosenScore);
//                            mScoreChoiceText.setText(getString(R.string.present_score_option, mChosenScore, mGame.getPoints(mChosenScore))); //, countedDice.toString()
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//                    }
//                }
//        );

        // Notification text
        mScoreChoiceText = findViewById(R.id.score_choice_text);
        mNotificationText = findViewById(R.id.notification_text);

        // Use score choice button
        mScoreConfirmationButton = findViewById(R.id.score_confirmation_button);
        mScoreConfirmationButton.setOnClickListener(view -> useScore());

        // game info texts
        mRoundNrText = findViewById(R.id.round_nr);
        mTotalScoreText = findViewById(R.id.total_score);
    }


    private void initDropdown() {
        mScoreChoices = mGame.getAvailableScoreChoices();
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mScoreChoices);
        mScoreChoiceDropdown.setAdapter(mSpinnerAdapter);
        mScoreChoiceDropdown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(mGame.isStarted() && mGame.isRoundOver()) {
                            mChosenScore = ScoreChoice.valueOf("" + mScoreChoiceDropdown.getSelectedItem());
//                            ArrayList<ArrayList<Die>> countedDice = mGame.getCountedDice(mChosenScore);
                            mScoreChoiceText.setText(getString(R.string.present_score_option, mChosenScore, mGame.getPoints(mChosenScore))); //, countedDice.toString()
                            Log.d(TAG, "selected " + mChosenScore);
                        }
                        else
                            Log.d(TAG, "Selected nothing");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }
        );
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        Log.d(TAG, "onSaveInstanceState() called");
//        state.putInt(STATE.CURRENT_ROLL.toString(), currentRoll);
        //state.putInt(STATE.NOTIFICATION.toString(), mNotificationText.getId());
        state.putString(STATE.NOTIFICATION.toString(), mNotificationText.getText().toString());

        state.putParcelable("mGame", mGame);
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

//        currentRoll = state.getInt(STATE.CURRENT_ROLL.toString());

        mGame = savedState.getParcelable("mGame");
//        Log.d("___Main.onRestore", "" + mGame.getTotalPoints());

        for(ImageButton dieButton : mDice.keySet()) {
            updateDieButtonImage(dieButton);

            if(mGame.isRoundOver())
                dieButton.setEnabled(false);
        }

        if(mGame.isRoundOver()) {
            mScoreConfirmationButton.setVisibility(View.VISIBLE);
            mRollButton.setVisibility(View.GONE);
            mRollButton.setOnClickListener(view -> startRound());
            //TODO måste kolla om poäng har satts. isåfall ska inte use score knappen vara synlig
        }
        else {
            mScoreConfirmationButton.setVisibility(View.GONE);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setOnClickListener(view -> rollDice());
        }

        this.updateRollButtonText();
//        mScoreChoices = mGame.getAvailableScoreChoices();
        initDropdown();
        mRoundNrText.setText(getString(R.string.round_nr, mGame.getCurrentRound()));
        mTotalScoreText.setText(getString(R.string.total_score, mGame.getTotalPoints()));
        mNotificationText.setText(savedState.getString(STATE.NOTIFICATION.toString()));
    }



    private void startRound() {
        mGame.newRound();
        mNotificationText.setText(null);
//        mScoreChoiceDropdown.setVisibility(View.VISIBLE);
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
            mGame.getHighestPoints(); //TODO (low prio) could add a checkbox option to use automatic calculation or not

            mRollButton.setVisibility(View.GONE);
//            mScoreChoiceDropdown.setVisibility(View.VISIBLE);

//TODO (viktig) senaste tanke - ev. orsak till bugg där fel poängval tas bort - getPoints måste köras INNAN getBestScoreChoice. Ibland görs inget val. det är då det buggar
            ScoreChoice bestScoreChoice = mGame.getBestScoreChoice();
            int bestScoreChoiceIndex = mScoreChoices.indexOf(bestScoreChoice);
//            Log.e(TAG, "index for selected score choice (" + bestScoreChoice + ") is " + bestScoreChoiceIndex);
            int highestScore = mGame.getHighestPoints();
            mScoreChoiceDropdown.setSelection(bestScoreChoiceIndex, false); //TODO buggy piece of SHIT
            Log.d(TAG, "setting dropdown selection to " + bestScoreChoice + ", " + bestScoreChoiceIndex);
            mNotificationText.setText("The highest score (" + bestScoreChoice + ") was pre-selected"); //It yields a score of " + highestScore + " using " + mGame.getCountedDiceForBestScoreChoice()

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
        mScoreConfirmationButton.setVisibility(View.GONE);
        mGame.setScore();

        mTotalScoreText.setText(getString(R.string.total_score, mGame.getTotalPoints()));

        // TODO temp

//        Intent scoreScreen = new Intent(this, ScoreActivity.class);
//        scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
//        scoreScreen.putExtra(ScoreActivity.Extras.TOTAL_SCORE.toString(), mGame.getTotalPoints());
//        scoreScreen.putExtra(ScoreActivity.Extras.ROUND_SCORES.toString(), mGame.getRoundScores());
//        scoreScreen.putExtra(ScoreActivity.Extras.ROUND_SCORE_CHOICES.toString(), mGame.getRoundScoreChoices());
//        startActivity(scoreScreen);

        prepareNextRound();
    }


    private void prepareNextRound() {
        if(mGame.isOver()) {
            Intent scoreScreen = new Intent(this, ScoreActivity.class);
            scoreScreen.putExtra(ScoreActivity.Extras.GAME.toString(), mGame);
            startActivity(scoreScreen);
        }
        else {
            mScoreChoiceText.setText(R.string.available_score_choices);
            mNotificationText.setText(R.string.new_round);
            mRollButton.setVisibility(View.VISIBLE);
            mRollButton.setText(R.string.roll);
            mRollButton.setOnClickListener(view -> startRound());

            mScoreChoices = mGame.getAvailableScoreChoices();
            mSpinnerAdapter.clear();
            mSpinnerAdapter.addAll(mScoreChoices);
            mSpinnerAdapter.notifyDataSetChanged();
        }
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
