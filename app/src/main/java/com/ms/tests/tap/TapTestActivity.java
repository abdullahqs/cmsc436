package com.ms.tests.tap;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ms.tests.R;

public class TapTestActivity extends AppCompatActivity {
    private static final String ROUND_KEY = "TEST_ROUND";
    private static final int TEST_DURATION = 10000;
    private static final int COUNTDOWN_INTERVAL = 1000;

    private CountDownTimer _tappingTest = new CountDownTimer(TEST_DURATION, COUNTDOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            finishTest();
        }
    };

    private RelativeLayout _tapView;
    private TextView _tapCountView;
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

        _tapCountView = (TextView) findViewById(R.id.tap_count_view);
        _tapView = (RelativeLayout) findViewById(R.id.tap_view);

        final Button testArea = (Button) findViewById(R.id.tap_test_area);
        testArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    _tapsCount++;
                    updateText();
                    testArea.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    testArea.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                }
                return false;
            }
        });

        initCountdown();
    }

    void initCountdown() {
        // Handles count down at beginning
        final ImageView countDownBG = (ImageView) findViewById(R.id.tap_test_bg);
        final TextView countDownText = (TextView) findViewById(R.id.tap_test_title);
        new CountDownTimer(4000, COUNTDOWN_INTERVAL) {
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

    private void updateText(){
        _tapCountView.setText("Taps: " + _tapsCount);
    }

    void startTest() {
        _tappingTest.start();
        _tapView.setVisibility(View.VISIBLE);
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
