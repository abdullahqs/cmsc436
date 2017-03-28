package com.ms.tests.bubble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import com.ms.tests.R;

import java.util.Random;

public class BubbleTestActivity extends AppCompatActivity {
    /*private static final int TEST_DURATION = 10000;
    private static final int COUNTDOWN_INTERVAL = 1000;

    private CountDownTimer _bubbleTest = new CountDownTimer(TEST_DURATION, COUNTDOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            finishTest();
        }
    };*/

    private double[] popTimes;
    private int totalBubbles;
    private int poppedBubbles;
    private double startTime;
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

        totalBubbles = 20;
        popTimes = new double[totalBubbles];
        poppedBubbles = 0;
        r = new Random();
        startTime = System.currentTimeMillis();

        final ImageButton bubble  = (ImageButton) findViewById(R.id.bubble);
        bubble.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double currentTime = System.currentTimeMillis();
                popTimes[poppedBubbles] = currentTime - startTime;
                startTime = currentTime;
                poppedBubbles++;

                if(poppedBubbles >= popTimes.length)
                    finishTest();
                else
                    moveBubble(bubble);
            }
        });

        moveBubble(bubble);
    }

    void finishTest() {
        Intent i = new Intent(BubbleTestActivity.this, BubbleTestResultsActivity.class);

        /*DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        BubbleTestResults _testResults = new BubbleTestResults(_popCount, dateFormat.format(Calendar.getInstance().getTime()));
        _testResults.numPops = _popCount;

        i.putExtra(BubbleTestResults.RESULTS_KEY, _testResults);*/

        double averageTime = 0;
        for(double t : popTimes) {
            averageTime += t;
        }

        averageTime = Math.round(((averageTime / popTimes.length ) / 1000.0) * 100.0) / 100.0;

        i.putExtra(BubbleTestResults.RESULTS_KEY, averageTime);

        startActivity(i);
    }

    void moveBubble(ImageButton bubble) {
        int newX = r.nextInt(maxWidth - (3 * bubble.getWidth()));
        int newY = r.nextInt(maxHeight - (3 * bubble.getHeight()));

        bubble.setX(newX);
        bubble.setY(newY);
    }
}
