package com.ms.tests.spiral;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ms.tests.R;

public class SpiralTestResultActivity extends AppCompatActivity {
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test_results);

        mButton = (Button) findViewById(R.id.share_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();

                String resultImage = i.getStringExtra(SpiralTestActivity.RESULT_IMAGE_URI);
                if(resultImage != null){
                    Uri imageUri = Uri.parse(resultImage);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, imageUri);
                    startActivity(Intent.createChooser(share, "Share Results"));
                }
            }
        });
    }
}
