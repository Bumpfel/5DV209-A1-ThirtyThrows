package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startGameButton = findViewById(R.id.start_game_button);
        Intent gameIntent = new Intent(this, MainGameActivity.class);
        startGameButton.setOnClickListener(view -> startActivity(gameIntent));

        Button howToPlayButton = findViewById(R.id.how_to_play_button);
        Intent howToPlayScreen = new Intent(this, HowToPlayActivity.class);
        howToPlayButton.setOnClickListener(view -> startActivity(howToPlayScreen));

    }

}
