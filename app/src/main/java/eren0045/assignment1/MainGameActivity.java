package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainGameActivity extends AppCompatActivity {

    private Button mDieButton1;
    private Button mDieButton2;
    private Button mDieButton3;
    private Button mDieButton4;
    private Button mDieButton5;
    private Button mDieButton6;

    private TextView mNotificationMsg;
    private Button mRollButton;

    private int nrOfRolls = 0;
    private final int MAX_ROLLS = 3;

    private enum STATE { ROLLS, NOTIFICATION }

    private static final String TAG = "MainGameActivity";

    private Map<Button, Die> dice = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        // tie buttons to variables
        mDieButton1 = findViewById(R.id.die1);
        mDieButton2 = findViewById(R.id.die2);
        mDieButton3 = findViewById(R.id.die3);
        mDieButton4 = findViewById(R.id.die4);
        mDieButton5 = findViewById(R.id.die5);
        mDieButton6 = findViewById(R.id.die6);
        mNotificationMsg = findViewById(R.id.notification_msg);
        mRollButton = findViewById(R.id.roll_button);

        Button mRestartButton = findViewById(R.id.restart_button);
        mRestartButton.setOnClickListener(view -> {
            nrOfRolls = 0;
            initializeGame();
        });

        mRollButton.setOnClickListener(view -> {
            rollDice();
        });

        if(savedInstanceState == null)
            initializeGame();
        else
            restoreGame(savedInstanceState);
    }

    private void initializeGame() {
        mNotificationMsg.setText("");

        //associate buttons with a java die object
        dice.put(mDieButton1, new Die());
        dice.put(mDieButton2, new Die());
        dice.put(mDieButton3, new Die());
        dice.put(mDieButton4, new Die());
        dice.put(mDieButton5, new Die());
        dice.put(mDieButton6, new Die());


        // set text and onClick action for each die
        for(Button dieButton : dice.keySet()) {

            dieButton.setAlpha(1);
            dieButton.setEnabled(true);

            dieButton.setText("" + dice.get(dieButton).getValue());
            dieButton.setOnClickListener(view -> toggleDie(dieButton));
        }

        mRollButton.setEnabled(true);
        String rollText = String.format(getString(R.string.roll_button), MAX_ROLLS - nrOfRolls);
        mRollButton.setText(rollText);
    }

    private void restoreGame(Bundle state) {
        dice.put(mDieButton1, state.getParcelable("" + mDieButton1.getId()));
        dice.put(mDieButton2, state.getParcelable("" + mDieButton2.getId()));
        dice.put(mDieButton3, state.getParcelable("" + mDieButton3.getId()));
        dice.put(mDieButton4, state.getParcelable("" + mDieButton4.getId()));
        dice.put(mDieButton5, state.getParcelable("" + mDieButton5.getId()));
        dice.put(mDieButton6, state.getParcelable("" + mDieButton6.getId()));

        nrOfRolls = state.getInt(STATE.ROLLS.toString());

        for(Button dieButton : dice.keySet()) {

            if(MAX_ROLLS == nrOfRolls) {
                dieButton.setEnabled(false);
            }
            else if(dice.get(dieButton).isDisabled()) {
                dieButton.setAlpha((float) 0.2);
            }
            else {
                dieButton.setAlpha(1);
                dieButton.setEnabled(true);
            }
            dieButton.setText("" + dice.get(dieButton).getValue());
            dieButton.setOnClickListener(view -> toggleDie(dieButton));
        }

        if(MAX_ROLLS == nrOfRolls)
            mRollButton.setEnabled(false);
        else
            mRollButton.setEnabled(true);

        String rollText = String.format(getString(R.string.roll_button), MAX_ROLLS - nrOfRolls);
        mRollButton.setText(rollText);

        mNotificationMsg.setText(state.getString(STATE.NOTIFICATION.toString()));

    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putInt(STATE.ROLLS.toString(), nrOfRolls);
        //state.putInt(STATE.NOTIFICATION.toString(), mNotificationMsg.getId());
        state.putString(STATE.NOTIFICATION.toString(), mNotificationMsg.getText().toString());

        for(Button dieButton : dice.keySet()) {
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


    private void toggleDie(Button button) { // TODO possibly re-write
        Die thisDie = dice.get(button);
        dice.get(button).toggleDie();

        if(thisDie.isDisabled())
            button.setAlpha((float) 0.2);
        else
            button.setAlpha(1);
    }


    private void rollDice() {
        if(nrOfRolls < MAX_ROLLS) {
            for(Button dieButton : dice.keySet()) {
                Die thisDie = dice.get(dieButton);
                if(!thisDie.isDisabled()) {
                    thisDie.throwDie();
                    dieButton.setText("" + thisDie.getValue());
                }
            }
            nrOfRolls ++;
        }
        if(nrOfRolls == MAX_ROLLS) {
            for(Button dieButton : dice.keySet()) {
                dieButton.setAlpha(1);
                dieButton.setEnabled(false);
            }
            mRollButton.setEnabled(false);
            mNotificationMsg.setText(String.format(getString(R.string.game_over_text), "SOME_SCORE"));
        }

        String rollText = String.format(getString(R.string.roll_button), MAX_ROLLS - nrOfRolls);
        mRollButton.setText(rollText);
    }
}
