package com.ms.tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TapTestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test_result);

        TextView handTest = (TextView) findViewById(R.id.handTest);
        TextView numTests = (TextView) findViewById(R.id.numTests);
        TextView averageTaps = (TextView) findViewById(R.id.averageTaps);

        Intent i = getIntent();
        TapTestResults results = (TapTestResults) i.getParcelableExtra("TEST_RESULTS");
        // TESTING
        /*TapTestResults results = new TapTestResults(true, 4);
        results.TestResults[0] = 8;
        results.TestResults[1] = 4;
        results.TestResults[2] = 12;
        results.TestResults[3] = 6;*/

        int totalNumTaps = 0;
        for(int k = 0; k < results.NumTests; k++) {
            totalNumTaps += results.TestResults[k];
        }

        handTest.setText(results.IsLeftHand ? "Left hand test" : "Right hand test" );
        numTests.setText("Ran a total of: " + results.NumTests + " tests.");
        averageTaps.setText("Average of: " + (totalNumTaps / results.NumTests) + " taps per test.");

        final Button button = (Button) findViewById(R.id.restartButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), TestSelectionActivity.class);
                startActivity(i);
            }
        });
    }
}
