package com.ms.tests.bubble;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ms.tests.R;
import com.ms.tests.tap.TapTestResultActivity;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class BubbleTestActivity extends AppCompatActivity {
    private static final int TEST_DURATION = 10000;
    private static final int COUNTDOWN_INTERVAL = 1000;

    private CountDownTimer _bubbleTest = new CountDownTimer(TEST_DURATION, COUNTDOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            finishTest();
        }
    };

    private int _popCount = 0;
    private int maxWidth, maxHeight;
    Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble_test);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxWidth = displayMetrics.widthPixels;
        maxHeight = displayMetrics.heightPixels;

        r = new Random();

        final ImageButton bubble  = (ImageButton) findViewById(R.id.bubble);
        bubble.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _popCount++;

                moveBubble(bubble);
            }
        });

        moveBubble(bubble);

        _bubbleTest.start();
    }

    void finishTest() {
        Intent i = new Intent(BubbleTestActivity.this, BubbleTestResultsActivity.class);

        /*DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        BubbleTestResults _testResults = new BubbleTestResults(_popCount, dateFormat.format(Calendar.getInstance().getTime()));
        _testResults.numPops = _popCount;

        i.putExtra(BubbleTestResults.RESULTS_KEY, _testResults);*/

        i.putExtra(BubbleTestResults.RESULTS_KEY, _popCount);

        startActivity(i);
    }

    void moveBubble(ImageButton bubble) {
        int newX = r.nextInt(maxWidth - (3 * bubble.getWidth()));
        int newY = r.nextInt(maxHeight - (3 * bubble.getHeight()));

        bubble.setX(newX);
        bubble.setY(newY);
    }
}
