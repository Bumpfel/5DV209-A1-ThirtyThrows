package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainGameActivity.class);
            startActivity(intent);
        });

    }

    /*public void startGame(View view) {
        Intent intent = new Intent(this, MainGameActivity.class);
        startActivity(intent);
    }*/
}