package com.ms.tests.rotation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ms.tests.R;

import org.w3c.dom.Text;


public class RotateResultActivity extends AppCompatActivity {

    TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_result);

        Intent i = getIntent();
        String temp = i.getStringExtra(RotateTestResults.KEY);
        int[] out = i.getIntArrayExtra(RotateTestResults.RESULT_KEY);
        int average = 0;
        for(int index = 0;index<out.length;index++){
            average += out[index];
        }
        average = average/out.length;
        String avg_str = ""+average;
        display = (TextView) findViewById(R.id.output);
        display.setText(avg_str);



    }
}
