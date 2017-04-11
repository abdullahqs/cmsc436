package com.ms.tests.rotation;

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


public class RotateResultActivity extends AppCompatActivity implements CMSC436Sheet.Host {

    TextView display;
    Button mBack;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_result);

        Intent i = getIntent();
        double[] out = i.getDoubleArrayExtra(RotateTestResults.RESULT_KEY);
        double average = 0;
        for(int index = 0;index<out.length;index++){
            average += out[index];
        }
        double len = out.length;
        average = average/len;
        String avg_str = "On average, it took: "+average + " seconds to complete each rep.";
        display = (TextView) findViewById(R.id.output);
        display.setText(avg_str);

        mBack = (Button)  findViewById(R.id.testselection);

        mBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(RotateResultActivity.this, TestSelectionActivity.class);
                startActivity(i);
            }
        });

        if (true){
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), "1ooKJktuWc0N9SFUkcI8GlgkoRQLGP6mqOwt2TKmOIDo");
            sheet.writeData(CMSC436Sheet.TestType.RH_CURL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.RH_CURL, "t15p01", (float)average);
        } else {
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), "1ooKJktuWc0N9SFUkcI8GlgkoRQLGP6mqOwt2TKmOIDo");
            sheet.writeData(CMSC436Sheet.TestType.LH_CURL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.LH_CURL, "t15p01", (float)average);
        }

    }
}
