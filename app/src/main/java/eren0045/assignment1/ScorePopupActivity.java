package eren0045.assignment1;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class ScorePopupActivity extends ScoreActivity {


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Button newGameButton = findViewById(R.id.start_new_game_button);
        newGameButton.setVisibility(View.GONE);

        Button startScreenButton = findViewById(R.id.start_screen_button);
        startScreenButton.setVisibility(View.GONE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .75), (int) (height * .7));

    }

}
