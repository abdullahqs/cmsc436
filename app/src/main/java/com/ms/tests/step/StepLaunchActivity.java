package com.ms.tests.step;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ms.tests.R;

public class StepLaunchActivity extends AppCompatActivity {
    private Button mButton;
    private TextView mCountdown;
    private TextView mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_launch);

        mButton = (Button) findViewById(R.id.start_step_test);
        mInfo = (TextView) findViewById(R.id.step_info);
        mCountdown = (TextView) findViewById(R.id.step_countdown_view);
    }

    public void onStartTest(View v){
        mButton.setVisibility(View.GONE);
        mInfo.setVisibility(View.GONE);
        mCountdown.setVisibility(View.VISIBLE);
        startCountdown();
    }

    private void startCountdown(){
        CountDownTimer timer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int)((millisUntilFinished) / 1000);
                String time = String.valueOf(timeLeft);
                mCountdown.setText(time);
            }

            @Override
            public void onFinish() {
                Intent i = new Intent(StepLaunchActivity.this, StepTestActivity.class);
                startActivity(i);
            }
        };

        timer.start();
    }
}
