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
        double testTime;
        int numSteps;

        if(i != null){
            testTime = i.getDoubleExtra(StepTestActivity.STEP_TEST_LENGTH, 0);
            numSteps = i.getIntExtra(StepTestActivity.STEP_TEST_TARGET, 0);

            mStepTime.setText("You walked a total of " + numSteps + " steps in " + testTime + " seconds.");
        }
    }
}
