package com.ms.tests.level;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.tests.R;
import com.ms.tests.SheetsStrings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cmsc436.tharri16.googlesheetshelper.CMSC436Sheet;

public class LevelTestResultsActivity extends AppCompatActivity implements  CMSC436Sheet.Host {
    private Button mButton;
    private ImageView mResultView;
    private Uri mImageUri;

    private TextView mResultScore;

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
        setContentView(R.layout.activity_level_test_results);

        mButton = (Button) findViewById(R.id.level_results_share_button);
        mResultView = (ImageView) findViewById(R.id.level_results_result_preview);
        mResultScore = (TextView) findViewById(R.id.level_results_score);

        Intent i = getIntent();

        String resultImage = i.getStringExtra(LevelTestActivity.RESULT_IMAGE_URI);
        if(resultImage != null) {
            mImageUri = Uri.parse(resultImage);
            mResultView.setImageURI(mImageUri);
        }

        int calculation = 0;
        int score = i.getIntExtra(LevelTestActivity.RESULT_SCORE, -1);
        if(score != -1){
            calculation = score;
            mResultScore.setText(score + " / 100");
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c.getTime());

        LevelTestResults result = new LevelTestResults(calculation,date);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageUri != null){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, mImageUri);
                    share.setType("image/jpeg");
                    startActivity(Intent.createChooser(share, "Share Results"));
                }
            }
        });

        SheetsStrings ss = new SheetsStrings();

        if (true){
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), ss.getUrl());
            //sheet.writeData(CMSC436Sheet.TestType.RH_LEVEL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.RH_LEVEL, "t15p01", (float)calculation);

            sheet2 = new CMSC436Sheet(this, getString(R.string.app_name), ss.getTeamUrl());
            //sheet2.writeData(CMSC436Sheet.TestType.RH_LEVEL, "t15p01", System.currentTimeMillis());
            sheet2.writeData(CMSC436Sheet.TestType.RH_LEVEL, "t15p01", (float)calculation);
        } else {
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), ss.getUrl());
            //sheet.writeData(CMSC436Sheet.TestType.LH_LEVEL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.LH_LEVEL, "t15p01", (float)calculation);

            sheet2 = new CMSC436Sheet(this, getString(R.string.app_name), ss.getTeamUrl());
            //sheet2.writeData(CMSC436Sheet.TestType.LH_LEVEL, "t15p01", System.currentTimeMillis());
            sheet2.writeData(CMSC436Sheet.TestType.LH_LEVEL, "t15p01", (float)calculation);
        }
    }
}
