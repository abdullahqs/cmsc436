package com.ms.tests.tap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ms.tests.R;
import com.ms.tests.SheetsStrings;
import com.ms.tests.TestSelectionActivity;

import cmsc436.tharri16.googlesheetshelper.CMSC436Sheet;

public class TapTestResultActivity extends AppCompatActivity implements CMSC436Sheet.Host {

    private static final int LIB_ACCOUNT_NAME_REQUEST_CODE = 1001;
    private static final int LIB_AUTHORIZATION_REQUEST_CODE = 1002;
    private static final int LIB_PERMISSION_REQUEST_CODE = 1003;
    private static final int LIB_PLAY_SERVICES_REQUEST_CODE = 1004;

    private CMSC436Sheet sheet, sheet2;

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        sheet.onRequestPermissionsResult(requestCode, permissions, grantResults);
        sheet2.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sheet.onActivityResult(requestCode, resultCode, data);
        sheet2.onActivityResult(requestCode, resultCode, data);
    }

    public int getRequestCode(CMSC436Sheet.Action action) {
        switch (action) {
            case REQUEST_ACCOUNT_NAME:
                return LIB_ACCOUNT_NAME_REQUEST_CODE;
            case REQUEST_AUTHORIZATION:
                return LIB_AUTHORIZATION_REQUEST_CODE;
            case REQUEST_PERMISSIONS:
                return LIB_PERMISSION_REQUEST_CODE;
            case REQUEST_PLAY_SERVICES:
                return LIB_PLAY_SERVICES_REQUEST_CODE;
            default:
                return -1; // boo java doesn't know we exhausted the enum
        }
    }

    public Activity getActivity() {
        return this;
    }

    public void notifyFinished(Exception e) {
        if (e != null) {
            throw new RuntimeException(e); // just to see the exception easily in logcat
        }

        Log.i(getClass().getSimpleName(), "Done");
    }

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
        int answer = (int) (results.getValue());
        handTest.setText(results.isLeftHand ? "Left hand test" : "Right hand test" );
        numTests.setText("Ran a total of: " + results.numTests + " test" + (results.numTests > 1 ? "s" : "") + ".");
        averageTaps.setText("Average of: " + answer + " taps per test.");

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

        SheetsStrings ss = new SheetsStrings();

        if (results.isLeftHand){
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), ss.getUrl());
            //sheet.writeData(CMSC436Sheet.TestType.LH_TAP, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.LH_TAP, "t15p01", answer);

            sheet2 = new CMSC436Sheet(this, getString(R.string.app_name), ss.getTeamUrl());
            //sheet2.writeData(CMSC436Sheet.TestType.LH_TAP, "t15p01", System.currentTimeMillis());
            sheet2.writeData(CMSC436Sheet.TestType.LH_TAP, "t15p01", answer);
        } else {
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), ss.getUrl());
            //sheet.writeData(CMSC436Sheet.TestType.RH_TAP, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.RH_TAP, "t15p01", answer);

            sheet2 = new CMSC436Sheet(this, getString(R.string.app_name), ss.getTeamUrl());
            //sheet2.writeData(CMSC436Sheet.TestType.RH_TAP, "t15p01", System.currentTimeMillis());
            sheet2.writeData(CMSC436Sheet.TestType.RH_TAP, "t15p01", answer);
        }

    }
}
