package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import eren0045.assignment1.model.ThirtyThrowsGame;

public class ScoreActivity extends AppCompatActivity {

    enum Extras { GAME }

    private final String TAG = "---ScoreActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        //get game from intent
        Intent intent = getIntent();
        ThirtyThrowsGame game = intent.getParcelableExtra(Extras.GAME.toString());

        // total score
        TextView pointsText = findViewById(R.id.total_points_textview);
        pointsText.setText(getString(R.string.result_total_points, game.getTotalPoints()));

        // Score data
        TextView roundsText = findViewById(R.id.rounds_textview);
        TextView roundPointsText = findViewById(R.id.round_points_textview);
        TextView roundScoreChoicesText = findViewById(R.id.round_score_choices_textview);

        int[] roundPoints = game.getRoundPoints();
        String[] roundScoreChoices = game.getRoundScoreChoices();

        StringBuilder rounds = new StringBuilder();
        StringBuilder formattedPoints = new StringBuilder();
        StringBuilder formattedScoreChoices = new StringBuilder();

        // Formatting
        int i = 0;
        while(i < roundScoreChoices.length && roundScoreChoices[i] != null) {
            rounds.append(getString(R.string.round) + (i + 1) + "\n");
            formattedPoints.append(roundPoints[i] + "\n");
            formattedScoreChoices.append(roundScoreChoices[i] + "\n");
            i ++;
        }

        roundsText.setText(rounds.toString());
        roundPointsText.setText(formattedPoints.toString());
        roundScoreChoicesText.setText(formattedScoreChoices.toString());

        Button startNewGameButton = findViewById(R.id.start_new_game_button);
        startNewGameButton.setOnClickListener(view -> {
            startActivity(new Intent(ScoreActivity.this, MainGameActivity.class));
            finish();
        });

        Button startScreenButton = findViewById(R.id.start_screen_button);
        startScreenButton.setOnClickListener(view -> finish() );

    }


}
