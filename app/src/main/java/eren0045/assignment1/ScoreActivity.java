package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

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

        mPointsText = findViewById(R.id.points_textview);
        mPointsText.setText("You got a total score of " + mGame.getTotalPoints());

        TextView roundPointsText = findViewById(R.id.round_points);
        roundPointsText.setText(Arrays.toString(mGame.getRoundScores()));

        TextView roundScoreChoicesText = findViewById(R.id.round_points);
        roundScoreChoicesText.setText(Arrays.toString(mGame.getRoundScoreChoices()));
    }




//    static Intent makeIntent(Context context, DiceGame game) {
//        Intent scoreScreen = new Intent(context, ScoreActivity.class);
//        scoreScreen.putExtra(Extras.GAME.toString(), game);
//        return scoreScreen;
//    }
}
