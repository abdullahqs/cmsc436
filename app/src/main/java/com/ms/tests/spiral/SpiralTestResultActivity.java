package com.ms.tests.spiral;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.tests.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SpiralTestResultActivity extends AppCompatActivity {
    private Button mButton;
    private ImageView mResultView;
    private Uri mImageUri;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test_results);

        mButton = (Button) findViewById(R.id.spiral_results_share_button);
        mResultView = (ImageView) findViewById(R.id.spiral_results_result_preview);
        mTextView = (TextView) findViewById(R.id.score);


        Intent i = getIntent();

        Double score = i.getDoubleExtra("SpiralMetric",0);
        int scoreInt = score.intValue();
        final String scoreString = Integer.toString(scoreInt);
        mTextView.setText(scoreString);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c.getTime());

        SpiralTestResults result = new SpiralTestResults(scoreInt,date);


        String resultImage = i.getStringExtra(SpiralTestActivity.RESULT_IMAGE_URI);
        if(resultImage != null) {
            mImageUri = Uri.parse(resultImage);
            mResultView.setImageURI(mImageUri);

        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageUri != null){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, mImageUri);
                    share.putExtra(Intent.EXTRA_TEXT,"I scored "+scoreString+" out of 100.");
                    share.setType("image/jpeg");
                    startActivity(Intent.createChooser(share, "Share Results"));
                }
            }
        });

    }
}
