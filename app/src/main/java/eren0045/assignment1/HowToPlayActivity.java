package eren0045.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class HowToPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        Button goBackButton = findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(view -> finish() );
    }
}
