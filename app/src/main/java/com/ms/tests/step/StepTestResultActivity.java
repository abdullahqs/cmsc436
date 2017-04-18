package com.ms.tests.step;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ms.tests.R;

public class StepTestResultActivity extends AppCompatActivity {
    private TextView mStepTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_test_result);

        mStepTime = (TextView) findViewById(R.id.step_length_view);

        Intent i = getIntent();
        double movingVelocity;
        double accumVelocity;

        if(i != null){
            //movingVelocity = i.getDoubleExtra(StepTestActivity.STEP_TEST_MOVING_VELOCITY, 0);
            accumVelocity = i.getDoubleExtra(StepTestActivity.STEP_TEST_ACCUMULATED_VELOCITY, 0);

            mStepTime.setText("Accumulated Average Speed: " + accumVelocity + "m/s");
        }
    }
}
