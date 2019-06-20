package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    enum Extras { GAME }

    private DiceGame mGame;
    private TextView mPointsText;
//    private TextView mRoundScoresText;

    private final String TAG = "---ScoreActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        mGame = intent.getParcelableExtra(Extras.GAME.toString());

        // total score
        mPointsText = findViewById(R.id.total_points_textview);
        mPointsText.setText("You got a total score of " + mGame.getTotalPoints());

        // Score data
        TextView roundsText = findViewById(R.id.rounds_textview);
        TextView roundPointsText = findViewById(R.id.round_points_textview);
        TextView roundScoreChoicesText = findViewById(R.id.round_score_choices_textview);

        int[] roundPoints = mGame.getRoundPoints();
        String[] roundScoreChoices = mGame.getRoundScoreChoices();

        StringBuilder rounds = new StringBuilder();
        StringBuilder formattedPoints = new StringBuilder();
        StringBuilder formattedScoreChoices = new StringBuilder();
        for (int i = 0; i < roundPoints.length; i ++) {
            rounds.append("Round " + (i + 1) + "\n");
            formattedPoints.append(roundPoints[i] + "\n");
            formattedScoreChoices.append(roundScoreChoices[i] + "\n");
        }

        roundsText.setText(rounds.toString());
        roundPointsText.setText(formattedPoints.toString());
        roundScoreChoicesText.setText(formattedScoreChoices.toString());

        //
        Button startNewGameButton = findViewById(R.id.start_new_game_button);
        startNewGameButton.setOnClickListener(view -> {
            startActivity(new Intent(ScoreActivity.this, MainGameActivity.class));
            finish();
        });

    }


}
