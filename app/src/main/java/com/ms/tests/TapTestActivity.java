package com.ms.tests;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TapTestActivity extends AppCompatActivity {
    private static final String ROUND_KEY = "TEST_ROUND";
    private static final int TEST_START = 10000;
    private static final int TEST_END = 0;

    private CountDownTimer _tappingTest = new CountDownTimer(TEST_START, TEST_END) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            finishTest();
        }
    };

    private TapTestResults _testResults;
    private int _testRound;
    private int _tapsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test);

        Intent i = getIntent();
        _testResults = i.getParcelableExtra(TapTestResults.RESULTS_KEY);
        _testRound = i.getIntExtra(ROUND_KEY, 1);

        ImageView testArea = (ImageView) findViewById(R.id.tap_test_area);
        testArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _tapsCount++;
            }
        });

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
        _tappingTest.start();
    }

    void finishTest() {
        _testResults.testResults[_testRound - 1] = _tapsCount;
        _testRound += 1;

        Intent i;
        if (_testRound <= _testResults.numTests) {
            i = new Intent(getBaseContext(), TapTestActivity.class);
            i.putExtra(ROUND_KEY, _testRound);
        } else {
            i = new Intent(getBaseContext(), TapTestResultActivity.class);
        }

        i.putExtra(TapTestResults.RESULTS_KEY, _testResults);

        startActivity(i);
    }
}
