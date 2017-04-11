package com.ms.tests.sway;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ms.tests.R;

public class SwayTestConfig extends AppCompatActivity {
    private Button mButton;
    private MediaPlayer beepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sway_test_config);


        mButton = (Button) this.findViewById(R.id.swayButton);
        beepSound = MediaPlayer.create(this, R.raw.beep);

    }

    public void onSwayClick(View view) {
        beepSound.start();
        Intent i = new Intent(SwayTestConfig.this, SwayTestCalibration.class);
        startActivity(i);
    }
}