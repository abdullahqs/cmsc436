package com.ms.tests;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TapTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test);

        // Handles count down at beginning
        final ImageView countDownBG = (ImageView) findViewById(R.id.tap_test_bg);
        final TextView countDownText = (TextView) findViewById(R.id.tap_test_title);
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                countDownText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                ((ViewGroup)countDownBG.getParent()).removeView(countDownBG);
                ((ViewGroup)countDownText.getParent()).removeView(countDownText);
                // Start test
            }
        }.start();

        ImageView testArea = (ImageView) findViewById(R.id.tap_test_area);
        testArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
