package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;

public class ScoreActivity extends AppCompatActivity {

    enum Extras { GAME, TOTAL_SCORE, ROUND_SCORES, ROUND_SCORE_CHOICES }

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

        int totalScore = intent.getIntExtra(Extras.TOTAL_SCORE.toString(), 0);
        int[] roundScores = intent.getIntArrayExtra(Extras.ROUND_SCORES.toString());
//        ScoreChoice[] roundScoreChoices = intent.getStringArrayExtra(Extras.ROUND_SCORE_CHOICES.toString());

        mPointsText = findViewById(R.id.points_textview);
//        mPointsText.setText("You got a total score of " + mGame.getTotalPoints());
        mPointsText.setText("You got a total score of " + totalScore);

        TextView roundScoresText = findViewById(R.id.round_scores);
        roundScoresText.setText(roundScores.toString());

        TextView roundScoreChoicesText = findViewById(R.id.round_scores);
//        roundScoreChoicesText.setText(roundScoreChoices.toString()); //TODO broken

        Log.d(TAG, Arrays.toString(mGame.getRoundScores()));
        Log.d(TAG, Arrays.toString(mGame.getRoundScoreChoices()));
    }




//    static Intent makeIntent(Context context, DiceGame game) {
//        Intent scoreScreen = new Intent(context, ScoreActivity.class);
//        scoreScreen.putExtra(Extras.GAME.toString(), game);
//        return scoreScreen;
//    }
}
