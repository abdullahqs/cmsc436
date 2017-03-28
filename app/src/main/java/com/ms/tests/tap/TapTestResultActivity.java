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

        TextView handTest = (TextView) findViewById(R.id.tap_results_hand_test);
        TextView numTests = (TextView) findViewById(R.id.tap_results_num_tests);
        TextView averageTaps = (TextView) findViewById(R.id.tap_results_average_taps);

        Button mButton = (Button) findViewById(R.id.tap_results_share_button);

        Intent i = getIntent();
        TapTestResults results = i.getParcelableExtra(TapTestResults.RESULTS_KEY);

        int totalNumTaps = 0;
        for(int k = 0; k < results.numTests; k++) {
            totalNumTaps += results.testResults[k];
        }

        results.setValue(totalNumTaps/results.numTests);

        handTest.setText(results.isLeftHand ? "Left hand test" : "Right hand test" );
        numTests.setText("Ran a total of: " + results.numTests + " test" + (results.numTests > 1 ? "s" : "") + ".");
        averageTaps.setText("Average of: " + (results.getValue()) + " taps per test.");

        final String shareString = ((results.isLeftHand ? "Left Hand Test\n" : "Right Hand Test\n") + "Ran a total of " + results.numTests + " test\n" + (results.numTests > 1 ? "s" : "") + ".\n" + "Average of: " + (totalNumTaps / results.numTests) + " taps per test.");

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareString != null) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_TEXT, shareString);
                    share.setType("text/plain");
                    startActivity(Intent.createChooser(share, "Share Results"));

                }
            }
        });



        final Button button = (Button) findViewById(R.id.tap_results_restart_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), TestSelectionActivity.class);
                startActivity(i);
            }
        });
    }
}
