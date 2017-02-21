package com.ms.tests.tap;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
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

        _tapView = (RelativeLayout) findViewById(R.id.tap_test_view);


        final Button button  = (Button) findViewById(R.id.tap_test_area);
        final ImageView image = (ImageView) findViewById(R.id.tap_image);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _tapsCount++;

                image.setScaleX(image.getScaleX() * 1.1f < 1 ? image.getScaleX() * 1.1f : 1);
                image.setScaleY(image.getScaleY() * 1.1f < 1 ? image.getScaleY() * 1.1f : 1);

                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(image, "scaleX", 0.25f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(image, "scaleY", 0.25f);

                scaleDownX.setDuration(1000);
                scaleDownY.setDuration(1000);

                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
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
                //((ViewGroup)countDownBG.getParent()).removeView(countDownBG);
                ((ViewGroup)countDownText.getParent()).removeView(countDownText);
                startTest();
            }
        }.start();
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
