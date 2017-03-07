package com.ms.tests.level;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ms.tests.R;
import com.ms.tests.spiral.SpiralTestActivity;
import com.ms.tests.spiral.SpiralTestResults;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LevelTestResultsActivity extends AppCompatActivity {
    private Button mButton;
    private ImageView mResultView;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test_results);

        mButton = (Button) findViewById(R.id.level_results_share_button);
        mResultView = (ImageView) findViewById(R.id.level_results_result_preview);

        Intent i = getIntent();

        String resultImage = i.getStringExtra(LevelTestActivity.RESULT_IMAGE_URI);
        if(resultImage != null) {
            mImageUri = Uri.parse(resultImage);
            mResultView.setImageURI(mImageUri);
        }

        int calculation = 0;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c.getTime());

        LevelTestResults result = new LevelTestResults(calculation,date);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageUri != null){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, mImageUri);
                    share.setType("image/jpeg");
                    startActivity(Intent.createChooser(share, "Share Results"));
                }
            }
        });
    }
}
