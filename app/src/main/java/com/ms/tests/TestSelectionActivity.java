package com.ms.tests;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ms.tests.bubble.BubbleTestActivity;
import com.ms.tests.level.LevelTestActivity;
import com.ms.tests.rotation.RotateTestCalibrationActivity;
import com.ms.tests.spiral.SpiralTestActivity;
import com.ms.tests.step.StepLaunchActivity;
import com.ms.tests.sway.SwayTestConfig;
import com.ms.tests.tap.TapConfigActivity;

public class TestSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selection);

        final Button button = (Button) findViewById(R.id.tapButton);
       // button.setOnClickListener(onClick);
    }

    //@Override
    public void onSpiralClick(View v) {
        Intent i = new Intent(TestSelectionActivity.this, SpiralTestActivity.class);
        startActivity(i);
    }

    public void onTapClick(View v) {
        Intent i = new Intent(TestSelectionActivity.this, TapConfigActivity.class);
        startActivity(i);
    }

    public void onLevelClick(View v) {
        Intent i = new Intent(TestSelectionActivity.this, LevelTestActivity.class);
        startActivity(i);
    }

    public void onBubbleClick(View v) {
        Intent i = new Intent(TestSelectionActivity.this, BubbleTestActivity.class);
        startActivity(i);
    }

    public void onRotationClick(View v){
        Intent i = new Intent(TestSelectionActivity.this, RotateTestCalibrationActivity.class);
        startActivity(i);
    }

    public void onSwayClick(View v){
        Intent i = new Intent(TestSelectionActivity.this, SwayTestConfig.class);
        startActivity(i);
    }

    public void onStepClick(View v){
        Intent i = new Intent(this, StepLaunchActivity.class);
        startActivity(i);
    }
}
