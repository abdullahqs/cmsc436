package com.ms.tests.tap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ms.tests.R;
import com.ms.tests.TestSelectionActivity;

public class TapTestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test_result);

        TextView handTest = (TextView) findViewById(R.id.handTest);
        TextView numTests = (TextView) findViewById(R.id.numTests);
        TextView averageTaps = (TextView) findViewById(R.id.averageTaps);

        Intent i = getIntent();
        TapTestResults results = i.getParcelableExtra(TapTestResults.RESULTS_KEY);

        int totalNumTaps = 0;
        for(int k = 0; k < results.numTests; k++) {
            totalNumTaps += results.testResults[k];
        }

        handTest.setText(results.isLeftHand ? "Left hand test" : "Right hand test" );
        numTests.setText("Ran a total of: " + results.numTests + " test" + (results.numTests > 1 ? "s" : "") + ".");
        averageTaps.setText("Average of: " + (totalNumTaps / results.numTests) + " taps per test.");

        final Button button = (Button) findViewById(R.id.restartButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), TestSelectionActivity.class);
                startActivity(i);
            }
        });
    }
}
