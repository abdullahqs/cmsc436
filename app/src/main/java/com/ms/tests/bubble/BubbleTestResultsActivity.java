package com.ms.tests.bubble;

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
import com.ms.tests.TestSelectionActivity;

import cmsc436.tharri16.googlesheetshelper.CMSC436Sheet;

public class BubbleTestResultsActivity extends AppCompatActivity implements CMSC436Sheet.Host {

    private static final int LIB_ACCOUNT_NAME_REQUEST_CODE = 1001;
    private static final int LIB_AUTHORIZATION_REQUEST_CODE = 1002;
    private static final int LIB_PERMISSION_REQUEST_CODE = 1003;
    private static final int LIB_PLAY_SERVICES_REQUEST_CODE = 1004;

    private CMSC436Sheet sheet;

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        sheet.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sheet.onActivityResult(requestCode, resultCode, data);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble_test_results);

        TextView bubblePops = (TextView) findViewById(R.id.bubble_results_pops);

        Intent i = getIntent();
        /*BubbleTestResults results = (BubbleTestResults) (i.getParcelableExtra(BubbleTestResults.RESULTS_KEY));

        bubblePops.setText("You popped " + results.numPops + " balloons!");
        final String shareString = "Popped " + results.numPops + " balloons!";*/

        double averagePopTime = i.getDoubleExtra(BubbleTestResults.RESULTS_KEY, 0);
        bubblePops.setText("Your average pop time is " + averagePopTime + " seconds!");
        final String shareString = "Average pop time:  " + averagePopTime + " seconds!";

        final Button shareButton = (Button) findViewById(R.id.bubble_results_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
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

        final Button button = (Button) findViewById(R.id.bubble_results_restart_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), TestSelectionActivity.class);
                startActivity(i);
            }
        });

        if (true){
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), "1ooKJktuWc0N9SFUkcI8GlgkoRQLGP6mqOwt2TKmOIDo");
            sheet.writeData(CMSC436Sheet.TestType.RH_CURL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.RH_CURL, "t15p01", (float)averagePopTime);
        } else {
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), "1ooKJktuWc0N9SFUkcI8GlgkoRQLGP6mqOwt2TKmOIDo");
            sheet.writeData(CMSC436Sheet.TestType.LH_CURL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.LH_CURL, "t15p01", (float)averagePopTime);
        }
    }
}
