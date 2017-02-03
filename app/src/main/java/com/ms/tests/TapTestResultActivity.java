package com.ms.tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TapTestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_test_result);

        Intent i = getIntent();
        // PRODUCTION - TapTestResults results = (TapTestResults) i.getParcelableExtra("TEST_RESULTS");
        // TESTING
        TapTestResults results = new TapTestResults(true, 4);
        results.TestResults[0] = 8;
        results.TestResults[1] = 4;
        results.TestResults[2] = 12;
        results.TestResults[3] = 6;
    }
}
