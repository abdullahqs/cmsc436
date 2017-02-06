package com.ms.tests;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TapTestActivity extends AppCompatActivity {
    private TapTestResults _testResults;
    private int _testRound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test);

        Intent i = getIntent();
        _testResults = (TapTestResults)i.getParcelableExtra("TEST_RESULTS");
        _testRound = i.getIntExtra("TEST_ROUND", 1);

        initCountdown();
    }

    void initCountdown() {
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
                startTest();
            }
        }.start();
    }

    void startTest() {
        ImageView testArea = (ImageView) findViewById(R.id.tap_test_area);
        testArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (true) {
                    finishTest();
                }
            }
        });
    }

    void finishTest() {
        int results = 0;
        _testResults.TestResults[_testRound - 1] = results;

        _testRound += 1;

        Intent i;
        if (_testRound <= _testResults.NumTests) {
            i = new Intent(getBaseContext(), TapTestActivity.class);
            i.putExtra("TEST_ROUND", _testRound);
        } else {
            i = new Intent(getBaseContext(), TapTestResultActivity.class);
        }
        i.putExtra("TEST_RESULTS", _testResults);

        startActivity(i);
    }
}
