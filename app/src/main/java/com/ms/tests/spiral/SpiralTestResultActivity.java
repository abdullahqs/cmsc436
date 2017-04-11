package com.ms.tests.spiral;

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

public class SpiralTestResultActivity extends AppCompatActivity implements CMSC436Sheet.Host {
    private Button mButton;
    private ImageView mResultView;
    private Uri mImageUri;
    private TextView mTextView;
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
        setContentView(R.layout.activity_spiral_test_results);

        mButton = (Button) findViewById(R.id.spiral_results_share_button);
        mResultView = (ImageView) findViewById(R.id.spiral_results_result_preview);
        mTextView = (TextView) findViewById(R.id.score);


        Intent i = getIntent();

        Double score = i.getDoubleExtra("SpiralMetric",0);
        int scoreInt = score.intValue();
        final String scoreString = Integer.toString(scoreInt);
        mTextView.setText(scoreString);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c.getTime());

        SpiralTestResults result = new SpiralTestResults(scoreInt,date);


        String resultImage = i.getStringExtra(SpiralTestActivity.RESULT_IMAGE_URI);
        if(resultImage != null) {
            mImageUri = Uri.parse(resultImage);
            mResultView.setImageURI(mImageUri);

        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageUri != null){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, mImageUri);
                    share.putExtra(Intent.EXTRA_TEXT,"I scored "+scoreString+" out of 100.");
                    share.setType("image/jpeg");
                    startActivity(Intent.createChooser(share, "Share Results"));
                }
            }
        });

        SheetsStrings ss = new SheetsStrings();

        // We need to check to see which hand it is from.
        if (true){
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), ss.getUrl());
            //sheet.writeData(CMSC436Sheet.TestType.RH_SPIRAL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.RH_SPIRAL, "t15p01", scoreInt);

            sheet2 = new CMSC436Sheet(this, getString(R.string.app_name), ss.getTeamUrl());
            //sheet2.writeData(CMSC436Sheet.TestType.RH_SPIRAL, "t15p01", System.currentTimeMillis());
            sheet2.writeData(CMSC436Sheet.TestType.RH_SPIRAL, "t15p01", scoreInt);
        } else {
            sheet = new CMSC436Sheet(this, getString(R.string.app_name), ss.getUrl());
            //sheet.writeData(CMSC436Sheet.TestType.LH_SPIRAL, "t15p01", System.currentTimeMillis());
            sheet.writeData(CMSC436Sheet.TestType.LH_SPIRAL, "t15p01", scoreInt);

            sheet2 = new CMSC436Sheet(this, getString(R.string.app_name), ss.getTeamUrl());
            //sheet2.writeData(CMSC436Sheet.TestType.LH_SPIRAL, "t15p01", System.currentTimeMillis());
            sheet2.writeData(CMSC436Sheet.TestType.LH_SPIRAL, "t15p01", scoreInt);
        }

    }
}
