package com.ms.tests.spiral;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ms.tests.R;

public class SpiralTestResultActivity extends AppCompatActivity {
    private Button mButton;
    private ImageView mResultView;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test_results);

        mButton = (Button) findViewById(R.id.share_button);
        mResultView = (ImageView) findViewById(R.id.result_preview);

        Intent i = getIntent();

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
                    startActivity(Intent.createChooser(share, "Share Results"));
                }
            }
        });
    }
}
